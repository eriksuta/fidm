package com.esuta.fidm.gui.page.federation;

import com.esuta.fidm.gui.component.behavior.VisibleEnableBehavior;
import com.esuta.fidm.gui.component.data.FederationObjectInformationProvider;
import com.esuta.fidm.gui.component.data.table.TablePanel;
import com.esuta.fidm.gui.component.modal.ObjectChooserDialog;
import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.gui.page.federation.component.FederationOrgTreeDataProvider;
import com.esuta.fidm.gui.page.org.PageOrgList;
import com.esuta.fidm.gui.page.org.component.data.SelectableFolderContent;
import com.esuta.fidm.gui.page.org.component.data.TreeStateSet;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.ObjectAlreadyExistsException;
import com.esuta.fidm.infra.exception.ObjectNotFoundException;
import com.esuta.fidm.model.federation.client.ObjectTypeRestResponse;
import com.esuta.fidm.model.federation.service.ObjectInformation;
import com.esuta.fidm.repository.schema.core.*;
import com.esuta.fidm.repository.schema.support.FederationIdentifierType;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.tree.ISortableTreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.TableTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.table.TreeColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.eclipse.jetty.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *  @author shood
 * */
public class PageOrgPreview extends PageBase{

    private static final transient Logger LOGGER = Logger.getLogger(PageOrgPreview.class);

    private static final String ID_NAME = "name";
    private static final String ID_DISPLAY_NAME = "displayName";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_LOCALITY = "locality";
    private static final String ID_TYPE = "type";
    private static final String ID_PARENT_ORG = "parentOrgUnits";
    private static final String ID_PROVISIONING_POLICY_LABEL = "provisioningPolicyLabel";
    private static final String ID_PROVISIONING_POLICY_EDIT = "provisioningPolicyEdit";
    private static final String ID_PROVISIONING_POLICY_CHOOSER = "provisioningPolicyChooser";

    private static final String ID_TREE_CONTAINER = "treeContainer";
    private static final String ID_TREE_BODY_CONTAINER = "treeBodyContainer";
    private static final String ID_TREE = "tree";
    private static final String ID_TREE_HEADER = "treeHeader";
    private static final String ID_TREE_EXPAND = "treeExpand";
    private static final String ID_TREE_COLLAPSE = "treeCollapse";

    private static final String ID_GOVERNOR_CONTAINER = "governorsContainer";
    private static final String ID_GOVERNOR_LABEL = "governorsLabel";
    private static final String ID_GOVERNOR_TABLE = "governorsTable";

    private static final String ID_RESOURCE_IND_CONTAINER = "resourceInducementsContainer";
    private static final String ID_RESOURCE_INDUCEMENT_LABEL = "resourceInducementsLabel";
    private static final String ID_RESOURCE_IND_TABLE = "resourceInducementsTable";

    private static final String ID_ROLE_IND_CONTAINER = "roleInducementsContainer";
    private static final String ID_ROLE_INDUCEMENT_LABEL = "roleInducementsLabel";
    private static final String ID_ROLE_IND_TABLE = "roleInducementsTable";

    private static final String ID_BUTTON_SHARE = "shareButton";
    private static final String ID_BUTTON_SHARE_HIERARCHY = "shareHierarchyButton";
    private static final String ID_BUTTON_CANCEL = "cancelButton";

    private IModel<OrgType> model;
    private List<OrgType> providedOrgUnits;
    private String uniqueOrgAttribute;
    private IModel<OrgType> selectedOrgInHierarchy = new LoadableModel<OrgType>() {

        @Override
        protected OrgType load() {
            return getSelectedOrgInHierarchy();
        }
    };

    public PageOrgPreview(){
        this(null, null, null);
    }

    public PageOrgPreview(final OrgType org, List<OrgType> providedOrgUnits, String uniqueOrgAttribute){
        model = new LoadableModel<OrgType>(false) {

            @Override
            protected OrgType load() {
                return org;
            }
        };

        this.uniqueOrgAttribute = uniqueOrgAttribute;
        this.providedOrgUnits = providedOrgUnits;
        initLayout();
    }

    private void initLayout(){
        Label name = new Label(ID_NAME, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return model.getObject().getName();
            }
        });
        add(name);

        Label displayName = new Label(ID_DISPLAY_NAME, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return model.getObject().getDisplayName();
            }
        });
        add(displayName);

        Label description = new Label(ID_DESCRIPTION, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return model.getObject().getDescription();
            }
        });
        add(description);

        Label locality = new Label(ID_LOCALITY, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return model.getObject().getLocality();
            }
        });
        add(locality);

        Label type = new Label(ID_TYPE, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                StringBuilder sb = new StringBuilder();

                for(String type: model.getObject().getOrgType()){
                    sb.append(type);
                    sb.append(", ");
                }

                return sb.toString();
            }
        });
        add(type);

        Label parentOrgLabel = new Label(ID_PARENT_ORG, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return Integer.toString(model.getObject().getParentOrgUnits().size());
            }
        });
        add(parentOrgLabel);

        //Provisioning policy components
        TextField provisioningPolicyLabel = new TextField<>(ID_PROVISIONING_POLICY_LABEL, createProvisioningPolicyLabel());
        provisioningPolicyLabel.setOutputMarkupId(true);
        provisioningPolicyLabel.add(AttributeAppender.replace("placeholder", "Set policy"));
        provisioningPolicyLabel.setEnabled(false);
        add(provisioningPolicyLabel);

        AjaxLink provisioningPolicyEdit = new AjaxLink(ID_PROVISIONING_POLICY_EDIT) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                provisioningPolicyEditPerformed(target);
            }
        };
        add(provisioningPolicyEdit);

        initModalWindows();
        initOrgHierarchyPreview();
        initGovernorPreview();
        initInducementsPreview();
        initButtons();
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
                    ProvisioningPolicyType policy = getModelService().readObject(ProvisioningPolicyType.class, provisioningPolicyUid);
                    return policy.getName();
                } catch (DatabaseCommunicationException e) {
                    error("Could not load provisioning policy with uid: '" + provisioningPolicyUid + "' from the repository.");
                    LOGGER.error("Could not load provisioning policy with uid: '" + provisioningPolicyUid + "' from the repository.");
                }

                return "Set Policy";
            }
        };
    }

    private void initModalWindows(){
        ModalWindow provisioningPolicyChooser = new ObjectChooserDialog<ProvisioningPolicyType>(
                ID_PROVISIONING_POLICY_CHOOSER, ProvisioningPolicyType.class){

            @Override
            public void objectChoosePerformed(AjaxRequestTarget target, IModel<ProvisioningPolicyType> rowModel) {
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
    }

    private void initOrgHierarchyPreview(){
        WebMarkupContainer treeContainer = new WebMarkupContainer(ID_TREE_CONTAINER);
        treeContainer.setOutputMarkupId(true);
        add(treeContainer);

        WebMarkupContainer treeHeader = new WebMarkupContainer(ID_TREE_HEADER);
        treeHeader.setOutputMarkupId(true);
        treeContainer.add(treeHeader);

        AjaxLink treeExpand = new AjaxLink(ID_TREE_EXPAND) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                treeExpandPerformed(target);
            }
        };
        treeHeader.add(treeExpand);

        AjaxLink treeCollapse = new AjaxLink(ID_TREE_COLLAPSE) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                treeCollapsePerformed(target);
            }
        };
        treeHeader.add(treeCollapse);

        FederationOrgTreeDataProvider treeProvider = new FederationOrgTreeDataProvider(model, providedOrgUnits, uniqueOrgAttribute);
        List<IColumn<OrgType, String>> columns = new ArrayList<>();
        columns.add(new TreeColumn<OrgType, String>(new Model<>("Org. Unit Hierarchy")));

        WebMarkupContainer treeBodyContainer = new WebMarkupContainer(ID_TREE_BODY_CONTAINER);
        treeContainer.add(treeBodyContainer);

        TableTree<OrgType, String> tree = new TableTree<OrgType, String>(ID_TREE, columns, treeProvider,
                Integer.MAX_VALUE, new TreeStateModel(treeProvider)) {

            @Override
            protected Component newContentComponent(String id, IModel<OrgType> model) {
                return new SelectableFolderContent(id, this, model, selectedOrgInHierarchy) {

                    @Override
                    protected void onClick(AjaxRequestTarget target) {
                        super.onClick(target);

                        selectTreeItemPerformed(target);
                    }
                };
            }

            @Override
            protected Item<OrgType> newRowItem(String id, int index, final IModel<OrgType> model) {
                Item<OrgType> item = super.newRowItem(id, index, model);
                item.add(AttributeModifier.append("class", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        OrgType itemObject = model.getObject();
                        if (itemObject != null && itemObject.equals(selectedOrgInHierarchy.getObject())) {
                            return "success";
                        }

                        return null;
                    }
                }));
                return item;
            }
        };

        tree.getTable().add(AttributeModifier.replace("class", "table table-striped table-condensed"));
        treeBodyContainer.add(tree);
    }

    private TableTree getTree() {
        return (TableTree) get(ID_TREE_CONTAINER + ":" + ID_TREE_BODY_CONTAINER + ":" + ID_TREE);
    }

    private OrgType getSelectedOrgInHierarchy() {
        TableTree<OrgType, String> tree = getTree();
        ITreeProvider<OrgType> provider = tree.getProvider();
        Iterator<? extends OrgType> iterator = provider.getRoots();

        return iterator.hasNext() ? iterator.next() : null;
    }

    private void initGovernorPreview(){
        WebMarkupContainer governorContainer = new WebMarkupContainer(ID_GOVERNOR_CONTAINER);
        governorContainer.setOutputMarkupId(true);
        add(governorContainer);

        Label governorsLabel = new Label(ID_GOVERNOR_LABEL, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return "Governors (" + model.getObject().getGovernors().size() + ")";
            }
        });
        governorContainer.add(governorsLabel);

        final FederationObjectInformationProvider governorProvider = new FederationObjectInformationProvider(getPage(),
                getGovernorIdentifiers());
        List<IColumn> governorColumns = createObjectInformationColumns();
        TablePanel governorTable = new TablePanel(ID_GOVERNOR_TABLE, governorProvider, governorColumns, 10);
        governorTable.add(new VisibleEnableBehavior() {

            @Override
            public boolean isVisible() {
                return governorProvider.size() > 0;
            }
        });
        governorTable.setShowPaging(false);
        governorContainer.add(governorTable);
    }

    private List<FederationIdentifierType> getGovernorIdentifiers(){
        List<FederationIdentifierType> list = new ArrayList<>();

        for(ObjectReferenceType ref: model.getObject().getGovernors()){
            list.add(ref.getFederationIdentifier());
        }

        return list;
    }

    private void initInducementsPreview(){
        //Resource Inducements Container
        WebMarkupContainer resourceInducementsContainer = new WebMarkupContainer(ID_RESOURCE_IND_CONTAINER);
        resourceInducementsContainer.setOutputMarkupId(true);
        add(resourceInducementsContainer);

        Label resourceInducementsLabel = new Label(ID_RESOURCE_INDUCEMENT_LABEL, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return "Resource Inducements (" + model.getObject().getResourceInducements().size() + ")";
            }
        });
        resourceInducementsContainer.add(resourceInducementsLabel);

        final FederationObjectInformationProvider resourceInducementsProvider = new FederationObjectInformationProvider(getPage(),
                getResourceInducementsIdentifier());
        List<IColumn> resourceInducementsColumns = createObjectInformationColumns();
        TablePanel resourceInducementsTable = new TablePanel(ID_RESOURCE_IND_TABLE, resourceInducementsProvider, resourceInducementsColumns, 10);
        resourceInducementsTable.add(new VisibleEnableBehavior() {

            @Override
            public boolean isVisible() {
                return resourceInducementsProvider.size() > 0;
            }
        });
        resourceInducementsTable.setShowPaging(false);
        resourceInducementsContainer.add(resourceInducementsTable);

        //Role Inducements Container
        WebMarkupContainer roleInducementsContainer = new WebMarkupContainer(ID_ROLE_IND_CONTAINER);
        roleInducementsContainer.setOutputMarkupId(true);
        add(roleInducementsContainer);

        Label roleInducementsLabel = new Label(ID_ROLE_INDUCEMENT_LABEL, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return "RoleInducements (" + model.getObject().getRoleInducements().size() + ")";
            }
        });
        roleInducementsContainer.add(roleInducementsLabel);

        final FederationObjectInformationProvider roleInducementsProvider = new FederationObjectInformationProvider(getPage(),
                getRoleInducementsIdentifiers());
        List<IColumn> roleInducementsColumns = createObjectInformationColumns();
        TablePanel roleInducementTable = new TablePanel(ID_ROLE_IND_TABLE, roleInducementsProvider, roleInducementsColumns, 10);
        roleInducementTable.add(new VisibleEnableBehavior() {

            @Override
            public boolean isVisible() {
                return roleInducementsProvider.size() > 0;
            }
        });
        roleInducementTable.setShowPaging(false);
        roleInducementsContainer.add(roleInducementTable);
    }

    private List<FederationIdentifierType> getResourceInducementsIdentifier(){
        List<FederationIdentifierType> list = new ArrayList<>();

        for(InducementType ref: model.getObject().getResourceInducements()){
            list.add(ref.getFederationIdentifier());
        }

        return list;
    }

    private List<FederationIdentifierType> getRoleInducementsIdentifiers(){
        List<FederationIdentifierType> list = new ArrayList<>();

        for(InducementType ref: model.getObject().getRoleInducements()){
            list.add(ref.getFederationIdentifier());
        }

        return list;
    }

    private List<IColumn> createObjectInformationColumns(){
        List<IColumn> columns = new ArrayList<>();
        columns.add(new PropertyColumn<ObjectInformation, String>(new Model<>("Name"), "objectName", "objectName"));
        columns.add(new PropertyColumn<ObjectInformation, String>(new Model<>("Description"), "objectDescription", "objectDescription"));
        return columns;
    }

//    private void initButtons(Form mainForm){
    private void initButtons(){
        AjaxLink cancel = new AjaxLink(ID_BUTTON_CANCEL) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                cancelPerformed();
            }
        };
        add(cancel);

        AjaxLink shareHierarchy = new AjaxLink(ID_BUTTON_SHARE_HIERARCHY) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                shareHierarchyPerformed(target);
            }
        };
        add(shareHierarchy);

        AjaxLink share = new AjaxLink(ID_BUTTON_SHARE) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                sharePerformed(target);
            }
        };
        add(share);
    }

    private void treeExpandPerformed(AjaxRequestTarget target){
        TableTree tree = getTree();
        TreeStateModel model = (TreeStateModel) tree.getDefaultModel();
        model.expandAll();

        target.add(tree);
    }

    private void treeCollapsePerformed(AjaxRequestTarget target){
        TableTree tree = getTree();
        TreeStateModel model = (TreeStateModel) tree.getDefaultModel();
        model.collapseAll();

        target.add(tree);
    }

    private void selectTreeItemPerformed(AjaxRequestTarget target){
        target.add(getFeedbackPanel());
        setResponsePage(new PageOrgPreview(selectedOrgInHierarchy.getObject(), providedOrgUnits, uniqueOrgAttribute));
    }

    private void cancelPerformed(){
        setResponsePage(PageFederationList.class);
    }

    private void shareHierarchyPerformed(AjaxRequestTarget target){
        if(model == null || model.getObject() == null){
            warn("Can't share the org. unit.");
            target.add(getFeedbackPanel());
            return;
        }

        if(model.getObject().getProvisioningPolicy() == null){
            warn("Provisioning policy for org. unit not selected.");
            target.add(getFeedbackPanel());
            return;
        }

        OrgType rootToShare = model.getObject();
        ObjectReferenceType provisioningPolicyRef = rootToShare.getProvisioningPolicy();
        clearOrgParentReferences(rootToShare);
        try {
            List<OrgType> hierarchyToShare = getOrgHierarchyToShare(rootToShare, new ArrayList<OrgType>(), providedOrgUnits);

            List<OrgType> createdOrgUnits = new ArrayList<>();
            for(OrgType org: hierarchyToShare){
                FederationIdentifierType orgIdentifier = org.getFederationIdentifier();
                FederationMemberType member = getFederationMemberByName(orgIdentifier.getFederationMemberId());

                ObjectTypeRestResponse<OrgType> response = getFederationServiceClient().createGetOrgUnitRequest(member, orgIdentifier);

                int status = response.getStatus();
                if(HttpStatus.OK_200 == status){
                    OrgType o = response.getValue();
                    o = getModelService().createObject(o);
                    o.setProvisioningPolicy(provisioningPolicyRef);
                    createdOrgUnits.add(o);
                    info("Org. unit shared correctly. New org.: '" + o.getName() + "'(" + o.getUid() + ").");

                } else {
                    String message = response.getMessage();
                    LOGGER.error("Could not share org. unit. REST response: " + status + ", message: " + message);
                    error("Could not share org. unit. REST response: " + status + ", message: " + message);
                }
            }

            //Prepare correct parent references and update existing units
            prepareParentOrgReferences(findRoot(createdOrgUnits), createdOrgUnits);

            //And update existing org. hierarchy with correct parent org. references
            for(OrgType org: createdOrgUnits){
                getModelService().updateObject(org);
            }

        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Could not create and process request to share selected org. unit.", e);
            error("Could not create and process request to share selected org. unit. Reason: " + e);
        } catch (ObjectAlreadyExistsException e) {
            LOGGER.error("Could not create org. unit. Conflicting object already exists.", e);
            error("Could not create org. unit. Conflicting object already exists. Reason: " + e);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            LOGGER.error("Could not create org. unit. Can't determine the origin of org. unit.", e);
            error("Could not create org. unit. Can't determine the origin of org. unit." + e);
        } catch (ObjectNotFoundException e) {
            LOGGER.error("Could not update org. unit. Could not find target org. unit to update.", e);
            error("Could not update org. unit. Could not find target org. unit to update." + e);
        }

        setResponsePage(PageOrgList.class);
        target.add(getFeedbackPanel());
    }

    private List<OrgType> getOrgHierarchyToShare(OrgType root, List<OrgType> hierarchy, List<OrgType> allOrgUnits)
            throws NoSuchFieldException, IllegalAccessException {

        hierarchy.add(root);
        List<OrgType> children = new ArrayList<>();

        //First, get the children for root from all provided org. units
        for(OrgType org: allOrgUnits){
            for(ObjectReferenceType parent: org.getParentOrgUnits()){
                String uniqueAttributeValue = parent.getFederationIdentifier().getUniqueAttributeValue();

                if(isChild(root, uniqueAttributeValue)){
                    children.add(org);
                }
            }
        }

        //Then repeat the process for all the children units
        for(OrgType child: children){
            getOrgHierarchyToShare(child, hierarchy, allOrgUnits);
        }

        //Finally, then return the hierarchy to be shared
        return hierarchy;
    }

    private boolean isChild(OrgType org, String uniqueAttributeValue) throws NoSuchFieldException, IllegalAccessException {
        Field uniqueAttribute = org.getClass().getDeclaredField(uniqueOrgAttribute);
        uniqueAttribute.setAccessible(true);
        String attributeValue = (String)uniqueAttribute.get(org);

        return uniqueAttributeValue.equals(attributeValue);
    }

    private void prepareParentOrgReferences(OrgType root, List<OrgType> hierarchy)
            throws NoSuchFieldException, IllegalAccessException {

        if(root == null){
            return;
        }

        //First, get the children of the root
        List<OrgType> children = new ArrayList<>();
        for(OrgType org: hierarchy){
            for(ObjectReferenceType parent: org.getParentOrgUnits()){
                String uniqueAttributeValue = parent.getFederationIdentifier().getUniqueAttributeValue();

                if(isChild(root, uniqueAttributeValue)){
                    children.add(org);
                }
            }
        }

        //Prepare references for current children
        for(OrgType child: children){
            for(ObjectReferenceType parent: child.getParentOrgUnits()){
                parent.setUid(root.getUid());
            }
        }

        //Repeat the process for all the children
        for(OrgType child: children){
            prepareParentOrgReferences(child, hierarchy);
        }
    }

    private OrgType findRoot(List<OrgType> hierarchy){
        for(OrgType org: hierarchy){
            if(org.getParentOrgUnits().isEmpty()){
                return org;
            }
        }

        return null;
    }

    private void provisioningPolicyEditPerformed(AjaxRequestTarget target){
        ModalWindow window = (ModalWindow) get(ID_PROVISIONING_POLICY_CHOOSER);
        window.show(target);
    }

    private void provisioningPolicyChoosePerformed(AjaxRequestTarget target, IModel<ProvisioningPolicyType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            return;
        }

        if(model.getObject() == null){
            return;
        }

        OrgType org = model.getObject();
        ProvisioningPolicyType policy = rowModel.getObject();
        ObjectReferenceType policyRef = new ObjectReferenceType();
        policyRef.setUid(policy.getUid());
        policyRef.setSharedInFederation(false);
        org.setProvisioningPolicy(policyRef);

        ModalWindow window = (ModalWindow) get(ID_PROVISIONING_POLICY_CHOOSER);
        window.close(target);

        info("Provisioning Policy was selected. If you will share entire org. unit hierarchy, the chosen provisioning policy" +
                " will be applied to all org. units in hierarchy.");
        target.add(get(ID_PROVISIONING_POLICY_LABEL), getFeedbackPanel());
    }

    private void sharePerformed(AjaxRequestTarget target){
        if(model == null || model.getObject() == null){
            warn("Can't share the org. unit.");
            target.add(getFeedbackPanel());
            return;
        }

        if(model.getObject().getProvisioningPolicy() == null){
            warn("Provisioning policy for org. unit not selected.");
            target.add(getFeedbackPanel());
            return;
        }

        OrgType orgToShare = model.getObject();
        FederationIdentifierType orgIdentifier = orgToShare.getFederationIdentifier();
        FederationMemberType member = getFederationMemberByName(orgIdentifier.getFederationMemberId());

        if(member == null){
            warn("There currently isn't a membership with federation member: '" + orgIdentifier.getFederationMemberId() + "'.");
            target.add(getFeedbackPanel());
            return;
        }

        try {
            ObjectTypeRestResponse<OrgType> response = getFederationServiceClient().createGetOrgUnitRequest(member, orgIdentifier);

            int status = response.getStatus();
            if(HttpStatus.OK_200 == status){
                OrgType org = response.getValue();
                clearOrgParentReferences(org);
                org.setProvisioningPolicy(model.getObject().getProvisioningPolicy());
                org = getModelService().createObject(org);
                info("Org. unit shared correctly. New org.: '" + org.getName() + "'(" + org.getUid() + ").");

            } else {
                String message = response.getMessage();
                LOGGER.error("Could not share org. unit. REST response: " + status + ", message: " + message);
                error("Could not share org. unit. REST response: " + status + ", message: " + message);
            }

        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Could not create and process request to share selected org. unit.", e);
            error("Could not create and process request to share selected org. unit. Reason: " + e);
        } catch (ObjectAlreadyExistsException e) {
            LOGGER.error("Could not create org. unit. Conflicting object already exists.", e);
            error("Could not create org. unit. Conflicting object already exists. Reason: " + e);
        }

        setResponsePage(PageOrgList.class);
        target.add(getFeedbackPanel());
    }

    private void clearOrgParentReferences(OrgType org){
        org.getParentOrgUnits().clear();
    }

    private static class TreeStateModel extends AbstractReadOnlyModel<Set<OrgType>> {

        private TreeStateSet<OrgType> set = new TreeStateSet<>();
        private ISortableTreeProvider provider;

        TreeStateModel(ISortableTreeProvider provider) {
            this.provider = provider;
        }

        @Override
        public Set<OrgType> getObject() {
            //just to have root expanded at all time
            if (set.isEmpty()) {
                Iterator<OrgType> iterator = provider.getRoots();
                if (iterator.hasNext()) {
                    set.add(iterator.next());
                }

            }
            return set;
        }

        public void expandAll() {
            set.expandAll();
        }

        public void collapseAll() {
            set.collapseAll();
        }
    }
}