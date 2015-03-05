package com.esuta.fidm.gui.page.federation;

import com.esuta.fidm.gui.component.behavior.VisibleEnableBehavior;
import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.GeneralException;
import com.esuta.fidm.infra.exception.ObjectNotFoundException;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.model.federation.client.SimpleRestResponse;
import com.esuta.fidm.model.federation.service.FederationMembershipRequest;
import com.esuta.fidm.repository.schema.core.FederationMemberType;
import com.esuta.fidm.repository.schema.core.SystemConfigurationType;
import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
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
 *
 *  TODO - currently, even the member requesting the deletion of membership
 *  is able to respond to this request, fix this (probably need another attribute
 *  to save the requestor of deletion)
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

    private static final String ID_ADD_BUTTON_GROUP = "addRequestButtonGroup";
    private static final String ID_BUTTON_ACCEPT = "acceptButton";
    private static final String ID_BUTTON_REJECT = "rejectButton";
    private static final String ID_DELETE_BUTTON_GROUP = "deleteRequestButtonGroup";
    private static final String ID_BUTTON_ACCEPT_DELETION = "deleteAcceptButton";
    private static final String ID_BUTTON_REJECT_DELETION = "deleteRejectButton";


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

        initAcceptButtons(mainForm);
    }

    private void initAcceptButtons(Form mainForm){
        WebMarkupContainer additionRequestButtonGroup = new WebMarkupContainer(ID_ADD_BUTTON_GROUP);
        additionRequestButtonGroup.setOutputMarkupId(true);
        additionRequestButtonGroup.setOutputMarkupPlaceholderTag(true);
        additionRequestButtonGroup.add(new VisibleEnableBehavior(){

            @Override
            public boolean isVisible() {
                FederationMemberType member = model.getObject();

                if(member == null || member.getFederationMemberName() == null || member.getRequesterIdentifier() == null){
                    return false;
                }

                if(!member.getRequesterIdentifier().equals(member.getFederationMemberName())){
                    return false;
                }

                return FederationMemberType.FederationMemberStatusType.REQUESTED.equals(member.getStatus());
            }
        });
        mainForm.add(additionRequestButtonGroup);

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
        additionRequestButtonGroup.add(accept);

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
        additionRequestButtonGroup.add(reject);

        WebMarkupContainer deletionRequestButtonGroup = new WebMarkupContainer(ID_DELETE_BUTTON_GROUP);
        deletionRequestButtonGroup.setOutputMarkupId(true);
        deletionRequestButtonGroup.setOutputMarkupPlaceholderTag(true);
        deletionRequestButtonGroup.add(new VisibleEnableBehavior() {

            @Override
            public boolean isVisible() {
                FederationMemberType member = model.getObject();

                if(member == null || member.getFederationMemberName() == null || member.getRequesterIdentifier() == null){
                    return false;
                }

                return FederationMemberType.FederationMemberStatusType.DELETE_REQUESTED.equals(member.getStatus());
            }
        });
        mainForm.add(deletionRequestButtonGroup);

        AjaxSubmitLink acceptDelete = new AjaxSubmitLink(ID_BUTTON_ACCEPT_DELETION) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                acceptDeletePerformed(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(getFeedbackPanel());
            }
        };
        deletionRequestButtonGroup.add(acceptDelete);

        AjaxSubmitLink rejectDelete = new AjaxSubmitLink(ID_BUTTON_REJECT_DELETION) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                rejectDeletePerformed(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(getFeedbackPanel());
            }
        };
        deletionRequestButtonGroup.add(rejectDelete);
    }

    private void cancelPerformed(){
        setResponsePage(PageFederationList.class);
    }

    private void acceptDeletePerformed(AjaxRequestTarget target){
        deleteAcceptRejectOperation(target, FederationMembershipRequest.Response.ACCEPT);
    }

    private void rejectDeletePerformed(AjaxRequestTarget target){
        deleteAcceptRejectOperation(target, FederationMembershipRequest.Response.DENY);
    }

    private void deleteAcceptRejectOperation(AjaxRequestTarget target, FederationMembershipRequest.Response response){
        if(model == null || model.getObject() == null){
            return;
        }

        FederationMemberType member = model.getObject();
        String memberName = member.getFederationMemberName();
        String memberUid = member.getUid();

        try {
            SimpleRestResponse status = getFederationServiceClient().createFederationDeletionRequestResponse(member, response);

            if(HttpStatus.OK_200 == status.getStatus()){
                if(FederationMembershipRequest.Response.ACCEPT.equals(response)){
                    getModelService().deleteObject(member);
                    LOGGER.info("Federation member: '" + memberName + "'(" + memberUid + ") deleted.");
                    getSession().info("Federation member: '" + memberName + "'(" + memberUid + ") deleted.");
                } else {
                    member.setStatus(FederationMemberType.FederationMemberStatusType.AVAILABLE);
                    getModelService().updateObject(member);
                    LOGGER.info("Federation member: '" + memberName + "'(" + memberUid + ") deletion not accepted. Switching status to AVAILABLE.");
                    getSession().info("Federation member: '" + memberName + "'(" + memberUid + ") deletion not accepted. Switching status to AVAILABLE..");
                }

            } else {
                String message = status.getMessage();
                LOGGER.error("Federation membership deletion request not successful. Reason: " + message);
                getSession().error("Federation membership deletion request not successful. Reason: " + message);
            }

        } catch (IOException e) {
            LOGGER.error("Could not create a REST request to ACCEPT/DENY federation member deletion. Federation member: '"
                    + member.getFederationMemberName() + "'. Reason: ", e);

            getSession().error("Could not create a REST request to ACCEPT/DENY federation member deletion. Federation member: '"
                    + member.getFederationMemberName() + "'. Reason: " + e);
        } catch (ObjectNotFoundException | DatabaseCommunicationException e) {
            LOGGER.error("Could not delete/update federation member: '" + member.getFederationMemberName()
                    + "'(" + member.getUid() + ").", e);
            getSession().error("Could not delete/update federation member: '" + member.getFederationMemberName()
                    + "'(" + member.getUid() + ")." + e);
        }

        setResponsePage(PageFederationList.class);
        target.add(getFeedbackPanel());
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
            SimpleRestResponse status = getFederationServiceClient().createFederationResponse(member, response);

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
                String message = status.getMessage();
                LOGGER.error("Federation membership request not successful. Reason: " + message);
                getSession().error("Federation membership request not successful. Reason: " + message);
            }

        } catch (IOException e) {
            LOGGER.error("Could not create a REST request to ACCEPT/DENY federation member. Federation member: '"
                    + member.getFederationMemberName() + "'. Reason: ", e);

            getSession().error("Could not create a REST request to ACCEPT/DENY federation member. Federation member: '"
                    + member.getFederationMemberName() + "'. Reason: " + e);
        } catch (ObjectNotFoundException | DatabaseCommunicationException e) {
            LOGGER.error("Could not update federation member: '" + member.getFederationMemberName()
                    + "'(" + member.getUid() + ").", e);
            getSession().error("Could not update federation member: '" + member.getFederationMemberName()
                    + "'(" + member.getUid() + ")." + e);
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
                SimpleRestResponse responseStatus = getFederationServiceClient().createFederationRequest(federationMember,
                        systemConfig.getLocalAddress(), systemConfig.getPort());

                if(HttpStatus.OK_200 == responseStatus.getStatus()){

                    SimpleRestResponse secondResponseStatus = getFederationServiceClient().createGetFederationIdentifierRequest(federationMember);

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
