package com.esuta.fidm.gui.page.config;

import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.GeneralException;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.repository.schema.core.SystemConfigurationType;
import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
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
    private static final String ID_IDENTIFIER = "identityProviderIdentifier";
    private static final String ID_DB_CONNECTION = "dbConnectionFile";
    private static final String ID_BUTTON_SAVE = "saveButton";
    private static final String ID_BUTTON_CANCEL = "cancelButton";

    private static final String SYSTEM_CONFIG_UID = "00000000-0000-0000-0000-000000000001";

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

    private SystemConfigurationType loadSystemConfiguration(){
        SystemConfigurationType systemConfiguration;

        try {
            systemConfiguration = getModelService().readObject(SystemConfigurationType.class, SYSTEM_CONFIG_UID);
        } catch (DatabaseCommunicationException exc){
            error("Couldn't retrieve system configuration object, uid: '" + SYSTEM_CONFIG_UID + "' from the repository. Reason: " + exc.getExceptionMessage());
            LOGGER.error("Couldn't retrieve system configuration object, uid: '" + SYSTEM_CONFIG_UID + "' from the repository. Reason: ", exc);
            throw new RestartResponseException(PageDebugList.class);
        }

        return systemConfiguration;
    }

    private void initLayout(){
        Form mainForm = new Form(ID_MAIN_FORM);
        mainForm.setOutputMarkupId(true);
        add(mainForm);

        TextField identifier = new TextField<>(ID_IDENTIFIER, new PropertyModel<String>(model, "identityProviderIdentifier"));
        identifier.setRequired(true);
        mainForm.add(identifier);

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
