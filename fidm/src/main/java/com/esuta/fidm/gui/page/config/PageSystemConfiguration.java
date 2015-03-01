package com.esuta.fidm.gui.page.config;

import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.GeneralException;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.repository.schema.core.SystemConfigurationType;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 *  @author shood
 *
 *  TODO - add some warning when saving changes - some changes may completely destroy FIDM
 * */
public class PageSystemConfiguration extends PageBase {

    private transient Logger LOGGER = Logger.getLogger(PageSystemConfiguration.class);

    private static final String ID_MAIN_FORM = "mainForm";
    private static final String ID_NAME = "name";
    private static final String ID_IDENTIFIER = "identityProviderIdentifier";
    private static final String ID_ADDRESS = "address";
    private static final String ID_PORT = "port";
    private static final String ID_DB_CONNECTION = "dbConnectionFile";
    private static final String ID_BUTTON_SAVE = "saveButton";
    private static final String ID_BUTTON_CANCEL = "cancelButton";

    private IModel<SystemConfigurationType> model;

    public PageSystemConfiguration(){

        model = new LoadableModel<SystemConfigurationType>(false) {

            @Override
            protected SystemConfigurationType load() {
                return loadSystemConfiguration();
            }
        };

        initLayout();
    }

    private void initLayout(){
        Form mainForm = new Form(ID_MAIN_FORM);
        mainForm.setOutputMarkupId(true);
        add(mainForm);

        TextField name = new TextField<>(ID_NAME, new PropertyModel<String>(model, "name"));
        name.setRequired(true);
        mainForm.add(name);

        TextField identifier = new TextField<>(ID_IDENTIFIER, new PropertyModel<String>(model, "identityProviderIdentifier"));
        identifier.setRequired(true);
        mainForm.add(identifier);

        Label address = new Label(ID_ADDRESS, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return model.getObject().getLocalAddress();
            }
        });
        mainForm.add(address);

        Label port = new Label(ID_PORT, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return Integer.toString(model.getObject().getPort());
            }
        });
        mainForm.add(port);

        TextField objectDbPath = new TextField<>(ID_DB_CONNECTION, new PropertyModel<String>(model, "dbConnectionFile"));
        objectDbPath.setRequired(true);
        mainForm.add(objectDbPath);

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
        setResponsePage(PageDebugList.class);
    }

    private void savePerformed(AjaxRequestTarget target){
        ModelService modelService = getModelService();
        SystemConfigurationType systemConfig;

        if(model == null || model.getObject() == null){
            return;
        }

        systemConfig = model.getObject();

        try{
            modelService.updateObject(systemConfig);

        } catch (GeneralException e){
            LOGGER.error("Can't save system configuration object: ", e);
            error("Can't save system configuration object, uid: '" + systemConfig.getUid() + "'. Reason: " + e.getExceptionMessage());
        }

        getSession().success("System configuration, uid: '" + systemConfig.getUid() + "' has been saved successfully.");
        LOGGER.info("System configuration, uid '" + systemConfig.getUid() + "' has been saved successfully.");
        setResponsePage(PageDebugList.class);
        target.add(getFeedbackPanel());
    }
}
