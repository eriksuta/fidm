package com.esuta.fidm.gui.page.org;

import com.esuta.fidm.gui.component.WebMiscUtil;
import com.esuta.fidm.gui.component.behavior.VisibleEnableBehavior;
import com.esuta.fidm.gui.component.data.ObjectDataProvider;
import com.esuta.fidm.gui.component.data.column.EditDeleteButtonColumn;
import com.esuta.fidm.gui.component.data.table.TablePanel;
import com.esuta.fidm.gui.component.form.MultiValueTextEditPanel;
import com.esuta.fidm.gui.component.form.MultiValueTextPanel;
import com.esuta.fidm.gui.component.modal.ObjectChooserDialog;
import com.esuta.fidm.gui.component.modal.ObjectInformationDialog;
import com.esuta.fidm.gui.component.modal.SharingPolicyViewerDialog;
import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.gui.page.resource.PageResource;
import com.esuta.fidm.gui.page.roles.PageRole;
import com.esuta.fidm.gui.page.users.PageUser;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.GeneralException;
import com.esuta.fidm.infra.exception.ObjectNotFoundException;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.model.federation.client.ObjectTypeRestResponse;
import com.esuta.fidm.repository.schema.core.*;
import com.esuta.fidm.repository.schema.support.FederationIdentifierType;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.eclipse.jetty.http.HttpStatus;

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

    private static final String ID_SHARING_POLICY_LABEL = "sharingPolicyLabel";
    private static final String ID_SHARING_POLICY_EDIT = "sharingPolicyEdit";
    private static final String ID_PROVISIONING_POLICY_LABEL = "provisioningPolicyLabel";
    private static final String ID_PROVISIONING_POLICY_EDIT = "provisioningPolicyEdit";

    private static final String ID_PARENT_ORG_UNIT_CHOOSER = "parentOrgUnitChooser";
    private static final String ID_GOVERNOR_CHOOSER = "governorChooser";
    private static final String ID_RESOURCE_INDUCEMENT_CHOOSER = "resourceInducementChooser";
    private static final String ID_ROLE_INDUCEMENT_CHOOSER = "roleInducementChooser";
    private static final String ID_SHARING_POLICY_CHOOSER = "sharingPolicyChooser";
    private static final String ID_SHARING_POLICY_VIEWER = "sharingPolicyViewer";
    private static final String ID_OBJECT_INFORMATION_VIEWER = "objectInformationViewer";
    private static final String ID_PROVISIONING_POLICY_VIEWER = "provisioningPolicyViewer";

    private IModel<OrgType> model;
    private IModel<FederationSharingPolicyType> sharingPolicyModel;

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

        sharingPolicyModel = new LoadableModel<FederationSharingPolicyType>(false) {

            @Override
            protected FederationSharingPolicyType load() {
                return loadSharingPolicy();
            }
        };

        initLayout();
    }

    @Override
    protected IModel<String> createPageSubtitleModel() {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                if(isLocalOrgUnit()){
                    return "View/Edit local org. unit.";
                } else {
                    return "View/Edit copy of org. unit. Origin IP: " + getOrgUnitOriginFederationMember();
                }
            }
        };
    }

    private OrgType loadOrgUnit(){
        if(!isEditingOrgUnit()){
            return new OrgType();
        }

        PageParameters parameters = getPageParameters();
        String uid = parameters.get(UID_PAGE_PARAMETER_NAME).toString();
        OrgType org;

        try {
            org = getModelService().readObject(OrgType.class, uid);
        } catch (DatabaseCommunicationException exc){
            error("Couldn't retrieve org. unit with oid: '" + uid + "' from the repository. Reason: " + exc.getExceptionMessage());
            LOGGER.error("Couldn't retrieve org. unit with oid: '" + uid + "' from the repository. Reason: ", exc);
            throw new RestartResponseException(PageOrgList.class);
        }

        return org;
    }

    private FederationSharingPolicyType loadSharingPolicy(){
        if(isLocalOrgUnit()){
            return null;
        }

        FederationIdentifierType identifier = model.getObject().getFederationIdentifier();
        String federationMemberName = identifier.getFederationMemberId();

        try {
            ObjectTypeRestResponse<FederationSharingPolicyType> response = getFederationServiceClient()
                    .createGetOrgSharingPolicyRequest(getFederationMemberByName(federationMemberName), identifier);

            int responseStatus = response.getStatus();
            if(responseStatus == HttpStatus.OK_200){
                return response.getValue();
            } else {
                LOGGER.error("Could not retrieve sharing policy for this org. unit. Reason: " + response.getMessage());
                error("Could not retrieve sharing policy for this org. unit. Reason: " + response.getMessage());
                return null;
            }

        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Could not retrieve sharing policy for this org. unit.", e);
            error("Could not retrieve sharing policy for this org. unit. Reason: " + e);
        }

        return null;
    }

    private boolean isEditingOrgUnit(){
        PageParameters parameters = getPageParameters();
        return !parameters.get(UID_PAGE_PARAMETER_NAME).isEmpty();
    }

    private boolean isLocalOrgUnit(){
        if(model != null && model.getObject() != null){
            OrgType org = model.getObject();
            return org.getFederationIdentifier() == null;
        }

        return true;
    }

    private String getOrgUnitOriginFederationMember(){
        if(model != null && model.getObject() != null && model.getObject().getFederationIdentifier() != null){
            OrgType org = model.getObject();
            return org.getFederationIdentifier().getFederationMemberId();
        }

        return null;
    }

    private void initLayout(){
        Form mainForm = new Form(ID_MAIN_FORM);
        mainForm.setOutputMarkupId(true);
        add(mainForm);

        TextField name = new TextField<>(ID_NAME, new PropertyModel<String>(model, "name"));
        name.setRequired(true);
        prepareSharingPolicyBasedSingleValueBehavior(name, "name");
        mainForm.add(name);

        TextField displayName = new TextField<>(ID_DISPLAY_NAME, new PropertyModel<String>(model, "displayName"));
        displayName.setRequired(true);
        prepareSharingPolicyBasedSingleValueBehavior(displayName, "displayName");
        mainForm.add(displayName);

        TextArea description = new TextArea<>(ID_DESCRIPTION, new PropertyModel<String>(model, "description"));
        mainForm.add(description);

        TextField locality = new TextField<>(ID_LOCALITY, new PropertyModel<String>(model, "locality"));
        prepareSharingPolicyBasedSingleValueBehavior(locality, "locality");
        mainForm.add(locality);

        MultiValueTextPanel orgType = new MultiValueTextPanel<String>(ID_ORG_TYPE,
                new PropertyModel<List<String>>(model, "orgType"), false){

            @Override
            protected boolean isAddEnabled() {
                return canManipulateWithMultiValueAttribute("orgType");
            }

            @Override
            protected boolean isRemoveEnabled() {
                return canManipulateWithMultiValueAttribute("orgType");
            }

            @Override
            protected boolean isInputEnabled() {
                return !canManipulateWithMultiValueAttribute("orgType");
            }
        };
        mainForm.add(orgType);

        MultiValueTextEditPanel parentOrgUnit = new MultiValueTextEditPanel<ObjectReferenceType<OrgType>>(ID_PARENT_ORG_UNIT,
                new PropertyModel<List<ObjectReferenceType<OrgType>>>(model, "parentOrgUnits"), false){

            @Override
            protected IModel<String> createTextModel(IModel<ObjectReferenceType<OrgType>> model) {
                return createParentOrgUnitDisplayModel(model);
            }

            @Override
            protected ObjectReferenceType<OrgType> createNewEmptyItem() {
                return new ObjectReferenceType<>();
            }

            @Override
            protected void editPerformed(AjaxRequestTarget target, ObjectReferenceType<OrgType> object) {
                PageOrg.this.editParentOrgUnitPerformed(target);
            }

            @Override
            protected boolean isAddEnabled() {
                return canManipulateWithMultiValueAttribute("parentOrgUnits");
            }

            @Override
            protected boolean isRemoveEnabled() {
                return canManipulateWithMultiValueAttribute("parentOrgUnits");
            }
        };
        mainForm.add(parentOrgUnit);

        //Sharing policy components
        TextField sharingPolicyLabel = new TextField<>(ID_SHARING_POLICY_LABEL, createSharingPolicyLabel());
        sharingPolicyLabel.setOutputMarkupId(true);
        sharingPolicyLabel.add(AttributeAppender.replace("placeholder", "Set policy"));
        sharingPolicyLabel.setEnabled(false);
        mainForm.add(sharingPolicyLabel);

        AjaxLink sharingPolicyEdit = new AjaxLink(ID_SHARING_POLICY_EDIT) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                sharingPolicyEditPerformed(target);
            }
        };
        mainForm.add(sharingPolicyEdit);

        //Provisioning policy components
        TextField provisioningPolicyLabel = new TextField<>(ID_PROVISIONING_POLICY_LABEL, createProvisioningPolicyLabel());
        provisioningPolicyLabel.setOutputMarkupId(true);
        provisioningPolicyLabel.add(AttributeAppender.replace("placeholder", "Set policy"));
        provisioningPolicyLabel.setEnabled(false);
        mainForm.add(provisioningPolicyLabel);

        AjaxLink provisioningPolicyEdit = new AjaxLink(ID_PROVISIONING_POLICY_EDIT) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                provisioningPolicyEditPerformed(target);
            }
        };
        mainForm.add(provisioningPolicyEdit);

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

    private IModel<String> createSharingPolicyLabel(){
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                if(model == null || model.getObject() == null || model.getObject().getSharingPolicy() == null){
                    return "Set Policy";
                }

                if(!isLocalOrgUnit()){
                    return "View origin sharing policy";
                }

                ObjectReferenceType sharingPolicyRef = model.getObject().getSharingPolicy();
                String sharingPolicyUid = sharingPolicyRef.getUid();

                try {
                    FederationSharingPolicyType policy = getModelService().readObject(FederationSharingPolicyType.class, sharingPolicyUid);
                    return policy.getName();
                } catch (DatabaseCommunicationException e) {
                    error("Could not load sharing policy with uid: '" + sharingPolicyUid + "' from the repository.");
                    LOGGER.error("Could not load sharing policy with uid: '" + sharingPolicyUid + "' from the repository.");
                }

                return "Set Policy";
            }
        };
    }

    private IModel<String> createProvisioningPolicyLabel(){
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                if(model == null || model.getObject() == null || model.getObject().getProvisioningPolicy() == null){
                    return "Set Policy";
                }

                ObjectReferenceType provisioningPolicyRef = model.getObject().getProvisioningPolicy();
                String provisioningPolicyUid = provisioningPolicyRef.getUid();

                try {
                    FederationProvisioningPolicyType policy = getModelService().readObject(FederationProvisioningPolicyType.class, provisioningPolicyUid);
                    return policy.getName();
                } catch (DatabaseCommunicationException e) {
                    error("Could not load provisioning policy with uid: '" + provisioningPolicyUid + "' from the repository.");
                    LOGGER.error("Could not load provisioning policy with uid: '" + provisioningPolicyUid + "' from the repository.");
                }

                return "Set Policy";
            }
        };
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
                resourceInducementChoosePerformed(target, rowModel, isSharedInFederation());
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
                roleInducementChoosePerformed(target, rowModel, isSharedInFederation());
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
                parentOrgUnitChoosePerformed(target, rowModel, isSharedInFederation());
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
                governorChoosePerformed(target, rowModel, isSharedInFederation());
            }

            @Override
            public String getChooserTitle() {
                return "Choose governor.";
            }

            @Override
            public List<UserType> applyObjectFilter(List<UserType> list) {
                return applyGovernorChooserFilter(list);
            }
        };
        add(governorChooser);

        ModalWindow sharingPolicyChooser = new ObjectChooserDialog<FederationSharingPolicyType>(ID_SHARING_POLICY_CHOOSER, FederationSharingPolicyType.class){

            @Override
            public void objectChoosePerformed(AjaxRequestTarget target, IModel<FederationSharingPolicyType> rowModel) {
                sharingPolicyChoosePerformed(target, rowModel);
            }

            @Override
            public String getChooserTitle() {
                return "Choose Sharing Policy";
            }

            @Override
            public boolean isSharedInFederationEnabled() {
                return false;
            }
        };
        ((ObjectChooserDialog)sharingPolicyChooser).setSharedInFederation(true);
        add(sharingPolicyChooser);

        ModalWindow provisioningPolicyChooser = new ObjectChooserDialog<FederationProvisioningPolicyType>(
                ID_PROVISIONING_POLICY_VIEWER, FederationProvisioningPolicyType.class){

            @Override
            public void objectChoosePerformed(AjaxRequestTarget target, IModel<FederationProvisioningPolicyType> rowModel) {
                provisioningPolicyChoosePerformed(target, rowModel);
            }

            @Override
            public String getChooserTitle() {
                return "Choose Provisioning Policy";
            }

            @Override
            public boolean isSharedInFederationEnabled() {
                return false;
            }
        };
        add(provisioningPolicyChooser);

        ModalWindow sharingPolicyViewer = new SharingPolicyViewerDialog(ID_SHARING_POLICY_VIEWER, null, null);
        add(sharingPolicyViewer);

        ModalWindow objectInformationViewer = new ObjectInformationDialog(ID_OBJECT_INFORMATION_VIEWER, null);
        add(objectInformationViewer);
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
        addResourceInducement.add(new VisibleEnableBehavior(){

            @Override
            public boolean isEnabled() {
                return canManipulateWithMultiValueAttribute("resourceInducements");
            }
        });
        resourceInducementsContainer.add(addResourceInducement);

        List<IColumn> resourceInducementColumns = createResourceInducementColumns();
        final ObjectDataProvider resourceInducementsProvider = new ObjectDataProvider<ResourceType>(getPage(), ResourceType.class){

            @Override
            public List<ResourceType> applyDataFilter(List<ResourceType> list) {
                List<ResourceType> resourceInducementList = new ArrayList<>();

                if(model != null && model.getObject() != null){
                    OrgType org = model.getObject();

                    for(ResourceType resource: list){
                        for(InducementType<ResourceType> resourceInducement: org.getResourceInducements()){
                            if(resourceInducement.getUid().equals(resource.getUid())){
                                resourceInducementList.add(resource);
                                break;
                            }
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
        roleInducementsContainer.add(new VisibleEnableBehavior(){

            @Override
            public boolean isEnabled() {
                return canManipulateWithMultiValueAttribute("roleInducements");
            }
        });
        roleInducementsContainer.add(addRoleInducement);

        List<IColumn> roleInducementColumns = createRoleInducementColumns();
        final ObjectDataProvider roleInducementsProvider = new ObjectDataProvider<RoleType>(getPage(), RoleType.class){

            @Override
            public List<RoleType> applyDataFilter(List<RoleType> list) {
                List<RoleType> roleInducementList = new ArrayList<>();

                if(model != null && model.getObject() != null){
                    OrgType org = model.getObject();

                    for(RoleType role: list){
                        for(InducementType<RoleType> roleInducement: org.getRoleInducements()){
                            if(roleInducement.getUid().equals(role.getUid())){
                                roleInducementList.add(role);
                                break;
                            }
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

            @Override
            public boolean isRemoveEnabled() {
                return canManipulateWithMultiValueAttribute("resourceInducements");
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

            @Override
            public boolean isRemoveEnabled() {
                return canManipulateWithMultiValueAttribute("roleInducements");
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
                        for(AssignmentType<OrgType> orgAssignment: user.getOrgUnitAssignments()){
                            if(orgAssignment.getUid().equals(orgUid)){
                                memberList.add(user);
                            }
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
        addOrgUnit.add(new VisibleEnableBehavior(){

            @Override
            public boolean isEnabled() {
                return canManipulateWithMultiValueAttribute("governors");
            }
        });
        governorsContainer.add(addOrgUnit);

        List<IColumn> governorsColumn = createGovernorColumns();
        final ObjectDataProvider governorsProvider = new ObjectDataProvider<UserType>(getPage(), UserType.class){

            @Override
            public List<UserType> applyDataFilter(List<UserType> list) {
                List<UserType> managersList = new ArrayList<>();

                if(model != null && model.getObject() != null){
                    List<ObjectReferenceType<UserType>> managers = model.getObject().getGovernors();

                    for(UserType user: list){
                        for(ObjectReferenceType<UserType> managerRef: managers){
                            if(managerRef.getUid().equals(user.getUid())){
                                managersList.add(user);
                            }
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

            @Override
            public boolean isRemoveEnabled() {
                return canManipulateWithMultiValueAttribute("governors");
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

    private IModel<String> createParentOrgUnitDisplayModel(final IModel<ObjectReferenceType<OrgType>> parentRef){
        return new LoadableModel<String>() {

            @Override
            protected String load() {
                if(parentRef == null || parentRef.getObject() == null || parentRef.getObject().getUid() == null){
                    return null;
                }

                String orgUid = parentRef.getObject().getUid();
                OrgType parent = null;

                try {
                    parent = getModelService().readObject(OrgType.class, orgUid);
                } catch (DatabaseCommunicationException e) {
                    LOGGER.error("Parent org. unit with uid: '" + orgUid + "' does not exist.");
                    error("Parent org. unit with uid: '" + orgUid + "' does not exist.");
                }

                if(parent == null){
                    return null;
                }

                return parent.getDisplayName();
            }
        };
    }

    private void sharingPolicyEditPerformed(AjaxRequestTarget target){
        //If we are editing local org. unit, we can edit sharing policy,
        //else, the user is only able to view the policy rules
        if(isLocalOrgUnit()){
            ModalWindow window = (ModalWindow) get(ID_SHARING_POLICY_CHOOSER);
            window.show(target);
        } else {
            SharingPolicyViewerDialog window = (SharingPolicyViewerDialog) get(ID_SHARING_POLICY_VIEWER);
            window.updateModel(sharingPolicyModel.getObject().getRules(), sharingPolicyModel.getObject());
            window.show(target);
        }
    }

    private void provisioningPolicyEditPerformed(AjaxRequestTarget target){
        ModalWindow window = (ModalWindow) get(ID_PROVISIONING_POLICY_VIEWER);
        window.show(target);
    }

    private void resourceInducementChoosePerformed(AjaxRequestTarget target, IModel<ResourceType> resourceModel, boolean isSharedInFederation){
        if(resourceModel == null || resourceModel.getObject() == null){
            return;
        }

        if(model.getObject() == null){
            return;
        }

        String resourceUid = resourceModel.getObject().getUid();
        InducementType<ResourceType> resourceInducement = new InducementType<>(resourceUid, ResourceType.class);
        resourceInducement.setSharedInFederation(isSharedInFederation);
        model.getObject().getResourceInducements().add(resourceInducement);

        ModalWindow window = (ModalWindow) get(ID_RESOURCE_INDUCEMENT_CHOOSER);
        window.close(target);
        target.add(getResourceInducementsContainer());
    }

    private void roleInducementChoosePerformed(AjaxRequestTarget target, IModel<RoleType> roleModel, boolean isSharedInFederation){
        if(roleModel == null || roleModel.getObject() == null){
            return;
        }

        if(model.getObject() == null){
            return;
        }

        String roleUid = roleModel.getObject().getUid();
        InducementType<RoleType> roleInducement = new InducementType<>(roleUid, RoleType.class);
        roleInducement.setSharedInFederation(isSharedInFederation);
        model.getObject().getRoleInducements().add(roleInducement);

        ModalWindow window = (ModalWindow) get(ID_ROLE_INDUCEMENT_CHOOSER);
        window.close(target);
        target.add(getRoleInducementsContainer());
    }

    private void governorChoosePerformed(AjaxRequestTarget target, IModel<UserType> governorModel, boolean sharedInFederation){
        if(governorModel == null || governorModel.getObject() == null){
            return;
        }

        if(model.getObject() == null){
            return;
        }

        String governorUid = governorModel.getObject().getUid();
        ObjectReferenceType<UserType> governorReference = new ObjectReferenceType<>(governorUid, UserType.class);
        governorReference.setSharedInFederation(sharedInFederation);
        model.getObject().getGovernors().add(governorReference);

        ModalWindow window = (ModalWindow) get(ID_GOVERNOR_CHOOSER);
        window.close(target);
        target.add(getGovernorsContainer());
    }

    private void sharingPolicyChoosePerformed(AjaxRequestTarget target, IModel<FederationSharingPolicyType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            return;
        }

        if(model.getObject() == null){
            return;
        }

        OrgType org = model.getObject();
        FederationSharingPolicyType policy = rowModel.getObject();
        ObjectReferenceType<FederationSharingPolicyType> policyRef = new ObjectReferenceType<>();
        policyRef.setUid(policy.getUid());
        policyRef.setSharedInFederation(true);
        policyRef.setType(FederationSharingPolicyType.class);
        org.setSharingPolicy(policyRef);

        ModalWindow window = (ModalWindow) get(ID_SHARING_POLICY_CHOOSER);
        window.close(target);

        target.add(get(ID_MAIN_FORM + ":" + ID_SHARING_POLICY_LABEL));
    }

    private void provisioningPolicyChoosePerformed(AjaxRequestTarget target, IModel<FederationProvisioningPolicyType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            return;
        }

        if(model.getObject() == null){
            return;
        }

        OrgType org = model.getObject();
        FederationProvisioningPolicyType policy = rowModel.getObject();
        ObjectReferenceType<FederationProvisioningPolicyType> policyRef = new ObjectReferenceType<>();
        policyRef.setUid(policy.getUid());
        policyRef.setSharedInFederation(false);
        policyRef.setType(FederationProvisioningPolicyType.class);
        org.setProvisioningPolicy(policyRef);

        ModalWindow window = (ModalWindow) get(ID_PROVISIONING_POLICY_VIEWER);
        window.close(target);

        target.add(get(ID_MAIN_FORM + ":" + ID_PROVISIONING_POLICY_LABEL));
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

        List<ObjectReferenceType<OrgType>> parentOrgUnits = model.getObject().getParentOrgUnits();
        String currentOrgUid = model.getObject().getUid();

        for(OrgType org: list){
            ObjectReferenceType<OrgType> parentReference = new ObjectReferenceType<>(org.getUid(), OrgType.class);

            if(!parentOrgUnits.contains(parentReference) && !currentOrgUid.equals(org.getUid())){
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

        List<InducementType<ResourceType>> currentResourceInducements = model.getObject().getResourceInducements();

        for(ResourceType resource: list){
            InducementType<ResourceType> resourceInducement = new InducementType<>(resource.getUid(), ResourceType.class);

            if(!currentResourceInducements.contains(resourceInducement)){
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

        List<InducementType<RoleType>> currentRoleInducements = model.getObject().getRoleInducements();

        for(RoleType role: list){
            InducementType<RoleType> roleInducement = new InducementType<>(role.getUid(), RoleType.class);

            if(!currentRoleInducements.contains(roleInducement)){
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

        List<ObjectReferenceType<UserType>> currentGovernors = model.getObject().getGovernors();

        for(UserType user: list){
            ObjectReferenceType<UserType> governorReference = new ObjectReferenceType<>(user.getUid(), UserType.class);

            if(!currentGovernors.contains(governorReference)){
                newUserList.add(user);
            }
        }

        return newUserList;
    }

    /*
    *   The rule is only applied when editing/viewing shared org. unit. When there is no specific
    *   rule defined for single-value attribute, default single value tolerance level is applied.
    * */
    private void prepareSharingPolicyBasedSingleValueBehavior(Component component, final String attributeName){
        if(isLocalOrgUnit()){
            return;
        }

        if(!WebMiscUtil.isOrgAttributeSingleValue(attributeName)){
            return;
        }

        component.add(new VisibleEnableBehavior(){

            @Override
            public boolean isEnabled() {
                FederationSharingRuleType rule = WebMiscUtil.getRuleByAttributeName(sharingPolicyModel.getObject(), attributeName);

                if(rule == null){
                    SingleValueTolerance defaultSingleValueTolerance = sharingPolicyModel.getObject().getDefaultSingleValueTolerance();
                    return !SingleValueTolerance.ENFORCE.equals(defaultSingleValueTolerance);
                }

                return !SingleValueTolerance.ENFORCE.equals(rule.getSingleValueTolerance());
            }
        });
    }

    /*
    *   The rule is only applied when editing/viewing shared org. unit. When there is no specific
    *   rule defined for multi-value attribute, default multi value tolerance level is applied.
    * */
    private boolean canManipulateWithMultiValueAttribute(String attributeName){
        if(isLocalOrgUnit()){
            return true;
        }

        FederationSharingRuleType rule = WebMiscUtil.getRuleByAttributeName(sharingPolicyModel.getObject(), attributeName);

        if(rule == null){
            MultiValueTolerance defaultMultiValueTolerance = sharingPolicyModel.getObject().getDefaultMultiValueTolerance();
            return !MultiValueTolerance.ENFORCE.equals(defaultMultiValueTolerance);
        }

        return !MultiValueTolerance.ENFORCE.equals(rule.getMultiValueTolerance());
    }

    private void editParentOrgUnitPerformed(AjaxRequestTarget target){
        if(isLocalOrgUnit()){
            ModalWindow modal = (ModalWindow) get(ID_PARENT_ORG_UNIT_CHOOSER);
            modal.show(target);
        } else {
            info("Can't edit parent org. unit of remote org. unit.");
            target.add(getFeedbackPanel());
        }
    }

    private void parentOrgUnitChoosePerformed(AjaxRequestTarget target, IModel<OrgType> rowModel, boolean isSharedInFederation){
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
        ObjectReferenceType<OrgType> parentReference = new ObjectReferenceType<>(uid, OrgType.class);
        parentReference.setSharedInFederation(isSharedInFederation);
        model.getObject().getParentOrgUnits().add(parentReference);

        ModalWindow dialog = (ModalWindow) get(ID_PARENT_ORG_UNIT_CHOOSER);
        dialog.close(target);
        target.add(get(ID_MAIN_FORM + ":" + ID_PARENT_ORG_UNIT));
    }

    private void editResourceInducementPerformed(AjaxRequestTarget target, IModel<ResourceType> resourceModel){
        if(!isLocalOrgUnit()){
            info("Can't edit resource inducement of remote org. unit.");
            target.add(getFeedbackPanel());
            return;
        }

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
        if(!isLocalOrgUnit()){
            info("Can't edit role inducement of remote org. unit.");
            target.add(getFeedbackPanel());
            return;
        }

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
        if(!isLocalOrgUnit()){
            info("Can't edit member/governor of remote org. unit.");
            target.add(getFeedbackPanel());
            return;
        }

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

        ObjectReferenceType<UserType> governorToRemove = new ObjectReferenceType<>();
        for(ObjectReferenceType<UserType> governorRef: model.getObject().getGovernors()){
            if(governorRef.getUid().equals(governorUid)){
                governorToRemove = governorRef;
                break;
            }
        }

        model.getObject().getGovernors().remove(governorToRemove);
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

        InducementType<ResourceType> resourceInducementToRemove = new InducementType<>();
        for(InducementType<ResourceType> inducementRef: model.getObject().getResourceInducements()){
            if(inducementRef.getUid().equals(resourceInducementUid)){
                resourceInducementToRemove = inducementRef;
                break;
            }
        }
        model.getObject().getResourceInducements().remove(resourceInducementToRemove);
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

        InducementType<RoleType> roleInducementToRemove = new InducementType<>();
        for(InducementType<RoleType> inducementRef: model.getObject().getRoleInducements()){
            if(inducementRef.getUid().equals(roleInducementUid)){
                roleInducementToRemove = inducementRef;
                break;
            }
        }

        model.getObject().getRoleInducements().remove(roleInducementToRemove);
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
                for(ObjectReferenceType orgReference: orgUnit.getParentOrgUnits()){
                    if(uid.equals(orgReference.getUid())){
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
                    success("Org. unit: '" + orgUnit.getName() + "' federation sharing set successfully.");
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

        //Provisioning policy must always be specified
        if(orgUnit.getProvisioningPolicy() == null){
            error("Provisioning policy must be specified for each and every org. unit.");
            target.add(getFeedbackPanel());
            return;
        }

        //If org. unit is shared in federation, sharing policy must be specified
        if(orgUnit.isSharedInFederation()){
            if(orgUnit.getSharingPolicy() == null){
                error("Sharing policy must be specified for org. unit shared in federation. Specify a sharing" +
                        "policy for this org. unit or do not share it in federation.");
                target.add(getFeedbackPanel());
                return;
            }
        }

        //Filtering empty org. unit types
        List<String> newOrgTypes = new ArrayList<>();
        for(String type: orgUnit.getOrgType()){
            if(type != null && StringUtils.isNotEmpty(type)){
                newOrgTypes.add(type);
            }
        }
        orgUnit.getOrgType().clear();
        orgUnit.getOrgType().addAll(newOrgTypes);

        //Filtering empty parent references
        List<ObjectReferenceType<OrgType>> newParentReferences = new ArrayList<>();
        for(ObjectReferenceType<OrgType> parentRef: orgUnit.getParentOrgUnits()){
            if(parentRef != null && parentRef.getUid() != null && parentRef.getType() != null){
                newParentReferences.add(parentRef);
            }
        }
        orgUnit.getParentOrgUnits().clear();
        orgUnit.getParentOrgUnits().addAll(newParentReferences);


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