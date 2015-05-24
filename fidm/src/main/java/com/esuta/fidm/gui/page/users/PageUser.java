package com.esuta.fidm.gui.page.users;

import com.esuta.fidm.gui.component.WebMiscUtil;
import com.esuta.fidm.gui.component.behavior.VisibleEnableBehavior;
import com.esuta.fidm.gui.component.data.AssignableDataProvider;
import com.esuta.fidm.gui.component.data.column.EditDeleteButtonColumn;
import com.esuta.fidm.gui.component.data.table.TablePanel;
import com.esuta.fidm.gui.component.modal.AssignablePopupDialog;
import com.esuta.fidm.gui.component.modal.RemoteAccountViewerDialog;
import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.gui.page.org.PageOrg;
import com.esuta.fidm.gui.page.roles.PageRole;
import com.esuta.fidm.gui.page.users.dto.UserTypeDto;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.GeneralException;
import com.esuta.fidm.infra.exception.ObjectAlreadyExistsException;
import com.esuta.fidm.infra.exception.ObjectNotFoundException;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.model.federation.client.ObjectTypeRestResponse;
import com.esuta.fidm.model.federation.client.SimpleRestResponse;
import com.esuta.fidm.repository.schema.core.*;
import com.esuta.fidm.repository.schema.support.FederationIdentifierType;
import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.eclipse.jetty.http.HttpStatus;

import java.util.ArrayList;
import java.util.Iterator;
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
    private static final String ID_BUTTON_RECOMPUTE = "recomputeButton";
    private static final String ID_BUTTON_CANCEL = "cancelButton";

    private static final String ID_ROLE_CONTAINER = "roleContainer";
    private static final String ID_BUTTON_ADD_ROLE = "addRole";
    private static final String ID_TABLE_ROLES = "roleTable";

    private static final String ID_ORG_CONTAINER = "orgUnitContainer";
    private static final String ID_BUTTON_ADD_ORG = "addOrgUnit";
    private static final String ID_TABLE_ORG = "orgUnitTable";

    private static final String ID_ACCOUNT_CONTAINER = "accountContainer";
    private static final String ID_BUTTON_ADD_ACCOUNT = "addAccount";
    private static final String ID_TABLE_ACCOUNTS = "accountTable";

    private static final String ID_ROLE_ASSIGNABLE_POPUP = "roleAssignPopup";
    private static final String ID_ORG_ASSIGNABLE_POPUP = "orgAssignPopup";
    private static final String ID_ACCOUNT_POPUP = "accountPopup";
    private static final String ID_REMOTE_ACCOUNT_VIEWER = "remoteAccountViewer";

    private IModel<UserTypeDto> model;

    public PageUser() {
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

        AjaxSubmitLink recompute = new AjaxSubmitLink(ID_BUTTON_RECOMPUTE) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                recomputePerformed(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(getFeedbackPanel());
            }
        };
        recompute.add(new VisibleEnableBehavior(){

            @Override
            public boolean isEnabled() {
                return isEditingUser();
            }
        });
        mainForm.add(recompute);

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
        ModalWindow assignableModalRole = new AssignablePopupDialog<RoleType>(ID_ROLE_ASSIGNABLE_POPUP, RoleType.class){

            @Override
            public void addPerformed(AjaxRequestTarget target, IModel<RoleType> rowModel) {
                addRoleAssignmentPerformed(target, rowModel);
            }
        };
        add(assignableModalRole);

        ModalWindow assignableModalOrgUnit = new AssignablePopupDialog<OrgType>(ID_ORG_ASSIGNABLE_POPUP, OrgType.class){

            @Override
            public void addPerformed(AjaxRequestTarget target, IModel<OrgType> rowModel) {
                addOrgUnitPerformed(target, rowModel);
            }
        };
        add(assignableModalOrgUnit);

        ModalWindow assignableAccount = new AssignablePopupDialog<ResourceType>(ID_ACCOUNT_POPUP, ResourceType.class){

            @Override
            public void addPerformed(AjaxRequestTarget target, IModel<ResourceType> rowModel) {
                addAccountPerformed(target, rowModel);
            }
        };
        add(assignableAccount);

        ModalWindow remoteAccountViewer = new RemoteAccountViewerDialog(ID_REMOTE_ACCOUNT_VIEWER, null);
        add(remoteAccountViewer);
    }

    private void initAssignments(Form mainForm){
        //Init Role Container
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
                return model.getObject().getUser() != null && !model.getObject().getUser().getRoleAssignments().isEmpty();

            }
        });
        roleTable.setOutputMarkupId(true);
        roleContainer.add(roleTable);

        //Init Org. Unit Container
        WebMarkupContainer orgUnitContainer = new WebMarkupContainer(ID_ORG_CONTAINER);
        orgUnitContainer.setOutputMarkupId(true);
        orgUnitContainer.setOutputMarkupPlaceholderTag(true);
        mainForm.add(orgUnitContainer);

        AjaxLink addOrgUnit = new AjaxLink(ID_BUTTON_ADD_ORG) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                orgUnitAdditionPerformed(target);
            }
        };
        orgUnitContainer.add(addOrgUnit);

        List<IColumn> orgUnitColumns = createOrgUnitColumns();

        AssignableDataProvider<OrgType, UserType> orgUnitProvider = new AssignableDataProvider<>(getPage(),
                OrgType.class, model.getObject().getUser());

        TablePanel orgUnitTable = new TablePanel(ID_TABLE_ORG, orgUnitProvider, orgUnitColumns, 10);
        orgUnitTable.add(new VisibleEnableBehavior(){

            @Override
            public boolean isVisible() {
                return model.getObject().getUser() != null && !model.getObject().getUser().getOrgUnitAssignments().isEmpty();

            }
        });
        orgUnitTable.setOutputMarkupId(true);
        orgUnitContainer.add(orgUnitTable);

        //Init Account Assignments
        WebMarkupContainer accountContainer = new WebMarkupContainer(ID_ACCOUNT_CONTAINER);
        accountContainer.setOutputMarkupId(true);
        accountContainer.setOutputMarkupPlaceholderTag(true);
        mainForm.add(accountContainer);

        AjaxLink addAccount = new AjaxLink(ID_BUTTON_ADD_ACCOUNT) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                accountAdditionPerformed(target);
            }
        };
        accountContainer.add(addAccount);

        List<IColumn> accountColumns = createAccountColumns();

        AssignableDataProvider<AccountType, UserType> accountProvider = new AssignableDataProvider<AccountType, UserType>(getPage(),
                AccountType.class, model.getObject().getUser()){

            @Override
            public Iterator<AccountType> iterator(long first, long count) {
                getData().clear();
                getCurrentPageData().clear();

                try {

                    UserType user = getAssignmentSource();

                    for(AssignmentType assignment: user.getAccounts()){
                        if(assignment.getUid() != null && assignment.getFederationIdentifier() == null){
                            AccountType account = getModelService().readObject(AccountType.class, assignment.getUid());
                            getData().add(account);
                        } else if(assignment.getUid() == null && assignment.getFederationIdentifier() != null){
                            FederationIdentifierType identifier = assignment.getFederationIdentifier();
                            FederationMemberType member = WebMiscUtil.getFederationMemberByName(identifier.getFederationMemberId());

                            ObjectTypeRestResponse response = PageUser.this.getFederationServiceClient()
                                    .createGetAccountRequest(member, identifier.getUniqueAttributeValue());

                            if(response.getStatus() == HttpStatus.OK_200){
                                AccountType account = (AccountType)response.getValue();
                                getData().add(account);
                            }
                        }
                    }

                } catch (Exception e){
                    LOGGER.error("Could not create an iterator object for data of type: '" + getType().getSimpleName() + "'. Could not read objects from model.");
                }

                return getData().iterator();
            }
        };

        TablePanel accountTable = new TablePanel(ID_TABLE_ACCOUNTS, accountProvider, accountColumns, 10);
        accountTable.add(new VisibleEnableBehavior(){

            @Override
            public boolean isVisible() {
                return model.getObject().getUser() != null && !model.getObject().getUser().getAccounts().isEmpty();

            }
        });
        accountTable.setOutputMarkupId(true);
        accountContainer.add(accountTable);
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

    private List<IColumn> createOrgUnitColumns(){
        List<IColumn> columns = new ArrayList<>();

        columns.add(new PropertyColumn<OrgType, String>(new Model<>("Name"), "name", "name"));
        columns.add(new PropertyColumn<OrgType, String>(new Model<>("Display Name"), "displayName", "displayName"));
        columns.add(new PropertyColumn<OrgType, String>(new Model<>("Org. Type"), "orgType", "orgType"));
        columns.add(new PropertyColumn<OrgType, String>(new Model<>("Locality"), "locality", "locality"));
        columns.add(new EditDeleteButtonColumn<OrgType>(new Model<>("Actions")) {

            @Override
            public void editPerformed(AjaxRequestTarget target, IModel<OrgType> rowModel) {
                PageUser.this.editOrgUnitPerformed(target, rowModel);
            }

            @Override
            public void removePerformed(AjaxRequestTarget target, IModel<OrgType> rowModel) {
                PageUser.this.removeOrgUnitPerformed(target, rowModel);
            }
        });

        return columns;
    }

    private List<IColumn> createAccountColumns(){
        List<IColumn> columns = new ArrayList<>();

        columns.add(new PropertyColumn<AccountType, String>(new Model<>("Name"), "name", "name"));
        columns.add(new AbstractColumn<AccountType, String>(new Model<>("Origin")) {

            @Override
            public void populateItem(Item<ICellPopulator<AccountType>> cellItem, String componentId, IModel<AccountType> rowModel) {
                if(rowModel == null || rowModel.getObject() == null){
                    return;
                }

                AccountType account = rowModel.getObject();

                if(account.getUid() == null){
                    cellItem.add(new Label(componentId, "Remote"));
                } else {
                    cellItem.add(new Label(componentId, "Local"));
                }
            }
        });

        columns.add(new AbstractColumn<AccountType, String>(new Model<>("Resource")) {


            @Override
            public void populateItem(Item<ICellPopulator<AccountType>> cellItem, String componentId, IModel<AccountType> rowModel) {
                if(rowModel == null || rowModel.getObject() == null){
                    return;
                }

                AccountType account = rowModel.getObject();
                ObjectReferenceType resourceReference = account.getResource();
                String resourceUid = resourceReference.getUid();

                if(resourceUid == null){
                    cellItem.add(new Label(componentId, resourceReference.getFederationIdentifier().getUniqueAttributeValue()));
                    return;
                }

                ResourceType resource;

                try {
                    resource = getModelService().readObject(ResourceType.class, resourceUid);
                    cellItem.add(new Label(componentId, resource.getName()));
                } catch (DatabaseCommunicationException exc) {
                    error("Couldn't retrieve resource with oid: '" + resourceUid + "' from the database. Reason: " + exc.getExceptionMessage());
                    LOGGER.error("Couldn't retrieve resource with oid: '" + resourceUid + "' from the database. Reason: ", exc);
                }
            }
        });

        columns.add(new EditDeleteButtonColumn<AccountType>(new Model<>("Actions")){

            @Override
            public void editPerformed(AjaxRequestTarget target, IModel<AccountType> rowModel) {
                PageUser.this.editAccountPerformed(target, rowModel);
            }

            @Override
            public void removePerformed(AjaxRequestTarget target, IModel<AccountType> rowModel) {
                PageUser.this.removeAccountAssignmentPerformed(target, rowModel);
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

    public WebMarkupContainer getOrgContainer(){
        return (WebMarkupContainer) get(ID_MAIN_FORM + ":" + ID_ORG_CONTAINER);
    }

    public WebMarkupContainer getAccountContainer(){
        return (WebMarkupContainer) get(ID_MAIN_FORM + ":" + ID_ACCOUNT_CONTAINER);
    }

    private void addRoleAssignmentPerformed(AjaxRequestTarget target, IModel<RoleType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            return;
        }

        RoleType role = rowModel.getObject();
        AssignmentType roleAssignment = new AssignmentType(role.getUid());
        model.getObject().getUser().getRoleAssignments().add(roleAssignment);

        ModalWindow dialog = (ModalWindow) get(ID_ROLE_ASSIGNABLE_POPUP);
        dialog.close(target);
        target.add(getRoleContainer());
    }

    private void addOrgUnitPerformed(AjaxRequestTarget target, IModel<OrgType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            return;
        }

        OrgType org = rowModel.getObject();
        AssignmentType roleAssignment = new AssignmentType(org.getUid());
        model.getObject().getUser().getOrgUnitAssignments().add(roleAssignment);

        ModalWindow dialog = (ModalWindow) get(ID_ORG_ASSIGNABLE_POPUP);
        dialog.close(target);
        target.add(getOrgContainer());
    }

    private void addAccountPerformed(AjaxRequestTarget target, IModel<ResourceType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            return;
        }

        ResourceType resource = rowModel.getObject();
        AccountType account = new AccountType();

        ObjectReferenceType ownerReference = new ObjectReferenceType(model.getObject().getUser().getUid());
        account.setOwner(ownerReference);

        ObjectReferenceType resourceReference = new ObjectReferenceType(resource.getUid());
        account.setResource(resourceReference);

        try {
            account = getModelService().createObject(account);

        } catch (ObjectAlreadyExistsException e) {
            LOGGER.error("Can't add account to user.: ", e);
            error("Can't add account to user. Account already exists. Reason: " + e.getExceptionMessage());
        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Can't add account to user: ", e);
            error("Can't add account to the user. Reason: " + e.getExceptionMessage());
        }

        AssignmentType accountAssignment = new AssignmentType(account.getUid());
        model.getObject().getUser().getAccounts().add(accountAssignment);
        LOGGER.info("Account " + account.getUid() + " added to the user.");

        ModalWindow dialog = (ModalWindow) get(ID_ACCOUNT_POPUP);
        dialog.close(target);
        target.add(getAccountContainer());
    }

    private void roleAdditionPerformed(AjaxRequestTarget target){
        ModalWindow modal = (ModalWindow) get(ID_ROLE_ASSIGNABLE_POPUP);
        modal.show(target);
    }

    private void orgUnitAdditionPerformed(AjaxRequestTarget target){
        ModalWindow modal = (ModalWindow) get(ID_ORG_ASSIGNABLE_POPUP);
        modal.show(target);
    }

    private void accountAdditionPerformed(AjaxRequestTarget target){
        ModalWindow modal = (ModalWindow) get(ID_ACCOUNT_POPUP);
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

        AssignmentType toRemove = new AssignmentType();
        for(AssignmentType roleRef: model.getObject().getUser().getRoleAssignments()){
            if(roleRef.getUid().equals(roleUid)){
                toRemove = roleRef;
                break;
            }
        }

        model.getObject().getUser().getRoleAssignments().remove(toRemove);
        target.add(getRoleContainer());
    }

    private void editOrgUnitPerformed(AjaxRequestTarget target, IModel<OrgType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            error("Couldn't edit selected org. unit. It is no longer available.");
            target.add(getFeedbackPanel());
            return;
        }

        PageParameters parameters = new PageParameters();
        parameters.add(UID_PAGE_PARAMETER_NAME, rowModel.getObject().getUid());
        setResponsePage(PageOrg.class, parameters);
    }

    private void removeOrgUnitPerformed(AjaxRequestTarget target, IModel<OrgType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            error("Couldn't remove selected org. unit assignment. Something went wrong.");
            target.add(getFeedbackPanel());
            return;
        }

        String orgUid = rowModel.getObject().getUid();

        AssignmentType toRemove = new AssignmentType();
        for(AssignmentType orgRef: model.getObject().getUser().getOrgUnitAssignments()){
            if(orgRef.getUid().equals(orgUid)){
                toRemove = orgRef;
                break;
            }
        }

        model.getObject().getUser().getOrgUnitAssignments().remove(toRemove);
        target.add(getOrgContainer());
    }

    private void editAccountPerformed(AjaxRequestTarget target, IModel<AccountType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            error("Couldn't edit selected account. It is no longer available.");
            target.add(getFeedbackPanel());
            return;
        }

        AccountType account = rowModel.getObject();

        if(account.getUid() != null) {
            PageParameters parameters = new PageParameters();
            parameters.add(UID_PAGE_PARAMETER_NAME, rowModel.getObject().getUid());
            setResponsePage(PageAccount.class, parameters);
        } else {
            RemoteAccountViewerDialog window = (RemoteAccountViewerDialog) get(ID_REMOTE_ACCOUNT_VIEWER);
            window.updateModel(account);
            window.show(target);
        }
    }

    /**
     *  TODO - provide user with option to remove only assignment, or to archive/disable account etc.
     * */
    private void removeAccountAssignmentPerformed(AjaxRequestTarget target, IModel<AccountType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            error("Couldn't remove selected account assignment. Something went wrong.");
            target.add(getFeedbackPanel());
            return;
        }

        String accountUid = rowModel.getObject().getUid();

        if(accountUid != null){
            try {
                AccountType acc = getModelService().readObject(AccountType.class, accountUid);
                getModelService().deleteObject(acc);
            } catch (DatabaseCommunicationException | ObjectNotFoundException e) {
                LOGGER.error("Could not retrieve account with uid: '" + accountUid + "' of user with uid: '" + model.getObject().getUser().getUid() + "'.");
                target.add(getFeedbackPanel());
            }

            AssignmentType toRemove = new AssignmentType();
            for(AssignmentType accountRef: model.getObject().getUser().getAccounts()){
                if(accountRef.getUid().equals(accountUid)){
                    toRemove = accountRef;
                    break;
                }
            }

            model.getObject().getUser().getAccounts().remove(toRemove);
            target.add(getAccountContainer());
        } else {
            FederationIdentifierType identifier = rowModel.getObject().getFederationIdentifier();
            FederationMemberType member = WebMiscUtil.getFederationMemberByName(identifier.getFederationMemberId());

            try {
                SimpleRestResponse response = getFederationServiceClient()
                        .createRemoveAccountRequest(member, identifier.getUniqueAttributeValue());

                if(response.getStatus() == HttpStatus.OK_200){
                    AssignmentType toRemove = new AssignmentType();
                    for(AssignmentType accountRef: model.getObject().getUser().getAccounts()){
                        if(accountRef.getFederationIdentifier().getUniqueAttributeValue().equals(identifier.getUniqueAttributeValue())){
                            toRemove = accountRef;
                            break;
                        }
                    }

                    model.getObject().getUser().getAccounts().remove(toRemove);
                    target.add(getAccountContainer());
                } else {
                    LOGGER.error("Could not remove remote account with uid: '" + identifier.getUniqueAttributeValue()  +
                            "' of user with uid: '" + model.getObject().getUser().getUid() + "'.");
                    target.add(getFeedbackPanel());
                }

            } catch (DatabaseCommunicationException | NoSuchFieldException | IllegalAccessException e) {
                LOGGER.error("Could not remove remote account with uid: '" + identifier.getUniqueAttributeValue()  +
                        "' of user with uid: '" + model.getObject().getUser().getUid() + "'.");
                target.add(getFeedbackPanel());
            }
        }
    }

    private void cancelPerformed(){
        setResponsePage(PageUserList.class);
    }

    private void recomputePerformed(AjaxRequestTarget target){
        if(model == null || model.getObject() == null){
            return;
        }

        UserTypeDto userDto = model.getObject();
        UserType user = userDto.getUser();

        if(isEditingUser()){
            try {
                getModelService().recomputeUser(user);
                success("Recompute operation on user: '" + user.getName() + "'(" + user.getUid() + ") was successful.");
            } catch (DatabaseCommunicationException | ObjectNotFoundException e) {
                LOGGER.error("Can't recompute user: ", e);
                error("Can't recompute user with name: '" + user.getName() + "'. Reason: " + e.getExceptionMessage());
           }
        } else {
            warn("It is not possible to recompute user that is not created yet.");
        }

        target.add(this, getFeedbackPanel());
    }

    private void savePerformed(AjaxRequestTarget target){
        ModelService modelService = (ModelService) getModelService();
        UserTypeDto userDto;
        UserType user;

        if(model == null || model.getObject() == null){
            return;
        }

        userDto = model.getObject();
        user = userDto.getUser();

        if(userDto.getPassword().equals(userDto.getPasswordConfirm())){
            user.setPassword(userDto.getPassword());
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
