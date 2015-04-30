package com.esuta.fidm.gui.page.users;

import com.esuta.fidm.gui.component.behavior.VisibleEnableBehavior;
import com.esuta.fidm.gui.component.modal.ObjectChooserDialog;
import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.GeneralException;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.repository.schema.core.*;
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
                ObjectReferenceType resourceReference = new ObjectReferenceType(resourceUid);
                newAccount.setResource(resourceReference);
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

        TextField name = new TextField<>(ID_NAME, new PropertyModel<String>(model, "name"));
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
        ownerEdit.add(new AttributeAppender("class", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return isEditingAccount() ? " disabled" : null;
            }
        }));
        mainForm.add(ownerEdit);

        DropDownChoice resource = new DropDownChoice<>(ID_RESOURCE, new PropertyModel<ObjectReferenceType>(model, "resource"),
                new AbstractReadOnlyModel<List<ObjectReferenceType>>() {

                    @Override
                    public List<ObjectReferenceType> getObject() {
                        return getResourceNames();
                    }
                }, new IChoiceRenderer<ObjectReferenceType>() {
            @Override
            public String getDisplayValue(ObjectReferenceType object) {
                return getNameFromReference(object);
            }

            @Override
            public String getIdValue(ObjectReferenceType object, int index) {
                return Integer.toString(index);
            }
        });
        resource.add(new VisibleEnableBehavior(){

            @Override
            public boolean isEnabled() {
                return !isEditingAccount();
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
                        model.getObject().getOwner() != null){

                    ObjectReferenceType ownerReference = model.getObject().getOwner();

                    try {
                        UserType owner = getModelService().readObject(UserType.class, ownerReference.getUid());

                        return owner.getName();
                    } catch (DatabaseCommunicationException e) {
                        LOGGER.error("Can't load owner for account with uid: '" + model.getObject().getUid() + "'. Reason: " + e);
                    }
                }

                return null;
            }
        };
    }

    private List<ObjectReferenceType> getResourceNames(){
        List<ObjectReferenceType> resourceReferences = new ArrayList<>();

        try {
            List<ResourceType> resources = getModelService().getAllObjectsOfType(ResourceType.class);

            for(ResourceType resource: resources){
                ObjectReferenceType ref = new ObjectReferenceType(resource.getUid());
                resourceReferences.add(ref);
            }
        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Could not retrieve the list of resources.");
        }

        return resourceReferences;
    }

    private String getNameFromReference(ObjectReferenceType resourceRef){
        try {
            ResourceType resource = getModelService().readObject(ResourceType.class, resourceRef.getUid());
            return resource.getName();
        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Could not retrieve the resource: '" + resourceRef.getUid() + "'.");
        }

        return null;
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
        ObjectReferenceType ownerReference = new ObjectReferenceType(userUid);
        model.getObject().setOwner(ownerReference);

        ModalWindow window = (ModalWindow) get(ID_OWNER_CHOOSER_MODAL);
        window.close(target);
        target.add(get(ID_MAIN_FORM + ":" + ID_OWNER_INPUT));
    }

    private void cancelPerformed(){
        setResponsePage(PageUserList.class);
    }

    private void savePerformed(AjaxRequestTarget target){
        ModelService modelService = (ModelService) getModelService();
        AccountType account;

        if(model == null || model.getObject() == null){
            return;
        }

        account = model.getObject();

        try{
            ResourceType resource = modelService.readObject(ResourceType.class, account.getResource().getUid());
            ObjectReferenceType resourceReference = new ObjectReferenceType(resource.getUid());
            account.setResource(resourceReference);

            if(!isEditingAccount()){
                account = modelService.createObject(account);
            } else {
                modelService.updateObject(account);
            }

            if(!isEditingAccount()){
                ObjectReferenceType owner = account.getOwner();

                UserType user = modelService.readObject(UserType.class, owner.getUid());
                AssignmentType accountAssignment = new AssignmentType(account.getUid());
                accountAssignment.setShareInFederation(false);

                if(!accountExists(accountAssignment, user)){
                    user.getAccounts().add(accountAssignment);
                    modelService.updateObject(user);
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

    private boolean accountExists(AssignmentType accountAssignment, UserType user){
        for(AssignmentType assignment: user.getAccounts()){
            if(assignment.getUid().equals(accountAssignment.getUid())){
                return true;
            }
        }

        return false;
    }
}
