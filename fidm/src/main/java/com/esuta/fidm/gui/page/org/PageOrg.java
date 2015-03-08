package com.esuta.fidm.gui.page.org;

import com.esuta.fidm.gui.component.behavior.VisibleEnableBehavior;
import com.esuta.fidm.gui.component.data.ObjectDataProvider;
import com.esuta.fidm.gui.component.data.column.EditDeleteButtonColumn;
import com.esuta.fidm.gui.component.data.table.TablePanel;
import com.esuta.fidm.gui.component.form.MultiValueTextEditPanel;
import com.esuta.fidm.gui.component.form.MultiValueTextPanel;
import com.esuta.fidm.gui.component.modal.ObjectChooserDialog;
import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.gui.page.resource.PageResource;
import com.esuta.fidm.gui.page.roles.PageRole;
import com.esuta.fidm.gui.page.users.PageUser;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.GeneralException;
import com.esuta.fidm.infra.exception.ObjectNotFoundException;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.repository.schema.core.OrgType;
import com.esuta.fidm.repository.schema.core.ResourceType;
import com.esuta.fidm.repository.schema.core.RoleType;
import com.esuta.fidm.repository.schema.core.UserType;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 * */
public class PageOrg extends PageBase {

    private transient Logger LOGGER = Logger.getLogger(PageOrg.class);

    private static final String ID_MAIN_FORM = "mainForm";
    private static final String ID_NAME = "name";
    private static final String ID_DISPLAY_NAME = "displayName";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_LOCALITY = "locality";
    private static final String ID_ORG_TYPE = "orgType";
    private static final String ID_PARENT_ORG_UNIT = "parentOrgUnits";

    private static final String ID_FEDERATION_CONTAINER = "federationContainer";
    private static final String ID_SHARE_IN_FEDERATION = "sharedInFederation";
    private static final String ID_SHARE_SUBTREE = "sharedSubtree";
    private static final String ID_OVERRIDE_SHARING = "overrideParentSharing";

    private static final String ID_BUTTON_SAVE = "saveButton";
    private static final String ID_BUTTON_RECOMPUTE = "recomputeButton";
    private static final String ID_BUTTON_CANCEL = "cancelButton";

    private static final String ID_RESOURCE_IND_CONTAINER = "resourceInducementsContainer";
    private static final String ID_RESOURCE_IND_BUTTON_ADD = "addResourceInducement";
    private static final String ID_RESOURCE_IND_TABLE = "resourceInducementsTable";
    private static final String ID_ROLE_IND_CONTAINER = "roleInducementsContainer";
    private static final String ID_ROLE_IND_BUTTON_ADD = "addRoleInducement";
    private static final String ID_ROLE_IND_TABLE = "roleInducementsTable";
    private static final String ID_MEMBERS_CONTAINER = "memberContainer";
    private static final String ID_MEMBERS_TABLE = "membersTable";
    private static final String ID_GOVERNORS_CONTAINER = "governorsContainer";
    private static final String ID_BUTTON_ADD_GOVERNOR = "addGovernor";
    private static final String ID_GOVERNORS_TABLE = "governorsTable";

    private static final String ID_PARENT_ORG_UNIT_CHOOSER = "parentOrgUnitChooser";
    private static final String ID_GOVERNOR_CHOOSER = "governorChooser";
    private static final String ID_RESOURCE_INDUCEMENT_CHOOSER = "resourceInducementChooser";
    private static final String ID_ROLE_INDUCEMENT_CHOOSER = "roleInducementChooser";

    private IModel<OrgType> model;

    public PageOrg(){
        this(null);
    }

    public PageOrg(PageParameters parameters){
        super(parameters);

        model = new LoadableModel<OrgType>(false) {
            @Override
            protected OrgType load() {
                return loadOrgUnit();
            }
        };

        initLayout();
    }

    private OrgType loadOrgUnit(){
        if(!isEditingOrgUnit()){
            return new OrgType();
        }

        PageParameters parameters = getPageParameters();
        String uid = parameters.get(UID_PAGE_PARAMETER_NAME).toString();
        OrgType role;

        try {
            role = getModelService().readObject(OrgType.class, uid);
        } catch (DatabaseCommunicationException exc){
            error("Couldn't retrieve org. unit with oid: '" + uid + "' from the repository. Reason: " + exc.getExceptionMessage());
            LOGGER.error("Couldn't retrieve org. unit with oid: '" + uid + "' from the repository. Reason: ", exc);
            throw new RestartResponseException(PageOrgList.class);
        }

        return role;
    }

    private boolean isEditingOrgUnit(){
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

        TextField locality = new TextField<>(ID_LOCALITY, new PropertyModel<String>(model, "locality"));
        mainForm.add(locality);

        MultiValueTextPanel orgType = new MultiValueTextPanel<>(ID_ORG_TYPE,
                new PropertyModel<List<String>>(model, "orgType"), false);
        mainForm.add(orgType);

        MultiValueTextEditPanel parentOrgUnit = new MultiValueTextEditPanel<String>(ID_PARENT_ORG_UNIT,
                new PropertyModel<List<String>>(model, "parentOrgUnits"), false, true){

            @Override
            protected IModel<String> createTextModel(IModel<String> model) {
                return createParentOrgUnitDisplayModel(model);
            }

            @Override
            protected String createNewEmptyItem() {
                return "";
            }

            @Override
            protected void editPerformed(AjaxRequestTarget target, String object) {
                PageOrg.this.editParentOrgUnitPerformed(target);
            }
        };
        mainForm.add(parentOrgUnit);

        WebMarkupContainer federationContainer = new WebMarkupContainer(ID_FEDERATION_CONTAINER);
        federationContainer.setOutputMarkupId(true);
        federationContainer.add(new VisibleEnableBehavior(){

            @Override
            public boolean isVisible() {
                //If the org. unit is not in local federation member, the section
                //where federation options are edited should not be visible at all.
                return model.getObject().getFederationIdentifier() == null;
            }
        });
        mainForm.add(federationContainer);

        CheckBox sharedInFederation = new CheckBox(ID_SHARE_IN_FEDERATION, new PropertyModel<Boolean>(model, "sharedInFederation"));
        sharedInFederation.add(new OnChangeAjaxBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(getFederationContainer());
            }
        });
        federationContainer.add(sharedInFederation);

        CheckBox shareSubtree = new CheckBox(ID_SHARE_SUBTREE, new PropertyModel<Boolean>(model, "sharedSubtree"));
        shareSubtree.add(new VisibleEnableBehavior(){

            @Override
            public boolean isEnabled() {
                return model.getObject().isSharedInFederation();
            }
        });
        federationContainer.add(shareSubtree);

        CheckBox overrideSharing = new CheckBox(ID_OVERRIDE_SHARING, new PropertyModel<Boolean>(model, "overrideParentSharing"));
        overrideSharing.add(new VisibleEnableBehavior(){

            @Override
            public boolean isEnabled() {
                //Can't override parent sharing when the org. unit is root.
                if(model.getObject().getParentOrgUnits().isEmpty()){
                    return false;
                }

                return model.getObject().isSharedInFederation();
            }
        });
        federationContainer.add(overrideSharing);

        initButtons(mainForm);
        initModalWindows();
        initInducements(mainForm);
        initContainers(mainForm);
    }

    private void initButtons(Form mainForm){
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
                return isEditingOrgUnit();
            }
        });
        mainForm.add(recompute);

    }

    private void initModalWindows(){
        ModalWindow resourceInducementChooser = new ObjectChooserDialog<ResourceType>(ID_RESOURCE_INDUCEMENT_CHOOSER, ResourceType.class){

            @Override
            public void objectChoosePerformed(AjaxRequestTarget target, IModel<ResourceType> rowModel) {
                resourceInducementChoosePerformed(target, rowModel);
            }

            @Override
            public String getChooserTitle() {
                return "Choose Resource Inducement";
            }

            @Override
            public List<ResourceType> applyObjectFilter(List<ResourceType> list) {
                return applyResourceInducementChooserFilter(list);
            }
        };
        add(resourceInducementChooser);

        ModalWindow roleInducementChooser = new ObjectChooserDialog<RoleType>(ID_ROLE_INDUCEMENT_CHOOSER, RoleType.class){

            @Override
            public void objectChoosePerformed(AjaxRequestTarget target, IModel<RoleType> rowModel) {
                roleInducementChoosePerformed(target, rowModel);
            }

            @Override
            public String getChooserTitle() {
                return "Choose Role Inducement";
            }

            @Override
            public List<RoleType> applyObjectFilter(List<RoleType> list) {
                return applyRoleInducementChooserFilter(list);
            }
        };
        add(roleInducementChooser);

        ModalWindow parentOrgUnitChooser = new ObjectChooserDialog<OrgType>(ID_PARENT_ORG_UNIT_CHOOSER, OrgType.class){

            @Override
            public void objectChoosePerformed(AjaxRequestTarget target, IModel<OrgType> rowModel) {
                parentOrgUnitChoosePerformed(target, rowModel);
            }

            @Override
            public String getChooserTitle() {
                return "Choose parent org. unit";
            }

            @Override
            public List<OrgType> applyObjectFilter(List<OrgType> list) {
                return applyParentOrgChooserFilter(list);
            }
        };
        add(parentOrgUnitChooser);

        ModalWindow governorChooser = new ObjectChooserDialog<UserType>(ID_GOVERNOR_CHOOSER, UserType.class){

            @Override
            public void objectChoosePerformed(AjaxRequestTarget target, IModel<UserType> rowModel) {
                governorChoosePerformed(target, rowModel);
            }

            @Override
            public String getChooserTitle() {
                return "Choose parent org. unit";
            }

            @Override
            public List<UserType> applyObjectFilter(List<UserType> list) {
                return applyGovernorChooserFilter(list);
            }
        };
        add(governorChooser);
    }

    private void initInducements(Form mainForm){
        //Resource Inducements Container
        WebMarkupContainer resourceInducementsContainer = new WebMarkupContainer(ID_RESOURCE_IND_CONTAINER);
        resourceInducementsContainer.setOutputMarkupId(true);
        resourceInducementsContainer.setOutputMarkupPlaceholderTag(true);
        mainForm.add(resourceInducementsContainer);

        AjaxLink addResourceInducement = new AjaxLink(ID_RESOURCE_IND_BUTTON_ADD) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                addResourceInducementPerformed(target);
            }
        };
        resourceInducementsContainer.add(addResourceInducement);

        List<IColumn> resourceInducementColumns = createResourceInducementColumns();
        final ObjectDataProvider resourceInducementsProvider = new ObjectDataProvider<ResourceType>(getPage(), ResourceType.class){

            @Override
            public List<ResourceType> applyDataFilter(List<ResourceType> list) {
                List<ResourceType> resourceInducementList = new ArrayList<>();

                if(model != null && model.getObject() != null){
                    OrgType org = model.getObject();

                    for(ResourceType resource: list){
                        if(org.getResourceInducements().contains(resource.getUid())){
                            resourceInducementList.add(resource);
                        }
                    }
                }

                return resourceInducementList;
            }
        };

        TablePanel resourceInducementsTable = new TablePanel(ID_RESOURCE_IND_TABLE, resourceInducementsProvider, resourceInducementColumns, 10);
        resourceInducementsTable.add(new VisibleEnableBehavior(){

            @Override
            public boolean isVisible() {
                return resourceInducementsProvider.size() > 0;
            }
        });
        resourceInducementsTable.setShowPaging(false);
        resourceInducementsTable.setOutputMarkupId(true);
        resourceInducementsContainer.add(resourceInducementsTable);

        //Role Inducements Container
        WebMarkupContainer roleInducementsContainer = new WebMarkupContainer(ID_ROLE_IND_CONTAINER);
        roleInducementsContainer.setOutputMarkupId(true);
        roleInducementsContainer.setOutputMarkupPlaceholderTag(true);
        mainForm.add(roleInducementsContainer);

        AjaxLink addRoleInducement = new AjaxLink(ID_ROLE_IND_BUTTON_ADD) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                addRoleInducementPerformed(target);
            }
        };
        roleInducementsContainer.add(addRoleInducement);

        List<IColumn> roleInducementColumns = createRoleInducementColumns();
        final ObjectDataProvider roleInducementsProvider = new ObjectDataProvider<RoleType>(getPage(), RoleType.class){

            @Override
            public List<RoleType> applyDataFilter(List<RoleType> list) {
                List<RoleType> roleInducementList = new ArrayList<>();

                if(model != null && model.getObject() != null){
                    OrgType org = model.getObject();

                    for(RoleType role: list){
                        if(org.getRoleInducements().contains(role.getUid())){
                            roleInducementList.add(role);
                        }
                    }
                }

                return roleInducementList;
            }
        };

        TablePanel roleInducementsTable = new TablePanel(ID_ROLE_IND_TABLE, roleInducementsProvider, roleInducementColumns, 10);
        roleInducementsTable.add(new VisibleEnableBehavior() {

            @Override
            public boolean isVisible() {
                return roleInducementsProvider.size() > 0;
            }
        });
        roleInducementsTable.setOutputMarkupId(true);
        roleInducementsTable.setShowPaging(false);
        roleInducementsContainer.add(roleInducementsTable);
    }

    private List<IColumn> createResourceInducementColumns(){
        List<IColumn> columns = new ArrayList<>();

        columns.add(new PropertyColumn<ResourceType, String>(new Model<>("Name"), "name", "name"));
        columns.add(new PropertyColumn<ResourceType, String>(new Model<>("Type"), "resourceType", "resourceType"));
        columns.add(new EditDeleteButtonColumn<ResourceType>(new Model<>("Actions")){

            @Override
            public void editPerformed(AjaxRequestTarget target, IModel<ResourceType> rowModel) {
                PageOrg.this.editResourceInducementPerformed(target, rowModel);
            }

            @Override
            public void removePerformed(AjaxRequestTarget target, IModel<ResourceType> rowModel) {
                PageOrg.this.removeResourceInducementPerformed(target, rowModel);
            }
        });

        return columns;
    }

    private List<IColumn> createRoleInducementColumns(){
        List<IColumn> columns = new ArrayList<>();

        columns.add(new PropertyColumn<RoleType, String>(new Model<>("Name"), "name", "name"));
        columns.add(new PropertyColumn<RoleType, String>(new Model<>("DisplayName"), "displayName", "displayName"));
        columns.add(new PropertyColumn<RoleType, String>(new Model<>("Type"), "roleType", "roleType"));
        columns.add(new EditDeleteButtonColumn<RoleType>(new Model<>("Actions")){

            @Override
            public void editPerformed(AjaxRequestTarget target, IModel<RoleType> rowModel) {
                PageOrg.this.editRoleInducementPerformed(target, rowModel);
            }

            @Override
            public void removePerformed(AjaxRequestTarget target, IModel<RoleType> rowModel) {
                PageOrg.this.removeRoleInducementPerformed(target, rowModel);
            }
        });

        return columns;
    }

    private void initContainers(Form mainForm){
        //Members Container
        WebMarkupContainer membersContainer = new WebMarkupContainer(ID_MEMBERS_CONTAINER);
        membersContainer.setOutputMarkupId(true);
        membersContainer.setOutputMarkupPlaceholderTag(true);
        mainForm.add(membersContainer);

        List<IColumn> membersColumns = createMemberColumns();
        final ObjectDataProvider membersProvider = new ObjectDataProvider<UserType>(getPage(), UserType.class){

            @Override
            public List<UserType> applyDataFilter(List<UserType> list) {
                List<UserType> memberList = new ArrayList<>();

                if(model != null && model.getObject() != null){
                    String orgUid = model.getObject().getUid();

                    for(UserType user: list){
                        if(user.getOrgUnitAssignments().contains(orgUid)){
                            memberList.add(user);
                        }
                    }
                }

                return memberList;
            }
        };

        TablePanel membersTable = new TablePanel(ID_MEMBERS_TABLE, membersProvider, membersColumns, 10);
        membersTable.add(new VisibleEnableBehavior(){

            @Override
            public boolean isVisible() {
                return membersProvider.size() > 0;
            }
        });
        membersTable.setOutputMarkupId(true);
        membersContainer.add(membersTable);

        //Governors Container
        WebMarkupContainer governorsContainer = new WebMarkupContainer(ID_GOVERNORS_CONTAINER);
        governorsContainer.setOutputMarkupId(true);
        governorsContainer.setOutputMarkupPlaceholderTag(true);
        mainForm.add(governorsContainer);

        AjaxLink addOrgUnit = new AjaxLink(ID_BUTTON_ADD_GOVERNOR) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                addGovernorPerformed(target);
            }
        };
        governorsContainer.add(addOrgUnit);

        List<IColumn> governorsColumn = createGovernorColumns();
        final ObjectDataProvider governorsProvider = new ObjectDataProvider<UserType>(getPage(), UserType.class){

            @Override
            public List<UserType> applyDataFilter(List<UserType> list) {
                List<UserType> managersList = new ArrayList<>();

                if(model != null && model.getObject() != null){
                    List<String> managersUid = model.getObject().getGovernors();

                    for(UserType user: list){
                        if(managersUid.contains(user.getUid())){
                            managersList.add(user);
                        }
                    }
                }

                return managersList;
            }
        };

        TablePanel governorsTable = new TablePanel(ID_GOVERNORS_TABLE, governorsProvider, governorsColumn, 10);
        governorsTable.add(new VisibleEnableBehavior(){

            @Override
            public boolean isVisible() {
                return governorsProvider.size() > 0;
            }
        });
        governorsTable.setOutputMarkupId(true);
        governorsContainer.add(governorsTable);
    }

    private List<IColumn> createMemberColumns(){
        List<IColumn> columns = new ArrayList<>();

        columns.add(new PropertyColumn<UserType, String>(new Model<>("Name"), "name", "name"));
        columns.add(new PropertyColumn<UserType, String>(new Model<>("Given Name"), "givenName", "givenName"));
        columns.add(new PropertyColumn<UserType, String>(new Model<>("Family Name"), "familyName", "familyName"));
        columns.add(new PropertyColumn<UserType, String>(new Model<>("E-mail"), "emailAddress", "emailAddress"));
        columns.add(new PropertyColumn<UserType, String>(new Model<>("Locality"), "locality", "locality"));

        columns.add(new EditDeleteButtonColumn<UserType>(new Model<>("Actions")){

            @Override
            public void editPerformed(AjaxRequestTarget target, IModel<UserType> rowModel) {
                PageOrg.this.editMemberPerformed(target, rowModel);
            }

            @Override
            public boolean getRemoveVisible() {
                return false;
            }
        });

        return columns;
    }

    private List<IColumn> createGovernorColumns(){
        List<IColumn> columns = new ArrayList<>();

        columns.add(new PropertyColumn<UserType, String>(new Model<>("Name"), "name", "name"));
        columns.add(new PropertyColumn<UserType, String>(new Model<>("Given Name"), "givenName", "givenName"));
        columns.add(new PropertyColumn<UserType, String>(new Model<>("Family Name"), "familyName", "familyName"));
        columns.add(new PropertyColumn<UserType, String>(new Model<>("E-mail"), "emailAddress", "emailAddress"));
        columns.add(new PropertyColumn<UserType, String>(new Model<>("Locality"), "locality", "locality"));

        columns.add(new EditDeleteButtonColumn<UserType>(new Model<>("Actions")){

            @Override
            public void editPerformed(AjaxRequestTarget target, IModel<UserType> rowModel) {
                PageOrg.this.editGovernorPerformed(target, rowModel);
            }

            @Override
            public void removePerformed(AjaxRequestTarget target, IModel<UserType> rowModel) {
                PageOrg.this.removeGovernorPerformed(target, rowModel);
            }
        });

        return columns;
    }

    private Form getMainForm(){
        return (Form) get(ID_MAIN_FORM);
    }

    private WebMarkupContainer getFederationContainer(){
        return (WebMarkupContainer) get(ID_MAIN_FORM + ":" + ID_FEDERATION_CONTAINER);
    }

    private WebMarkupContainer getResourceInducementsContainer(){
        return (WebMarkupContainer) get(ID_MAIN_FORM + ":" + ID_RESOURCE_IND_CONTAINER);
    }

    private WebMarkupContainer getRoleInducementsContainer(){
        return (WebMarkupContainer) get(ID_MAIN_FORM + ":" + ID_ROLE_IND_CONTAINER);
    }

    private WebMarkupContainer getMembersContainer(){
        return (WebMarkupContainer) get(ID_MAIN_FORM + ":" + ID_MEMBERS_CONTAINER);
    }

    private WebMarkupContainer getGovernorsContainer(){
        return (WebMarkupContainer) get(ID_MAIN_FORM + ":" + ID_GOVERNORS_CONTAINER);
    }

    private IModel<String> createParentOrgUnitDisplayModel(final IModel<String> uidModel){
        return new LoadableModel<String>() {

            @Override
            protected String load() {
                if(uidModel == null || uidModel.getObject() == null || StringUtils.isEmpty(uidModel.getObject())){
                    return null;
                }

                String uid = uidModel.getObject();
                OrgType parent = null;

                try {
                    parent = getModelService().readObject(OrgType.class, uid);
                } catch (DatabaseCommunicationException e) {
                    LOGGER.error("Parent org. unit with uid: '" + uid + "' does not exist.");
                    error("Parent org. unit with uid: '" + uid + "' does not exist.");
                }

                if(parent == null){
                    return null;
                }

                return parent.getDisplayName();
            }
        };
    }

    private void resourceInducementChoosePerformed(AjaxRequestTarget target, IModel<ResourceType> resourceModel){
        if(resourceModel == null || resourceModel.getObject() == null){
            return;
        }

        if(model.getObject() == null){
            return;
        }

        String resourceUid = resourceModel.getObject().getUid();
        model.getObject().getResourceInducements().add(resourceUid);

        ModalWindow window = (ModalWindow) get(ID_RESOURCE_INDUCEMENT_CHOOSER);
        window.close(target);
        target.add(getResourceInducementsContainer());
    }

    private void roleInducementChoosePerformed(AjaxRequestTarget target, IModel<RoleType> roleModel){
        if(roleModel == null || roleModel.getObject() == null){
            return;
        }

        if(model.getObject() == null){
            return;
        }

        String roleUid = roleModel.getObject().getUid();
        model.getObject().getRoleInducements().add(roleUid);

        ModalWindow window = (ModalWindow) get(ID_ROLE_INDUCEMENT_CHOOSER);
        window.close(target);
        target.add(getRoleInducementsContainer());
    }

    private void governorChoosePerformed(AjaxRequestTarget target, IModel<UserType> governorModel){
        if(governorModel == null || governorModel.getObject() == null){
            return;
        }

        if(model.getObject() == null){
            return;
        }

        String governorUid = governorModel.getObject().getUid();
        model.getObject().getGovernors().add(governorUid);

        ModalWindow window = (ModalWindow) get(ID_GOVERNOR_CHOOSER);
        window.close(target);
        target.add(getGovernorsContainer());
    }

    /**
     *  TODO - filter org. unit that can be chosen as parents, some cases:
     *      * any org. unit from sub-tree (to prevent cycles in org. unit hierarchy)
     * */
    private List<OrgType> applyParentOrgChooserFilter(List<OrgType> list){
        List<OrgType> newOrgList = new ArrayList<>();

        if(model.getObject() == null || !isEditingOrgUnit()){
            return list;
        }

        List<String> parentOrgUnits = model.getObject().getParentOrgUnits();
        String currentOrgUid = model.getObject().getUid();

        for(OrgType org: list){
            if(!parentOrgUnits.contains(org.getUid()) && !currentOrgUid.equals(org.getUid())){
                newOrgList.add(org);
            }
        }

        return newOrgList;
    }

    private List<ResourceType> applyResourceInducementChooserFilter(List<ResourceType> list){
        List<ResourceType> newResourceList = new ArrayList<>();

        if(model.getObject() == null){
            return list;
        }

        List<String> currentResourceInducements = model.getObject().getResourceInducements();

        for(ResourceType resource: list){
            if(!currentResourceInducements.contains(resource.getUid())){
                newResourceList.add(resource);
            }
        }

        return newResourceList;
    }

    private List<RoleType> applyRoleInducementChooserFilter(List<RoleType> list){
        List<RoleType> newRoleList = new ArrayList<>();

        if(model.getObject() == null){
            return list;
        }

        List<String> currentRoleInducements = model.getObject().getRoleInducements();

        for(RoleType role: list){
            if(!currentRoleInducements.contains(role.getUid())){
                newRoleList.add(role);
            }
        }

        return newRoleList;
    }

    private List<UserType> applyGovernorChooserFilter(List<UserType> list){
        List<UserType> newUserList = new ArrayList<>();

        if(model.getObject() == null){
            return list;
        }

        List<String> currentGovernors = model.getObject().getGovernors();

        for(UserType user: list){
            if(!currentGovernors.contains(user.getUid())){
                newUserList.add(user);
            }
        }

        return newUserList;
    }

    private void editParentOrgUnitPerformed(AjaxRequestTarget target){
        ModalWindow modal = (ModalWindow) get(ID_PARENT_ORG_UNIT_CHOOSER);
        modal.show(target);
    }

    private void parentOrgUnitChoosePerformed(AjaxRequestTarget target, IModel<OrgType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            error("Chosen value is not a valid org. unit.");
            target.add(getFeedbackPanel());
            return;
        }

        if(model == null || model.getObject() == null){
            error("Couldn't add parent org. to this org. unit. Invalid org. unit model, please refresh this page.");
            target.add(getFeedbackPanel());
            return;
        }

        String uid = rowModel.getObject().getUid();
        model.getObject().getParentOrgUnits().add(uid);

        ModalWindow dialog = (ModalWindow) get(ID_PARENT_ORG_UNIT_CHOOSER);
        dialog.close(target);
        target.add(getMainForm());
    }

    private void editResourceInducementPerformed(AjaxRequestTarget target, IModel<ResourceType> resourceModel){
        if(resourceModel == null || resourceModel.getObject() == null){
            error("Couldn't edit selected resource inducement. It is no longer available.");
            target.add(getFeedbackPanel());
            return;
        }

        PageParameters parameters = new PageParameters();
        parameters.add(UID_PAGE_PARAMETER_NAME, resourceModel.getObject().getUid());
        setResponsePage(new PageResource(parameters));
    }

    private void editRoleInducementPerformed(AjaxRequestTarget target, IModel<RoleType> roleModel){
        if(roleModel == null || roleModel.getObject() == null){
            error("Couldn't edit selected role inducement. It is no longer available.");
            target.add(getFeedbackPanel());
            return;
        }

        PageParameters parameters = new PageParameters();
        parameters.add(UID_PAGE_PARAMETER_NAME, roleModel.getObject().getUid());
        setResponsePage(new PageRole(parameters));
    }

    private void editMemberPerformed(AjaxRequestTarget target, IModel<UserType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            error("Couldn't edit selected user. It is no longer available.");
            target.add(getFeedbackPanel());
            return;
        }

        PageParameters parameters = new PageParameters();
        parameters.add(UID_PAGE_PARAMETER_NAME, rowModel.getObject().getUid());
        setResponsePage(new PageUser(parameters));
    }

    /*
    *   Since governor is also a UserType, we can use editMemberPerformed() method
    * */
    private void editGovernorPerformed(AjaxRequestTarget target, IModel<UserType> rowModel){
        editMemberPerformed(target, rowModel);
    }

    private void addResourceInducementPerformed(AjaxRequestTarget target){
        ModalWindow modal = (ModalWindow) get(ID_RESOURCE_INDUCEMENT_CHOOSER);
        modal.show(target);
    }

    private void addRoleInducementPerformed(AjaxRequestTarget target){
        ModalWindow modal = (ModalWindow) get(ID_ROLE_INDUCEMENT_CHOOSER);
        modal.show(target);
    }

    private void addGovernorPerformed(AjaxRequestTarget target){
        ModalWindow modal = (ModalWindow) get(ID_GOVERNOR_CHOOSER);
        modal.show(target);
    }

    private void removeGovernorPerformed(AjaxRequestTarget target, IModel<UserType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            error("Couldn't remove selected governor assignment. Something went wrong.");
            target.add(getFeedbackPanel());
            return;
        }

        String governorUid = rowModel.getObject().getUid();
        model.getObject().getGovernors().remove(governorUid);
        success("Governor with uid: '" + governorUid + "' was removed successfully.");
        target.add(getGovernorsContainer(), getFeedbackPanel());
    }

    private void removeResourceInducementPerformed(AjaxRequestTarget target, IModel<ResourceType> resourceModel){
        if(resourceModel == null || resourceModel.getObject() == null){
            error("Couldn't remove selected resource inducement. Something went wrong.");
            target.add(getFeedbackPanel());
            return;
        }

        String resourceInducementUid = resourceModel.getObject().getUid();
        model.getObject().getResourceInducements().remove(resourceInducementUid);
        success("Resource inducement with to resource with uid: '" + resourceInducementUid + "' was removed successfully.");
        target.add(getResourceInducementsContainer(), getFeedbackPanel());
    }

    private void removeRoleInducementPerformed(AjaxRequestTarget target, IModel<RoleType> roleModel){
        if(roleModel == null || roleModel.getObject() == null){
            error("Couldn't remove selected role inducement. Something went wrong.");
            target.add(getFeedbackPanel());
            return;
        }

        String roleInducementUid = roleModel.getObject().getUid();
        model.getObject().getRoleInducements().remove(roleInducementUid);
        success("Role inducement with to resource with uid: '" + roleInducementUid + "' was removed successfully.");
        target.add(getRoleInducementsContainer(), getFeedbackPanel());
    }

    private void cancelPerformed(){
        setResponsePage(PageOrgList.class);
    }

    private void recomputePerformed(AjaxRequestTarget target){
        if(model == null || model.getObject() == null){
            error("Couldn't recompute org. unit.");
            target.add(getFeedbackPanel());
            return;
        }

        OrgType orgUnit = model.getObject();

        if(!isEditingOrgUnit()){
            warn("Can't recompute org. unit not yet saved in repository.");
            target.add(getFeedbackPanel());
            return;
        }

        try {
            getModelService().recomputeOrganizationalUnit(orgUnit);
            success("Org. unit: '" + orgUnit.getName() + "'(" + orgUnit.getUid() + ") RECOMPUTE was successful.");
        } catch (DatabaseCommunicationException | ObjectNotFoundException e) {
            LOGGER.error("Can't recompute org. unit: ", e);
            error("Can't recompute org. unit with name: '" + orgUnit.getName() + "'. Reason: " + e.getExceptionMessage());
        }

        target.add(this, getFeedbackPanel());
    }

    private void shareOrgSubtree(OrgType org){
        String uid = org.getUid();

        try {
            List<OrgType> allOrgUnits = getModelService().getAllObjectsOfType(OrgType.class);
            List<OrgType> children =  new ArrayList<>();

            //First, retrieve all children of target org. unit
            for(OrgType orgUnit: allOrgUnits){
                for(String orgUid: orgUnit.getParentOrgUnits()){
                    if(uid.equals(orgUid)){
                        children.add(orgUnit);
                        break;
                    }
                }
            }

            //If the children does not override parent sharing, set sharing to true and save
            for(OrgType orgUnit: children){
                if(!orgUnit.isOverrideParentSharing()){
                    orgUnit.setSharedInFederation(true);
                    getModelService().updateObject(orgUnit);
                }
            }

            //Repeat the process recursively for entire tree
            for(OrgType orgUnit: children){
                shareOrgSubtree(orgUnit);
            }

        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Can't retrieve the children of org. unit: " + org.getName() + ". Reason: ", e);
            error("Can't retrieve the children of org. unit: " + org.getName() + ". Reason: " + e);
        } catch (ObjectNotFoundException e) {
            LOGGER.error("Can't save children org. unit. Reason: ", e);
            error("Can't save children org. unit. Reason: " + e);
        }
    }

    private void savePerformed(AjaxRequestTarget target){
        ModelService modelService = getModelService();
        OrgType orgUnit;

        if(model == null || model.getObject() == null){
            error("Couldn't save org. unit.");
            target.add(getFeedbackPanel());
            return;
        }

        orgUnit = model.getObject();

        //Filtering empty org. unit types
        List<String> newOrgTypes = new ArrayList<>();
        for(String type: orgUnit.getOrgType()){
            if(type != null && StringUtils.isNotEmpty(type)){
                newOrgTypes.add(type);
            }
        }
        orgUnit.getOrgType().clear();
        orgUnit.getOrgType().addAll(newOrgTypes);

        //Filtering empty org. unit parents
        List<String> newOrgParents = new ArrayList<>();
        for(String parent: orgUnit.getParentOrgUnits()){
            if(parent != null && StringUtils.isNotEmpty(parent)){
                newOrgParents.add(parent);
            }
        }
        orgUnit.getParentOrgUnits().clear();
        orgUnit.getParentOrgUnits().addAll(newOrgParents);

        try{
            if(!isEditingOrgUnit()){
                modelService.createObject(orgUnit);
            } else {
                modelService.updateObject(orgUnit);
                if(orgUnit.isSharedSubtree()){
                    shareOrgSubtree(orgUnit);
                }
            }

        } catch (GeneralException e){
            LOGGER.error("Can't add org. unit: ", e);
            error("Can't add org. unit with name: '" + orgUnit.getName() + "'. Reason: " + e.getExceptionMessage());
        }

        getSession().success("Org. Unit '" + orgUnit.getName() + "' has been saved successfully.");
        LOGGER.info("Org. Unit '" + orgUnit.getName() + "' has been saved successfully.");
        setResponsePage(PageOrgList.class);
        target.add(getFeedbackPanel());
    }
}