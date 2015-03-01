package com.esuta.fidm.gui.page.federation;

import com.esuta.fidm.gui.component.behavior.VisibleEnableBehavior;
import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.GeneralException;
import com.esuta.fidm.infra.exception.ObjectNotFoundException;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.model.federation.service.FederationMembershipRequest;
import com.esuta.fidm.model.federation.client.SimpleRestResponseStatus;
import com.esuta.fidm.repository.schema.core.FederationMemberType;
import com.esuta.fidm.repository.schema.core.SystemConfigurationType;
import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.eclipse.jetty.http.HttpStatus;

import java.io.IOException;

/**
 *  @author shood
 * */
public class PageFederation extends PageBase {

    private transient Logger LOGGER = Logger.getLogger(PageFederation.class);

    private static final String ID_MAIN_FORM = "mainForm";
    private static final String ID_FEDERATION_IDENTIFIER = "federationIdentifier";
    private static final String ID_NAME = "name";
    private static final String ID_DISPLAY_NAME = "displayName";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_WEB_ADDRESS = "webAddress";
    private static final String ID_PORT = "port";
    private static final String ID_LOCALITY = "locality";

    private static final String ID_BUTTON_SAVE = "saveButton";
    private static final String ID_BUTTON_CANCEL = "cancelButton";
    private static final String ID_BUTTON_ACCEPT = "acceptButton";
    private static final String ID_BUTTON_REJECT = "rejectButton";

    private IModel<FederationMemberType> model;

    public PageFederation(){
        this(null);
    }

    public PageFederation(PageParameters parameters){
        super(parameters);

        model = new LoadableModel<FederationMemberType>(false) {

            @Override
            protected FederationMemberType load() {
                return loadFederationMember();
            }
        };

        initLayout();
    }

    private FederationMemberType loadFederationMember(){
        if(!isEditingFederationMember()){
            return new FederationMemberType();
        }

        PageParameters parameters = getPageParameters();
        String uid = parameters.get(UID_PAGE_PARAMETER_NAME).toString();
        FederationMemberType federationMember;

        try {
            federationMember = getModelService().readObject(FederationMemberType.class, uid);
        } catch (DatabaseCommunicationException exc){
            error("Couldn't retrieve federation member with uid: '" + uid + "' from the repository. Reason: " + exc.getExceptionMessage());
            LOGGER.error("Couldn't retrieve federation member with oid: '" + uid + "' from the repository. Reason: ", exc);
            throw new RestartResponseException(PageFederationList.class);
        }

        return federationMember;
    }

    private boolean isEditingFederationMember(){
        PageParameters parameters = getPageParameters();
        return !parameters.get(UID_PAGE_PARAMETER_NAME).isEmpty();
    }

    private void initLayout(){
        Form mainForm = new Form(ID_MAIN_FORM);
        mainForm.setOutputMarkupId(true);
        add(mainForm);

        Label identifier = new Label(ID_FEDERATION_IDENTIFIER, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                String identifier = model.getObject().getFederationMemberName();

                if(identifier == null || identifier.isEmpty()){
                    return "Not specified yet";
                }

                return model.getObject().getFederationMemberName();
            }
        });
        mainForm.add(identifier);

        TextField name = new TextField<>(ID_NAME, new PropertyModel<String>(model, "name"));
        name.setRequired(true);
        mainForm.add(name);

        TextField displayName = new TextField<>(ID_DISPLAY_NAME, new PropertyModel<String>(model, "displayName"));
        displayName.setRequired(true);
        mainForm.add(displayName);

        TextArea description = new TextArea<>(ID_DESCRIPTION, new PropertyModel<String>(model, "description"));
        mainForm.add(description);

        TextField locality = new TextField<>(ID_LOCALITY, new PropertyModel<String>(model, "locality"));
        mainForm.add(locality);

        TextField webAddress = new TextField<>(ID_WEB_ADDRESS, new PropertyModel<String>(model, "webAddress"));
        webAddress.setRequired(true);
        webAddress.add(new VisibleEnableBehavior(){

            @Override
            public boolean isEnabled() {
                return !isEditingFederationMember();
            }
        });
        mainForm.add(webAddress);

        TextField port = new TextField<>(ID_PORT, new PropertyModel<String>(model, "port"));
        port.setRequired(true);
        port.add(new VisibleEnableBehavior() {

            @Override
            public boolean isEnabled() {
                return !isEditingFederationMember();
            }
        });
        mainForm.add(port);

        AjaxSubmitLink cancel = new AjaxSubmitLink(ID_BUTTON_CANCEL) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                cancelPerformed();
            }
        };
        cancel.setDefaultFormProcessing(false);
        mainForm.add(cancel);

        AjaxSubmitLink save = new AjaxSubmitLink(ID_BUTTON_SAVE) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                savePerformed(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(getFeedbackPanel());
            }
        };
        mainForm.add(save);

        AjaxSubmitLink accept = new AjaxSubmitLink(ID_BUTTON_ACCEPT) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                acceptPerformed(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(getFeedbackPanel());
            }
        };
        accept.add(new VisibleEnableBehavior(){

            @Override
            public boolean isEnabled() {
                return isAcceptRejectEnabled();
            }
        });
        mainForm.add(accept);

        AjaxSubmitLink reject = new AjaxSubmitLink(ID_BUTTON_REJECT) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                rejectPerformed(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(getFeedbackPanel());
            }
        };
        reject.add(new VisibleEnableBehavior(){

            @Override
            public boolean isEnabled() {
                return isAcceptRejectEnabled();
            }
        });
        mainForm.add(reject);
    }

    private boolean isAcceptRejectEnabled(){
        if(!isEditingFederationMember()){
            return false;
        }

        if(model.getObject().getRequesterIdentifier().equals(loadSystemConfiguration().getIdentityProviderIdentifier())){
            return false;
        }

        FederationMemberType.FederationMemberStatusType status = model.getObject().getStatus();
        return FederationMemberType.FederationMemberStatusType.REQUESTED.equals(status);
    }

    private void cancelPerformed(){
        setResponsePage(PageFederationList.class);
    }

    private void acceptPerformed(AjaxRequestTarget target){
        acceptRejectOperation(target, FederationMembershipRequest.Response.ACCEPT);
    }

    private void rejectPerformed(AjaxRequestTarget target){
        acceptRejectOperation(target, FederationMembershipRequest.Response.DENY);
    }

    private void acceptRejectOperation(AjaxRequestTarget target, FederationMembershipRequest.Response response){
        if(model == null || model.getObject() == null){
            return;
        }

        FederationMemberType member = model.getObject();

        try {
            SimpleRestResponseStatus status = getFederationServiceClient().createFederationResponse(member, response);

            if(HttpStatus.OK_200 == status.getStatus()){
                if(FederationMembershipRequest.Response.ACCEPT.equals(response)){
                    member.setStatus(FederationMemberType.FederationMemberStatusType.AVAILABLE);
                } else {
                    member.setStatus(FederationMemberType.FederationMemberStatusType.DENIED);
                }

                getModelService().updateObject(member);
                LOGGER.info("Federation membership response was correct.");
                getSession().info("Federation membership response was correct.");

            } else {
                LOGGER.error("Federation membership request not successful. Reason: " + status.getMessage());
                getSession().error("Federation membership request not successful. Reason: " + status.getMessage());
            }

        } catch (IOException e) {
            LOGGER.error("Could not create a REST request to ACCEPT/DENY federation member. Federation member: '"
                    + member.getFederationMemberName() + "'. Reason: ", e);

            getSession().error("Could not create a REST request to ACCEPT/DENY federation member. Federation member: '"
                    + member.getFederationMemberName() + "'. Reason: " + e);
        } catch (ObjectNotFoundException | DatabaseCommunicationException e) {
            LOGGER.error("Could not update federation member: '" + member.getFederationMemberName() + "'(" + member.getUid() + ").", e);
            getSession().error("Could not update federation member: '" + member.getFederationMemberName() + "'(" + member.getUid() + ")." + e);
        }

        setResponsePage(PageFederationList.class);
        target.add(getFeedbackPanel());
    }

    private void savePerformed(AjaxRequestTarget target){
        ModelService modelService = getModelService();
        FederationMemberType federationMember;

        if(model == null || model.getObject() == null){
            return;
        }

        federationMember = model.getObject();

        try{

            if(!isEditingFederationMember()){
                SystemConfigurationType systemConfig = loadSystemConfiguration();
                federationMember.setStatus(FederationMemberType.FederationMemberStatusType.REQUESTED);
                federationMember.setRequesterIdentifier(systemConfig.getIdentityProviderIdentifier());

                //At first, we send a request to make a bond with another federation request
                SimpleRestResponseStatus responseStatus = getFederationServiceClient().createFederationRequest(federationMember,
                        systemConfig.getLocalAddress(), systemConfig.getPort());

                if(HttpStatus.OK_200 == responseStatus.getStatus()){

                    SimpleRestResponseStatus secondResponseStatus = getFederationServiceClient().createGetFederationIdentifierRequest(federationMember);

                    //If that request is processed, we need to get the federation identifier of target
                    //federation member
                    if(HttpStatus.OK_200 == secondResponseStatus.getStatus()){
                        federationMember.setFederationMemberName(secondResponseStatus.getMessage());
                        federationMember = modelService.createObject(federationMember);
                    } else {
                        getSession().error("Federation request not processed, Status: "
                                + secondResponseStatus.getStatus() + ", Message: " + secondResponseStatus.getMessage());
                        LOGGER.error("Federation request not processed, Status: "
                                + secondResponseStatus.getStatus() + ", Message: " + secondResponseStatus.getMessage());
                    }

                    LOGGER.info("Federation request processed OK, " + responseStatus.getMessage());
                    getSession().success("Federation request processed OK, " + responseStatus.getMessage());
                } else {
                    getSession().error("Federation request not processed, Status: "
                            + responseStatus.getStatus() + ", Message: " + responseStatus.getMessage());
                    LOGGER.error("Federation request not processed, Status: "
                            + responseStatus.getStatus() + ", Message: " + responseStatus.getMessage());
                }

            } else {
                modelService.updateObject(federationMember);
            }

        } catch (GeneralException e){
            LOGGER.error("Can't add federation member: ", e);
            error("Can't add federation member with name: '" + federationMember.getName() + "'. Reason: " + e.getExceptionMessage());
        } catch (IOException e) {
            LOGGER.error("Can't process federation request: ", e);
            error("Can't process federation request for member with name: '" + federationMember.getName() + "'. Reason: " + e.getMessage());
        }

        getSession().success("Federation member '" + federationMember.getName() + "' has been saved successfully.");
        LOGGER.info("Federation member '" + federationMember.getName() + "' has been saved successfully.");
        setResponsePage(PageFederationList.class);
        target.add(getFeedbackPanel());
    }
}
