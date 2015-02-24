package com.esuta.fidm.gui.page.users;

import com.esuta.fidm.gui.component.modal.ObjectChooserDialog;
import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.GeneralException;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.repository.schema.core.AccountType;
import com.esuta.fidm.repository.schema.core.ResourceType;
import com.esuta.fidm.repository.schema.core.UserType;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 *
 *  TODO
 * */
public class PageAccount extends PageBase{

    private transient Logger LOGGER = Logger.getLogger(PageAccount.class);

    private static final String ID_MAIN_FORM = "mainForm";
    private static final String ID_NAME = "name";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_OWNER_INPUT = "ownerInput";
    private static final String ID_OWNER_EDIT = "ownerEdit";
    private static final String ID_RESOURCE = "resource";
    private static final String ID_PASSWORD = "password";
    private static final String ID_PROTECTED = "protected";
    private static final String ID_BUTTON_SAVE = "saveButton";
    private static final String ID_BUTTON_CANCEL = "cancelButton";

    private static final String ID_OWNER_CHOOSER_MODAL = "ownerChooser";

    private IModel<AccountType> model;

    public PageAccount(){
        this(null);
    }

    public PageAccount(PageParameters parameters){
        super(parameters);

        model = new LoadableModel<AccountType>(false) {
            @Override
            protected AccountType load() {
                return loadAccount();
            }
        };

        initLayout();
    }

    private AccountType loadAccount(){
        if(!isEditingAccount()){
            AccountType newAccount = new AccountType();

            if(!getPageParameters().get(PAGE_ACCOUNT_RESOURCE_UID).isEmpty()){
                PageParameters parameters = getPageParameters();
                String resourceUid = parameters.get(PAGE_ACCOUNT_RESOURCE_UID).toString();
                newAccount.setResource(resourceUid);
            }

            return newAccount;
        }

        PageParameters parameters = getPageParameters();
        String uid = parameters.get(UID_PAGE_PARAMETER_NAME).toString();
        AccountType account;

        try {
            account = getModelService().readObject(AccountType.class, uid);
        } catch (DatabaseCommunicationException exc){
            error("Couldn't retrieve account with oid: '" + uid + "' from the repository. Reason: " + exc.getExceptionMessage());
            LOGGER.error("Couldn't retrieve account with oid: '" + uid + "' from the repository. Reason: ", exc);
            throw new RestartResponseException(PageUserList.class);
        }

        return account;
    }

    private boolean isEditingAccount(){
        PageParameters parameters = getPageParameters();
        return !parameters.get(UID_PAGE_PARAMETER_NAME).isEmpty();
    }

    private void initLayout(){
        Form mainForm = new Form(ID_MAIN_FORM);
        mainForm.setOutputMarkupId(true);
        add(mainForm);

        TextField name = new TextField<>(ID_NAME, new PropertyModel<String>(model, "accountName"));
        name.setRequired(true);
        mainForm.add(name);

        TextArea description = new TextArea<>(ID_DESCRIPTION, new PropertyModel<String>(model, "description"));
        mainForm.add(description);

        TextField ownerInput = new TextField<>(ID_OWNER_INPUT, createOwnerInputModel());
        ownerInput.setRequired(true);
        ownerInput.setOutputMarkupId(true);
        ownerInput.add(AttributeAppender.replace("placeholder", "Add owner"));
        ownerInput.setEnabled(false);
        mainForm.add(ownerInput);

        AjaxLink ownerEdit = new AjaxLink(ID_OWNER_EDIT) {

            @Override
            public void onClick(AjaxRequestTarget target) {
               ownerEditPerformed(target);
            }
        };
        mainForm.add(ownerEdit);

        DropDownChoice resource = new DropDownChoice<>(ID_RESOURCE, new PropertyModel<String>(model, "resource"),

                new AbstractReadOnlyModel<List<String>>() {

                    @Override
                    public List<String> getObject() {
                        return getResourceNames();
                    }
                }, new IChoiceRenderer<String>() {
            @Override
            public String getDisplayValue(String object) {
                return object;
            }

            @Override
            public String getIdValue(String object, int index) {
                return Integer.toString(index);
            }
        });
        resource.setRequired(true);
        resource.setNullValid(false);
        mainForm.add(resource);

        PasswordTextField password = new PasswordTextField(ID_PASSWORD, new PropertyModel<String>(model, "password"));
        password.setResetPassword(false);
        password.setRequired(true);
        mainForm.add(password);

        CheckBox _protected = new CheckBox(ID_PROTECTED, new PropertyModel<Boolean>(model, "_protected"));
        mainForm.add(_protected);

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

        initModals();
    }

    private void initModals(){
        ModalWindow ownerChooser = new ObjectChooserDialog<UserType>(ID_OWNER_CHOOSER_MODAL, UserType.class){

            @Override
            public void objectChoosePerformed(AjaxRequestTarget target, IModel<UserType> rowModel) {
                ownerChoosePerformed(target, rowModel);
            }
        };
        add(ownerChooser);
    }

    private IModel<String> createOwnerInputModel(){
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                if(model.getObject() != null && model.getObject().getOwner() != null &&
                        StringUtils.isNotEmpty(model.getObject().getOwner())){
                    String ownerUid = model.getObject().getOwner();

                    try {
                        UserType owner = getModelService().readObject(UserType.class, ownerUid);

                        return owner.getName();
                    } catch (DatabaseCommunicationException e) {
                        LOGGER.error("Can't load owner for account with uid: '" + model.getObject().getUid() + "'. Reason: " + e);
                    }
                }

                return null;
            }
        };
    }

    private List<String> getResourceNames(){
        List<String> resourceNames = new ArrayList<>();

        try {
            List<ResourceType> resources = getModelService().getAllObjectsOfType(ResourceType.class);

            for(ResourceType resource: resources){
                resourceNames.add(resource.getName());
            }
        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Could not retrieve the list of resources.");
        }

        return resourceNames;
    }

    private void ownerEditPerformed(AjaxRequestTarget target){
        ModalWindow window = (ModalWindow) get(ID_OWNER_CHOOSER_MODAL);
        window.show(target);
    }

    private void ownerChoosePerformed(AjaxRequestTarget target, IModel<UserType> userModel){
        if(userModel == null || userModel.getObject() == null){
            return;
        }

        if(model.getObject() == null){
            return;
        }

        String userUid = userModel.getObject().getUid();
        model.getObject().setOwner(userUid);

        ModalWindow window = (ModalWindow) get(ID_OWNER_CHOOSER_MODAL);
        window.close(target);
        target.add(get(ID_MAIN_FORM + ":" + ID_OWNER_INPUT));
    }

    private void cancelPerformed(){
        setResponsePage(PageUserList.class);
    }

    private void savePerformed(AjaxRequestTarget target){
        ModelService modelService = getModelService();
        AccountType account;

        if(model == null || model.getObject() == null){
            return;
        }

        account = model.getObject();

        try{
            ResourceType resource = modelService.readObjectByName(ResourceType.class, account.getResource());
            account.setResource(resource.getUid());

            if(!isEditingAccount()){
                account = modelService.createObject(account);
            } else {
                modelService.updateObject(account);
            }

            if(account.getOwner() == null || account.getOwner().isEmpty()){
                String ownerUid = account.getOwner();

                UserType user = modelService.readObject(UserType.class, ownerUid);

                if(!user.getAccounts().contains(account.getUid())){
                    user.getAccounts().add(account.getUid());
                }
            }

        } catch (GeneralException e){
            LOGGER.error("Can't add role: ", e);
            error("Can't add account with name: '" + account.getName() + "'. Reason: " + e.getExceptionMessage());
        }

        getSession().success("Account '" + account.getName() + "' has been saved successfully.");
        LOGGER.info("Account '" + account.getName() + "' has been saved successfully.");
        setResponsePage(PageUserList.class);
        target.add(getFeedbackPanel());
    }
}
