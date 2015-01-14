package com.esuta.fidm.gui.page.users;

import com.esuta.fidm.gui.component.behavior.VisibleEnableBehavior;
import com.esuta.fidm.gui.component.data.AssignableDataProvider;
import com.esuta.fidm.gui.component.data.column.EditDeleteButtonColumn;
import com.esuta.fidm.gui.component.data.table.TablePanel;
import com.esuta.fidm.gui.component.modal.AssignablePopupDialog;
import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.gui.page.roles.PageRole;
import com.esuta.fidm.gui.page.users.dto.UserTypeDto;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.GeneralException;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.repository.schema.RoleType;
import com.esuta.fidm.repository.schema.UserType;
import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 *
 *  TODO - carefull with setResetPassword() on password fields - clear password value can be seen in source of page.
 *  (Although it is not an issue in prototype)
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

    private static final String ID_ROLE_CONTAINER = "roleContainer";
    private static final String ID_BUTTON_ADD_ROLE = "addRole";
    private static final String ID_TABLE_ROLES = "roleTable";

    private static final String ID_ASSIGNABLE_POPUP = "assignablePopup";

    private IModel<UserTypeDto> model;

    public PageUser(){
        this(null);
    }

    public PageUser(PageParameters parameters){
        super(parameters);

        model = new LoadableModel<UserTypeDto>(false) {

            @Override
            protected UserTypeDto load() {
                return loadUser();
            }
        };

        initLayout();
    }

    private UserTypeDto loadUser(){
        if(!isEditingUser()){
            return new UserTypeDto(new UserType());
        }

        PageParameters parameters = getPageParameters();
        String uid = parameters.get(UID_PAGE_PARAMETER_NAME).toString();
        UserTypeDto dto;

        try {
            UserType user = getModelService().readObject(UserType.class, uid);

            dto = new UserTypeDto(user);
        } catch (DatabaseCommunicationException exc){
            error("Couldn't retrieve user with oid: '" + uid + "' from the database. Reason: " + exc.getExceptionMessage());
            LOGGER.error("Couldn't retrieve user with oid: '" + uid + "' from the database. Reason: ", exc);
            throw new RestartResponseException(PageUserList.class);
        }

        return dto;
    }

    protected void initLayout(){
        Form mainForm = new Form(ID_MAIN_FORM);
        mainForm.setOutputMarkupId(true);
        add(mainForm);

        TextField name = new TextField<>(ID_NAME, new PropertyModel<String>(model, UserTypeDto.F_USER + ".name"));
        name.setRequired(true);
        mainForm.add(name);

        TextField givenName = new TextField<>(ID_GIVEN_NAME, new PropertyModel<String>(model, UserTypeDto.F_USER + ".givenName"));
        mainForm.add(givenName);

        TextField familyName = new TextField<>(ID_FAMILY_NAME, new PropertyModel<String>(model, UserTypeDto.F_USER + ".familyName"));
        mainForm.add(familyName);

        TextField fullName = new TextField<>(ID_FULL_NAME, new PropertyModel<String>(model, UserTypeDto.F_USER + ".fullName"));
        mainForm.add(fullName);

        TextField additionalName = new TextField<>(ID_ADDITIONAL_NAME, new PropertyModel<String>(model, UserTypeDto.F_USER + ".additionalName"));
        mainForm.add(additionalName);

        TextField nickName = new TextField<>(ID_NICK_NAME, new PropertyModel<String>(model, UserTypeDto.F_USER + ".nickName"));
        mainForm.add(nickName);

        TextField email = new TextField<>(ID_EMAIL, new PropertyModel<String>(model, UserTypeDto.F_USER + ".emailAddress"));
        mainForm.add(email);

        TextField locality = new TextField<>(ID_LOCALITY, new PropertyModel<String>(model, UserTypeDto.F_USER + ".locality"));
        mainForm.add(locality);

        TextField honorificPrefix = new TextField<>(ID_PREFIX, new PropertyModel<String>(model, UserTypeDto.F_USER + ".honorificPrefix"));
        mainForm.add(honorificPrefix);

        TextField honorificSuffix = new TextField<>(ID_SUFFIX, new PropertyModel<String>(model, UserTypeDto.F_USER + ".honorificSuffix"));
        mainForm.add(honorificSuffix);

        TextField title = new TextField<>(ID_TITLE, new PropertyModel<String>(model, UserTypeDto.F_USER + ".title"));
        mainForm.add(title);

        TextField phoneNumber = new TextField<>(ID_PHONE_NUMBER, new PropertyModel<String>(model, UserTypeDto.F_USER + ".telephoneNumber"));
        mainForm.add(phoneNumber);

        PasswordTextField password = new PasswordTextField(ID_PASS, new PropertyModel<String>(model, UserTypeDto.F_PASSWORD));
        password.setResetPassword(false);
        password.setRequired(true);
        mainForm.add(password);

        PasswordTextField passwordConfirm = new PasswordTextField(ID_PASS_CONFIRM, new PropertyModel<String>(model, UserTypeDto.F_PASSWORD_CONFIRM));
        passwordConfirm.setResetPassword(false);
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

        initModalWindows();
        initAssignments(mainForm);
    }

    private void initModalWindows(){
        ModalWindow assignableModalRole = new AssignablePopupDialog<RoleType>(ID_ASSIGNABLE_POPUP, RoleType.class){

            @Override
            public void addPerformed(AjaxRequestTarget target, IModel<RoleType> rowModel) {
                addRoleAssignmentPerformed(target, rowModel);
            }
        };
        add(assignableModalRole);
    }

    private void initAssignments(Form mainForm){
        WebMarkupContainer roleContainer = new WebMarkupContainer(ID_ROLE_CONTAINER);
        roleContainer.setOutputMarkupId(true);
        roleContainer.setOutputMarkupPlaceholderTag(true);
        mainForm.add(roleContainer);

        AjaxLink addRole = new AjaxLink(ID_BUTTON_ADD_ROLE) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                roleAdditionPerformed(target);
            }
        };
        roleContainer.add(addRole);

        List<IColumn> roleColumns = createRoleColumns();
        AssignableDataProvider<RoleType, UserType> roleProvider = new AssignableDataProvider<>(getPage(),
                RoleType.class, model.getObject().getUser());

        TablePanel roleTable = new TablePanel(ID_TABLE_ROLES, roleProvider, roleColumns, 10);
        roleTable.add(new VisibleEnableBehavior(){

            @Override
            public boolean isVisible() {
                if(model.getObject().getUser() == null){
                    return false;
                }

                return !model.getObject().getUser().getRoleAssignments().isEmpty();
            }
        });
        roleTable.setShowHeader(false);
        roleTable.setShowPaging(false);
        roleTable.setOutputMarkupId(true);
        roleContainer.add(roleTable);
    }

    private List<IColumn> createRoleColumns(){
        List<IColumn> columns = new ArrayList<>();

        columns.add(new PropertyColumn<RoleType, String>(new Model<>("Name"), "name", "name"));
        columns.add(new PropertyColumn<RoleType, String>(new Model<>("Display Name"), "displayName", "displayName"));
        columns.add(new PropertyColumn<RoleType, String>(new Model<>("Role Type"), "roleType", "roleType"));
        columns.add(new EditDeleteButtonColumn<RoleType>(new Model<>("Actions")){

            @Override
            public void editPerformed(AjaxRequestTarget target, IModel<RoleType> rowModel) {
                PageUser.this.editRolePerformed(target, rowModel);
            }

            @Override
            public void removePerformed(AjaxRequestTarget target, IModel<RoleType> rowModel) {
                PageUser.this.removeRolePerformed(target, rowModel);
            }
        });

        return columns;
    }

    private boolean isEditingUser(){
        PageParameters parameters = getPageParameters();
        return !parameters.get(UID_PAGE_PARAMETER_NAME).isEmpty();
    }

    public Form getMainForm(){
        return (Form) get(ID_MAIN_FORM);
    }

    public WebMarkupContainer getRoleContainer(){
        return (WebMarkupContainer) get(ID_MAIN_FORM + ":" + ID_ROLE_CONTAINER);
    }

    private void addRoleAssignmentPerformed(AjaxRequestTarget target, IModel<RoleType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            return;
        }

        RoleType role = rowModel.getObject();
        model.getObject().getUser().getRoleAssignments().add(role.getUid());

        ModalWindow dialog = (ModalWindow) get(ID_ASSIGNABLE_POPUP);
        dialog.close(target);
        target.add(getRoleContainer());
    }

    private void roleAdditionPerformed(AjaxRequestTarget target){
        ModalWindow modal = (ModalWindow) get(ID_ASSIGNABLE_POPUP);
        modal.show(target);
    }

    private void editRolePerformed(AjaxRequestTarget target, IModel<RoleType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            error("Couldn't edit selected role. It is no longer available.");
            target.add(getFeedbackPanel());
            return;
        }

        PageParameters parameters = new PageParameters();
        parameters.add(UID_PAGE_PARAMETER_NAME, rowModel.getObject().getUid());
        setResponsePage(PageRole.class, parameters);
    }

    private void removeRolePerformed(AjaxRequestTarget target, IModel<RoleType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            error("Couldn't remove selected role assignment. Something went wrong.");
            target.add(getFeedbackPanel());
            return;
        }

        String roleUid = rowModel.getObject().getUid();

        model.getObject().getUser().getRoleAssignments().remove(roleUid);
        target.add(getRoleContainer());
    }

    private void cancelPerformed(){
        setResponsePage(PageUserList.class);
    }

    private void savePerformed(AjaxRequestTarget target){
        ModelService modelService = getModelService();
        UserTypeDto userDto;
        UserType user;

        if(model == null || model.getObject() == null){
            return;
        }

        userDto = model.getObject();
        user = userDto.getUser();

        if(userDto.getPassword().equals(userDto.getPasswordConfirm())){
            userDto.setPassword(userDto.getPassword());
        } else {
            info("Values in password and password confirmation fields are not the same. Please provide correct password values.");
            target.add(getFeedbackPanel());
            return;
        }

        try{

            if(!isEditingUser()){
                modelService.createObject(user);
            } else {
                modelService.updateObject(user);
            }

        } catch (GeneralException e){
            LOGGER.error("Can't add user: ", e);
            error("Can't add user with name: '" + user.getName() + "'. Reason: " + e.getExceptionMessage());
        }

        getSession().success("User '" + user.getName() + "' has been saved successfully.");
        LOGGER.info("User '" + user.getName() + "' has been saved successfully.");
        setResponsePage(PageUserList.class);
        target.add(getFeedbackPanel());
    }
}
