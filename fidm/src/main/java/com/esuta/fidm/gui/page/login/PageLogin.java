package com.esuta.fidm.gui.page.login;

import com.esuta.fidm.gui.component.CustomFeedbackPanel;
import com.esuta.fidm.gui.component.WebMiscUtil;
import com.esuta.fidm.gui.component.behavior.VisibleEnableBehavior;
import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.dashboard.PageDashboard;
import com.esuta.fidm.gui.page.login.dto.LoginDto;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.model.IModelService;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.model.ProvisioningService;
import com.esuta.fidm.model.auth.AuthResult;
import com.esuta.fidm.model.auth.AuthService;
import com.esuta.fidm.model.auth.IAuthService;
import com.esuta.fidm.model.IProvisioningService;
import com.esuta.fidm.model.federation.client.AuthRestResponse;
import com.esuta.fidm.model.federation.client.GenericListRestResponse;
import com.esuta.fidm.model.federation.client.RestFederationServiceClient;
import com.esuta.fidm.repository.schema.core.FederationMemberType;
import com.esuta.fidm.repository.schema.core.ResourceType;
import com.esuta.fidm.repository.schema.core.UserType;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.eclipse.jetty.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 * */
public class PageLogin extends WebPage {

    private static final Logger LOGGER = Logger.getLogger(PageLogin.class);

    //ModelService instance - used to retrieve a list of accessible resources
    private transient IModelService modelService;

    //Authentication service instance - used to authenticate the user trying to log to
    //the system, or to one accounts on connected resources
    private transient IAuthService authService;

    //Provisioning service - needed when user tries to log in to an account
    private transient IProvisioningService provisioningService;

    //Federation Service client - handling login to remote resource
    private transient RestFederationServiceClient federationServiceClient;

    private static final String ID_LOGIN_FORM = "loginForm";
    private static final String ID_FEEDBACK_CONTAINER = "feedbackContainer";
    private static final String ID_FEEDBACK = "feedback";
    private static final String ID_NAME = "name";
    private static final String ID_PASSWORD = "password";
    private static final String ID_LOGIN_TO_RESOURCE = "logToResource";
    private static final String ID_LOGIN_TO_REMOTE_RESOURCE = "logToRemoteResource";
    private static final String ID_RESOURCE_CONTAINER = "resourceContainer";
    private static final String ID_RESOURCE = "resource";
    private static final String ID_REMOTE_LOGIN_CONTAINER = "remoteLoginContainer";
    private static final String ID_FEDERATION_MEMBER = "federationMember";
    private static final String ID_REMOTE_RESOURCE_CONTAINER = "remoteResourceContainer";
    private static final String ID_REMOTE_RESOURCE = "remoteResource";
    private static final String ID_BUTTON_LOGIN = "login";

    private IModel<LoginDto> loginModel;

    public PageLogin(){
        modelService = ModelService.getInstance();
        authService = AuthService.getInstance();
        provisioningService = ProvisioningService.getInstance();
        federationServiceClient = RestFederationServiceClient.getInstance();

        loginModel = new LoadableModel<LoginDto>(false) {

            @Override
            protected LoginDto load() {
                return new LoginDto();
            }
        };

        initLayout();
    }

    private void initLayout(){
        WebMarkupContainer feedbackContainer = new WebMarkupContainer(ID_FEEDBACK_CONTAINER);
        feedbackContainer.setOutputMarkupId(true);
        add(feedbackContainer);

        final FeedbackPanel feedback = new CustomFeedbackPanel(ID_FEEDBACK);
        feedback.setOutputMarkupId(true);
        feedbackContainer.add(feedback);

        Form loginForm = new Form(ID_LOGIN_FORM);
        loginForm.setOutputMarkupId(true);
        add(loginForm);

        TextField name = new TextField<>(ID_NAME, new PropertyModel<String>(loginModel, LoginDto.F_NAME));
        loginForm.add(name);

        PasswordTextField password = new PasswordTextField(ID_PASSWORD, new PropertyModel<String>(loginModel, LoginDto.F_PASSWORD));
        loginForm.add(password);

        CheckBox loginToResource = new CheckBox(ID_LOGIN_TO_RESOURCE, new PropertyModel<Boolean>(loginModel, LoginDto.F_LOGIN_TO_RESOURCE));
        loginToResource.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                loginModel.getObject().setLoginToRemoteResource(false);
                target.add(get(ID_LOGIN_FORM + ":" + ID_LOGIN_TO_REMOTE_RESOURCE));
                target.add(get(ID_LOGIN_FORM + ":" + ID_RESOURCE_CONTAINER));
                target.add(get(ID_LOGIN_FORM + ":" + ID_REMOTE_LOGIN_CONTAINER));
            }
        });
        loginForm.add(loginToResource);

        CheckBox loginToRemoteResource = new CheckBox(ID_LOGIN_TO_REMOTE_RESOURCE, new PropertyModel<Boolean>(loginModel, LoginDto.F_LOGIN_TO_REMOTE_RESOURCE));
        loginToRemoteResource.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                loginModel.getObject().setLoginToResource(false);
                target.add(get(ID_LOGIN_FORM + ":" + ID_LOGIN_TO_RESOURCE));
                target.add(get(ID_LOGIN_FORM + ":" + ID_RESOURCE_CONTAINER));
                target.add(get(ID_LOGIN_FORM + ":" + ID_REMOTE_LOGIN_CONTAINER));
            }
        });
        loginForm.add(loginToRemoteResource);

        //Resource container
        final WebMarkupContainer resourceContainer = new WebMarkupContainer(ID_RESOURCE_CONTAINER);
        resourceContainer.setOutputMarkupId(true);
        resourceContainer.setOutputMarkupPlaceholderTag(true);
        resourceContainer.add(new VisibleEnableBehavior() {

            @Override
            public boolean isVisible() {
                return loginModel.getObject().isLoginToResource();
            }
        });
        loginForm.add(resourceContainer);

        DropDownChoice resource = new DropDownChoice<>(ID_RESOURCE, new PropertyModel<String>(loginModel, LoginDto.F_RESOURCE_NAME),
            new AbstractReadOnlyModel<List<String>>() {

                @Override
                public List<String> getObject() {
                    return prepareResourceList();
                }
            }
        );
        resourceContainer.add(resource);

        //Remote LOGIN container
        final WebMarkupContainer remoteLoginContainer = new WebMarkupContainer(ID_REMOTE_LOGIN_CONTAINER);
        remoteLoginContainer.setOutputMarkupId(true);
        remoteLoginContainer.setOutputMarkupPlaceholderTag(true);
        remoteLoginContainer.add(new VisibleEnableBehavior() {

            @Override
            public boolean isVisible() {
                return loginModel.getObject().isLoginToRemoteResource();
            }
        });
        loginForm.add(remoteLoginContainer);

        DropDownChoice federationMember = new DropDownChoice<>(ID_FEDERATION_MEMBER, new PropertyModel<String>(loginModel, LoginDto.F_FEDERATION_MEMBER_NAME),
            new AbstractReadOnlyModel<List<String>>() {

                @Override
                public List<String> getObject() {
                    return prepareFederationMemberList();
                }
            }
        );
        federationMember.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                federationMemberSelectionPerformed(target);
            }
        });
        remoteLoginContainer.add(federationMember);

        WebMarkupContainer remoteResourceContainer = new WebMarkupContainer(ID_REMOTE_RESOURCE_CONTAINER);
        remoteResourceContainer.setOutputMarkupId(true);
        remoteResourceContainer.setOutputMarkupPlaceholderTag(true);
        remoteResourceContainer.add(new VisibleEnableBehavior(){

            @Override
            public boolean isVisible() {
                return loginModel.getObject().getFederationMemberName() != null;
            }
        });
        remoteLoginContainer.add(remoteResourceContainer);

        DropDownChoice remoteResources = new DropDownChoice<>(ID_REMOTE_RESOURCE, new PropertyModel<String>(loginModel, LoginDto.F_REMOTE_RESOURCE_NAME),
            new AbstractReadOnlyModel<List<String>>() {

                @Override
                public List<String> getObject() {
                    return loginModel.getObject().getRemoteResourceNameList();
                }
            }
        );
        remoteResourceContainer.add(remoteResources);

        AjaxSubmitLink login = new AjaxSubmitLink(ID_BUTTON_LOGIN) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                loginPerformed(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                error("Could not login. Form validation problem");
                target.add(getFeedbackPanel());
            }
        };
        loginForm.add(login);

        info("If you don't have an account, please use default account: 'Administrator' with password: '123456'.");
    }

    private WebMarkupContainer getFeedbackPanel(){
        return (WebMarkupContainer) get(ID_FEEDBACK_CONTAINER);
    }

    private List<String> prepareResourceList(){
        List<String> resourceNames = new ArrayList<>();

        try {
            List<ResourceType> resources = modelService.getAllObjectsOfType(ResourceType.class);

            for(ResourceType resource: resources){
                resourceNames.add(resource.getName());
            }

        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Could not load a list of available resources. Internal server error.");
            error("Could not load a list of available resources. Internal server error.");
            throw new RestartResponseException(PageLogin.class);
        }

        return resourceNames;
    }

    private List<String> prepareFederationMemberList(){
        List<String> members = new ArrayList<>();

        try {
            List<FederationMemberType> federationMembers = modelService.getAllObjectsOfType(FederationMemberType.class);

            for(FederationMemberType member: federationMembers){
                members.add(member.getFederationMemberName());
            }

        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Could not load list of available federation members. Reason: ", e);
            error("Could not load list of available federation members. Reason: " + e);
        }

        return members;
    }

    private void federationMemberSelectionPerformed(AjaxRequestTarget target){
        String memberName = loginModel.getObject().getFederationMemberName();
        FederationMemberType member = WebMiscUtil.getFederationMemberByName(memberName);

        if(member == null){
            LOGGER.error("Invalid federation member. Please select new one.");
            error("Invalid federation member. Please select new one.");
        }

        try {
            GenericListRestResponse<String> response = federationServiceClient.createGetAvailableResourcesRequest(member);

            if(HttpStatus.OK_200 == response.getStatus()){
                List<String> availableResources = response.getValues();
                loginModel.getObject().getRemoteResourceNameList().clear();
                loginModel.getObject().getRemoteResourceNameList().addAll(availableResources);
                target.add(get(ID_LOGIN_FORM + ":" + ID_REMOTE_LOGIN_CONTAINER + ":" + ID_REMOTE_RESOURCE_CONTAINER));

            } else {
                LOGGER.error("Could not retrieve list of available remote resources for federation member: '" +
                        memberName + "'. Reason: " + response.getMessage());
                error("Could not retrieve list of available remote resources for federation member: '" +
                        memberName + "'. Reason: " + response.getMessage());
            }

        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Could not retrieve list of available remote resources for federation member: '" + memberName + "'.");
            error("Could not retrieve list of available remote resources for federation member: '" + memberName + "'.");
        }

        target.add(getFeedbackPanel());
    }

    private void loginPerformed(AjaxRequestTarget target){
        LoginDto dto = loginModel.getObject();

        String name = dto.getName();
        String password = dto.getPassword();

        if(name == null || password == null ||
                StringUtils.isEmpty(name) || StringUtils.isEmpty(password)){
            error("Name or password fields are empty. Specify these fields to login. " +
                    "Or use default account 'Administrator', with password '123456'.");
            target.add(getFeedbackPanel());
            return;
        }

        try {
            AuthResult result;

            if(dto.isLoginToResource()){
                String resourceName = dto.getResourceName();

                if(resourceName == null || StringUtils.isEmpty(resourceName)){
                    error("You must specify a resource to log to.");
                    target.add(getFeedbackPanel());
                    return;
                }

                provisioningService.checkJitProvisioningList(modelService.readObjectByName(UserType.class, name),
                        modelService.readObjectByName(ResourceType.class, resourceName));
                result = authService.loginToResource(name, password, resourceName);
            } else if(dto.isLoginToRemoteResource()){
                FederationMemberType member = WebMiscUtil.getFederationMemberByName(dto.getFederationMemberName());

                AuthRestResponse response = federationServiceClient.createGetRemoteLoginRequest(member,
                        dto.getResourceName(), name, password);

                if(HttpStatus.OK_200 == response.getStatus()){
                    result = response.getResult();
                } else {
                    error("Could not login to remote resource. Please try again later. Reason: " + response.getMessage());
                    LOGGER.error("Could not login to remote resource. Please try again later. Reason: " + response.getMessage());
                    target.add(getFeedbackPanel());
                    return;
                }

            } else {
                result = authService.login(name, password);
            }

            switch (result){
                case SUCCESS:
                    LOGGER.info("User: '" + name + "' logged to the system.");
                    setResponsePage(PageDashboard.class);
                    break;
                case NO_ACCOUNT:
                    error("Specified account does not exist.");
                    break;
                case BAD_PASSWORD:
                    error("Provided password does not seem to be correct.");
                    break;
                case NO_ACCOUNT_ON_RESOURCE:
                    error("No resource for this user on selected resource.");
                    break;
                default:
                    error("Illegal authentication process result.");
                    break;
            }

        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Can't perform login operation. Internal server error.");
            error("Can't perform login operation. Internal server error.");
        }

        target.add(getFeedbackPanel());
    }
}
