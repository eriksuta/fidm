package com.esuta.fidm.gui.page.users;

import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.GeneralException;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.repository.schema.UserType;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 *  @author shood
 * */
public class PageUser extends PageBase {

    private transient Logger LOGGER = Logger.getLogger(PageUser.class);

    private static final String ID_MAIN_FORM = "mainForm";
    private static final String ID_NAME = "name";
    private static final String ID_GIVEN_NAME = "givenName";
    private static final String ID_FAMILY_NAME = "familyName";
    private static final String ID_FULL_NAME = "fullName";
    private static final String ID_ADDITIONAL_NAME = "additionalName";
    private static final String ID_NICK_NAME = "nickName";
    private static final String ID_EMAIL = "email";
    private static final String ID_LOCALITY = "locality";
    private static final String ID_PREFIX = "honorificPrefix";
    private static final String ID_SUFFIX = "honorificSuffix";
    private static final String ID_TITLE = "title";
    private static final String ID_PHONE_NUMBER = "telephoneNumber";
    private static final String ID_PASS = "password";
    private static final String ID_PASS_CONFIRM = "passwordConfirm";
    private static final String ID_BUTTON_SAVE = "saveButton";
    private static final String ID_BUTTON_CANCEL = "cancelButton";

    private IModel<UserType> model;
    private String pass;
    private String passConfirm;

    public PageUser(){

        model = new LoadableModel<UserType>(false) {

            @Override
            protected UserType load() {
                return loadUser();
            }
        };

        initLayout();
    }

    private UserType loadUser(){
        return new UserType();
    }

    protected void initLayout(){
        Form mainForm = new Form(ID_MAIN_FORM);
        mainForm.setOutputMarkupId(true);
        add(mainForm);

        TextField name = new TextField<String>(ID_NAME, new PropertyModel<String>(model, "name"));
        name.setRequired(true);
        mainForm.add(name);

        TextField givenName = new TextField<String>(ID_GIVEN_NAME, new PropertyModel<String>(model, "givenName"));
        mainForm.add(givenName);

        TextField familyName = new TextField<String>(ID_FAMILY_NAME, new PropertyModel<String>(model, "familyName"));
        mainForm.add(familyName);

        TextField fullName = new TextField<String>(ID_FULL_NAME, new PropertyModel<String>(model, "fullName"));
        mainForm.add(fullName);

        TextField additionalName = new TextField<String>(ID_ADDITIONAL_NAME, new PropertyModel<String>(model, "additionalName"));
        mainForm.add(additionalName);

        TextField nickName = new TextField<String>(ID_NICK_NAME, new PropertyModel<String>(model, "nickName"));
        mainForm.add(nickName);

        TextField email = new TextField<String>(ID_EMAIL, new PropertyModel<String>(model, "emailAdress"));
        mainForm.add(email);

        TextField locality = new TextField<String>(ID_LOCALITY, new PropertyModel<String>(model, "locality"));
        mainForm.add(locality);

        TextField honorificPrefix = new TextField<String>(ID_PREFIX, new PropertyModel<String>(model, "honorificPrefix"));
        mainForm.add(honorificPrefix);

        TextField honorificSuffix = new TextField<String>(ID_SUFFIX, new PropertyModel<String>(model, "honorificSuffix"));
        mainForm.add(honorificSuffix);

        TextField title = new TextField<String>(ID_TITLE, new PropertyModel<String>(model, "title"));
        mainForm.add(title);

        TextField phoneNumber = new TextField<String>(ID_PHONE_NUMBER, new PropertyModel<String>(model, "telephoneNumber"));
        mainForm.add(phoneNumber);

        PasswordTextField password = new PasswordTextField(ID_PASS, new PropertyModel<String>(this, "pass"));
        password.setRequired(true);
        mainForm.add(password);

        PasswordTextField passwordConfirm = new PasswordTextField(ID_PASS_CONFIRM, new PropertyModel<String>(this, "passConfirm"));
        passwordConfirm.setRequired(true);
        mainForm.add(passwordConfirm);

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

    public Form getMainForm(){
        return (Form) get(ID_MAIN_FORM);
    }

    private void cancelPerformed(){
        setResponsePage(PageUserList.class);
    }

    /**
     *  TODO - add password Handling
     * */
    private void savePerformed(AjaxRequestTarget target){
        ModelService modelService = getModelService();
        UserType user;

        if(model == null || model.getObject() == null){
            return;
        }

        user = model.getObject();

        try{
            modelService.createObject(user);
        } catch (GeneralException e){
            LOGGER.error("Can't add user: ", e);
            error("Can't add user with name: '" + user.getName() + "'. Reason: " + e.getExceptionMessage());
        }

        success("User has been saved.");
        target.add(getFeedbackPanel(), getMainForm());
    }
}
