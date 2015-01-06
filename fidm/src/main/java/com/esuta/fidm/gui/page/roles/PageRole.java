package com.esuta.fidm.gui.page.roles;

import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.GeneralException;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.repository.schema.RoleType;
import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *  @author shood
 * */
public class PageRole extends PageBase {

    private transient Logger LOGGER = Logger.getLogger(PageRole.class);

    public static final String ID_MAIN_FORM = "mainForm";
    public static final String ID_NAME = "name";
    public static final String ID_DISPLAY_NAME = "displayName";
    public static final String ID_DESCRIPTION = "description";
    public static final String ID_TYPE = "type";
    public static final String ID_BUTTON_SAVE = "saveButton";
    public static final String ID_BUTTON_CANCEL = "cancelButton";

    private IModel<RoleType> model;

    public PageRole(){
        model = new LoadableModel<RoleType>() {
            @Override
            protected RoleType load() {
                return loadRole();
            }
        };

        initLayout();
    }

    private RoleType loadRole(){
        if(!isEditingRole()){
            return new RoleType();
        }

        PageParameters parameters = getPageParameters();
        String uid = parameters.get(UID_PAGE_PARAMETER_NAME).toString();
        RoleType role;

        try {
            role = getModelService().readObject(RoleType.class, uid);
        } catch (DatabaseCommunicationException exc){
            error("Couldn't retrieve role with oid: '" + uid + "' from the repository. Reason: " + exc.getExceptionMessage());
            LOGGER.error("Couldn't retrieve role with oid: '" + uid + "' from the repository. Reason: ", exc);
            throw new RestartResponseException(PageRoleList.class);
        }

        return role;
    }

    private boolean isEditingRole(){
        PageParameters parameters = getPageParameters();
        return !parameters.get(UID_PAGE_PARAMETER_NAME).isEmpty();
    }

    private void initLayout(){
        Form mainForm = new Form(ID_MAIN_FORM);
        mainForm.setOutputMarkupId(true);
        add(mainForm);

        TextField name = new TextField<>(ID_NAME, new PropertyModel<String>(model, "name"));
        name.setRequired(true);
        mainForm.add(name);

        TextField displayName = new TextField<>(ID_DISPLAY_NAME, new PropertyModel<String>(model, "displayName"));
        displayName.setRequired(true);
        mainForm.add(displayName);

        TextArea description = new TextArea<>(ID_DESCRIPTION, new PropertyModel<String>(model, "description"));
        mainForm.add(description);

        TextField type = new TextField<>(ID_TYPE, new PropertyModel<String>(model, "roleType"));
        mainForm.add(type);

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
        setResponsePage(PageRoleList.class);
    }

    private void savePerformed(AjaxRequestTarget target){
        ModelService modelService = getModelService();
        RoleType role;

        if(model == null || model.getObject() == null){
            return;
        }

        role = model.getObject();

        try{

            if(!isEditingRole()){
                modelService.createObject(role);
            } else {
                modelService.updateObject(role);
            }

        } catch (GeneralException e){
            LOGGER.error("Can't add role: ", e);
            error("Can't add role with name: '" + role.getName() + "'. Reason: " + e.getExceptionMessage());
        }

        getSession().success("Role '" + role.getName() + "' has been saved successfully.");
        LOGGER.info("Role '" + role.getName() + "' has been saved successfully.");
        setResponsePage(PageRoleList.class);
        target.add(getFeedbackPanel());
    }
}
