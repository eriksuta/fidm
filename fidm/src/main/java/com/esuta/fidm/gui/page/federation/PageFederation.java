package com.esuta.fidm.gui.page.federation;

import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.GeneralException;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.repository.schema.core.FederationMemberType;
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

        Label identifier = new Label(ID_NAME, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
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
        mainForm.add(webAddress);

        TextField port = new TextField<>(ID_PORT, new PropertyModel<String>(model, "port"));
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
    }

    private void cancelPerformed(){
        setResponsePage(PageFederationList.class);
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
                federationMember.setStatus(FederationMemberType.FederationMemberStatusType.REQUESTED);
                modelService.createObject(federationMember);
            } else {
                modelService.updateObject(federationMember);
            }

        } catch (GeneralException e){
            LOGGER.error("Can't add federation member: ", e);
            error("Can't add federation member with name: '" + federationMember.getName() + "'. Reason: " + e.getExceptionMessage());
        }

        getSession().success("Federation member '" + federationMember.getName() + "' has been saved successfully.");
        LOGGER.info("Federation member '" + federationMember.getName() + "' has been saved successfully.");
        setResponsePage(PageFederationList.class);
        target.add(getFeedbackPanel());
    }
}
