package com.esuta.fidm.gui.page.resource;

import com.esuta.fidm.gui.component.data.ObjectDataProvider;
import com.esuta.fidm.gui.component.data.column.EditDeleteButtonColumn;
import com.esuta.fidm.gui.component.data.column.LinkColumn;
import com.esuta.fidm.gui.component.data.table.TablePanel;
import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.gui.page.users.PageAccount;
import com.esuta.fidm.gui.page.users.PageUser;
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
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 * */
public class PageResource extends PageBase {

    private transient Logger LOGGER = Logger.getLogger(PageResource.class);

    public static final String ID_MAIN_FORM = "mainForm";
    public static final String ID_NAME = "name";
    public static final String ID_DESCRIPTION = "description";
    public static final String ID_TYPE = "type";
    public static final String ID_BUTTON_SAVE = "saveButton";
    public static final String ID_BUTTON_CANCEL = "cancelButton";

    private static final String ID_ACCOUNT_CONTAINER = "accountContainer";
    private static final String ID_BUTTON_ADD_ACCOUNT = "addAccount";
    private static final String ID_TABLE_ACCOUNTS = "accountTable";

    private IModel<ResourceType> model;

    public PageResource(){
        this(null);
    }

    public PageResource(PageParameters parameters){
        super(parameters);

        model = new LoadableModel<ResourceType>(false) {

            @Override
            protected ResourceType load() {
                return loadResource();
            }
        };

        initLayout();
    }

    private ResourceType loadResource(){
        if(!isEditingResource()){
            return new ResourceType();
        }

        PageParameters parameters = getPageParameters();
        String uid = parameters.get(UID_PAGE_PARAMETER_NAME).toString();
        ResourceType resource;

        try {
            resource = getModelService().readObject(ResourceType.class, uid);
        } catch (DatabaseCommunicationException exc){
            error("Couldn't retrieve resource with oid: '" + uid + "' from the repository. Reason: " + exc.getExceptionMessage());
            LOGGER.error("Couldn't retrieve resource with oid: '" + uid + "' from the repository. Reason: ", exc);
            throw new RestartResponseException(PageResourceList.class);
        }

        return resource;
    }

    private boolean isEditingResource(){
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

        TextField type = new TextField<>(ID_TYPE, new PropertyModel<String>(model, "resourceType"));
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

        initResourceAccountsPanel(mainForm);
    }

    private WebMarkupContainer getAccountsContainer(){
        return (WebMarkupContainer) get(ID_MAIN_FORM + ":" + ID_ACCOUNT_CONTAINER);
    }

    private void initResourceAccountsPanel(Form mainForm){
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

        ObjectDataProvider<AccountType> accountProvider = new ObjectDataProvider<AccountType>(getPage(), AccountType.class){

            @Override
            public List<AccountType> applyDataFilter(List<AccountType> list) {
                return resourceAccountDataFilter(list);
            }
        };

        TablePanel accountTable = new TablePanel(ID_TABLE_ACCOUNTS, accountProvider, accountColumns, 10);
        accountTable.setOutputMarkupId(true);
        accountContainer.add(accountTable);
    }

    private List<IColumn> createAccountColumns(){
        List<IColumn> columns = new ArrayList<>();

        columns.add(new PropertyColumn<AccountType, String>(new Model<>("Name"), "accountName", "accountName"));
        columns.add(new LinkColumn<AccountType>(new Model<>("Owner"), "owner"){

            @Override
            protected IModel<String> createLinkModel(IModel<AccountType> rowModel) {
                return createOwnerLinkModel(rowModel);
            }

            @Override
            public void onClick(AjaxRequestTarget target, IModel<AccountType> rowModel) {
                accountOwnerEditPerformed(target, rowModel);
            }
        });
        columns.add(new EditDeleteButtonColumn<AccountType>(new Model<>("Actions")){

            @Override
            public void editPerformed(AjaxRequestTarget target, IModel<AccountType> rowModel) {
                PageResource.this.editAccountPerformed(target, rowModel);
            }

            @Override
            public void removePerformed(AjaxRequestTarget target, IModel<AccountType> rowModel) {
                PageResource.this.removeAccountAssignmentPerformed(target, rowModel);
            }
        });

        return columns;
    }

    private List<AccountType> resourceAccountDataFilter(List<AccountType> list){
        List<AccountType> newList = new ArrayList<>();

        //return empty list when we are creating new resource
        if(model == null || model.getObject() == null || model.getObject().getUid() == null){
            return newList;
        }

        String resourceUid = model.getObject().getUid();

        for(AccountType acc: list){
            if(resourceUid.equals(acc.getResource())){
                newList.add(acc);
            }
        }

        return newList;
    }

    private IModel<String> createOwnerLinkModel(final IModel<AccountType> rowModel){
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                String userUid = rowModel.getObject().getOwner();
                UserType owner = null;

                try {
                    owner = getModelService().readObject(UserType.class, userUid);
                } catch (DatabaseCommunicationException e) {
                    LOGGER.error("Could not load user with oid: '" + userUid + "' from the repository.");
                }

                return owner != null ? owner.getName() : null;
            }
        };
    }

    private void editAccountPerformed(AjaxRequestTarget target, IModel<AccountType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            error("Couldn't edit selected account. It is no longer available.");
            target.add(getFeedbackPanel());
            return;
        }

        PageParameters parameters = new PageParameters();
        parameters.add(UID_PAGE_PARAMETER_NAME, rowModel.getObject().getUid());
        setResponsePage(new PageAccount(parameters));
    }

    private void removeAccountAssignmentPerformed(AjaxRequestTarget target, IModel<AccountType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            error("Object selected to delete does not exist.");
            target.add(getFeedbackPanel());
            return;
        }

        AccountType account = rowModel.getObject();
        String accountName = account.getName();

        try {
            getModelService().deleteObject(account);

            //We also have to remove account reference from its owner
            String ownerUid = account.getOwner();

            if(ownerUid != null || StringUtils.isNotEmpty(ownerUid)){
                UserType owner = getModelService().readObject(UserType.class, ownerUid);
                owner.getAccounts().remove(ownerUid);
                getModelService().updateObject(owner);
            }

        } catch (GeneralException e){
            LOGGER.error("Could not delete account: '" + accountName + "'. Reason: ", e);
            error("Could not delete account: '" + accountName + "'. Reason: " + e.getExceptionMessage());
            target.add(getFeedbackPanel());
            return;
        }

        LOGGER.info("Account '" + accountName + "' was successfully deleted from the system.");
        success("Account '" + accountName + "' was successfully deleted from the system.");
        target.add(getFeedbackPanel(), getAccountsContainer());
    }

    private void accountOwnerEditPerformed(AjaxRequestTarget target, IModel<AccountType> rowModel){
        PageParameters parameters = new PageParameters();
        parameters.add(UID_PAGE_PARAMETER_NAME, rowModel.getObject().getOwner());
        setResponsePage(new PageUser(parameters));
    }

    private void accountAdditionPerformed(AjaxRequestTarget target){
        if(model == null || model.getObject() == null || model.getObject().getUid() == null ||
                StringUtils.isEmpty(model.getObject().getUid())){
            warn("Can't add accounts to currently created resource. Save it and then add account to it.");
            target.add(getFeedbackPanel());
        }

        PageParameters parameters = new PageParameters();
        parameters.add(PAGE_ACCOUNT_RESOURCE_UID, model.getObject().getUid());
        setResponsePage(new PageAccount(parameters));
    }

    private void cancelPerformed(){
        setResponsePage(PageResourceList.class);
    }

    private void savePerformed(AjaxRequestTarget target){
        ModelService modelService = getModelService();
        ResourceType resource;

        if(model == null || model.getObject() == null){
            return;
        }

        resource = model.getObject();

        try{

            if(!isEditingResource()){
                modelService.createObject(resource);
            } else {
                modelService.updateObject(resource);
            }

        } catch (GeneralException e){
            LOGGER.error("Can't add resource: ", e);
            error("Can't add resource with name: '" + resource.getName() + "'. Reason: " + e.getExceptionMessage());
        }

        getSession().success("Resource '" + resource.getName() + "' has been saved successfully.");
        LOGGER.info("Resource '" + resource.getName() + "' has been saved successfully.");
        setResponsePage(PageResourceList.class);
        target.add(getFeedbackPanel());
    }
}
