package com.esuta.fidm.gui.page.org.component;

import com.esuta.fidm.gui.component.data.ObjectDataProvider;
import com.esuta.fidm.gui.component.data.column.EditDeleteButtonColumn;
import com.esuta.fidm.gui.component.data.column.LinkColumn;
import com.esuta.fidm.gui.component.data.table.TablePanel;
import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.gui.page.org.PageOrg;
import com.esuta.fidm.gui.page.org.PageOrgList;
import com.esuta.fidm.gui.page.org.component.data.OrgTreeDataProvider;
import com.esuta.fidm.gui.page.org.component.data.SelectableFolderContent;
import com.esuta.fidm.gui.page.org.component.data.TreeStateSet;
import com.esuta.fidm.gui.page.users.PageUser;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.GeneralException;
import com.esuta.fidm.infra.exception.ObjectNotFoundException;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.model.federation.client.SimpleRestResponse;
import com.esuta.fidm.repository.schema.core.AssignmentType;
import com.esuta.fidm.repository.schema.core.ObjectReferenceType;
import com.esuta.fidm.repository.schema.core.OrgType;
import com.esuta.fidm.repository.schema.core.UserType;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.tree.ISortableTreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.TableTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.table.TreeColumn;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.eclipse.jetty.http.HttpStatus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *  @author shood
 *
 *  based on implementation by lazyman, see
 *  (https://github.com/Evolveum/midpoint/blob/0f5c644324da0f574f084dfc2ede306f2538a053/gui/admin-gui/src/main/java/com/evolveum/midpoint/web/page/admin/users/component/TreeTablePanel.java)
 * */
public class OrgUnitTreePanel extends Panel {

    private static final Logger LOGGER = Logger.getLogger(OrgUnitTreePanel.class);

    private static final String ID_TREE_HEADER = "treeHeader";
    private static final String ID_BUTTON_EXPAND_TREE = "treeExpand";
    private static final String ID_BUTTON_COLLAPSE_TREE = "treeCollapse";
    private static final String ID_BUTTON_DELETE_ROOT = "deleteRoot";
    private static final String ID_BUTTON_EDIT_ROOT = "editRoot";
    private static final String ID_TREE_CONTAINER = "treeContainer";
    private static final String ID_TREE = "tree";
    private static final String ID_FORM = "form";
    private static final String ID_CHILDREN_TABLE = "childrenTable";
    private static final String ID_MEMBER_TABLE = "memberTable";

    private IModel<String> rootUidModel;
    private IModel<OrgType> selected = new LoadableModel<OrgType>() {

        @Override
        protected OrgType load() {
            return getRootOrgUnitFromProvider();
        }
    };

    public OrgUnitTreePanel(String id, IModel<String> rootOid) {
        super(id);
        this.rootUidModel = rootOid;
        setOutputMarkupId(true);

        initLayout();
    }

    private PageBase getPageBase(){
        return (PageBase) getPage();
    }

    protected void initLayout() {
        WebMarkupContainer treeHeader = new WebMarkupContainer(ID_TREE_HEADER);
        treeHeader.setOutputMarkupId(true);
        add(treeHeader);

        AjaxLink editRoot = new AjaxLink(ID_BUTTON_EDIT_ROOT) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                editRootPerformed(target);
            }
        };
        treeHeader.add(editRoot);

        AjaxLink deleteRoot = new AjaxLink(ID_BUTTON_DELETE_ROOT) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                deleteRootPerformed(target);
            }
        };
        treeHeader.add(deleteRoot);

        AjaxLink treeExpand = new AjaxLink(ID_BUTTON_EXPAND_TREE) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                treeExpandPerformed(target);
            }
        };
        treeHeader.add(treeExpand);

        AjaxLink treeCollapse = new AjaxLink(ID_BUTTON_COLLAPSE_TREE) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                treeCollapsePerformed(target);
            }
        };
        treeHeader.add(treeCollapse);

        ISortableTreeProvider provider = new OrgTreeDataProvider(this, rootUidModel);
        List<IColumn<OrgType, String>> columns = new ArrayList<>();
        columns.add(new TreeColumn<OrgType, String>(new Model<>("Org. Unit Hierarchy")));

        WebMarkupContainer treeContainer = new WebMarkupContainer(ID_TREE_CONTAINER) {

            @Override
            public void renderHead(IHeaderResponse response) {
                super.renderHead(response);

                //method computes height based on document.innerHeight() - screen height;
                response.render(OnDomReadyHeaderItem.forScript("updateHeight('" + getMarkupId()
                        + "', ['#" + OrgUnitTreePanel.this.get(ID_FORM).getMarkupId() + "'], ['#"
                        + OrgUnitTreePanel.this.get(ID_TREE_HEADER).getMarkupId() + "'])"));
            }
        };
        add(treeContainer);

        TableTree<OrgType, String> tree = new TableTree<OrgType, String>(ID_TREE, columns, provider,
                Integer.MAX_VALUE, new TreeStateModel(provider)) {

            @Override
            protected Component newContentComponent(String id, IModel<OrgType> model) {
                return new SelectableFolderContent(id, this, model, selected) {

                    @Override
                    protected void onClick(AjaxRequestTarget target) {
                        super.onClick(target);

                        selectTreeItemPerformed(target);
                    }
                };
            }

            @Override
            protected Item<OrgType> newRowItem(String id, int index, final IModel<OrgType> model) {
                Item item = super.newRowItem(id, index, model);
                item.add(AttributeModifier.append("class", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        OrgType itemObject = model.getObject();
                        if (itemObject != null && itemObject.equals(selected.getObject())) {
                            return "success";
                        }

                        return null;
                    }
                }));
                return item;
            }
        };

        tree.getTable().add(AttributeModifier.replace("class", "table table-striped table-condensed"));
        treeContainer.add(tree);

        Form form = new Form(ID_FORM);
        form.setOutputMarkupId(true);
        add(form);

        initChildrenTable(form);
        initMemberTable(form);
    }

    private void initChildrenTable(Form form) {
        ObjectDataProvider tableProvider = new ObjectDataProvider<OrgType>(this, OrgType.class) {

            @Override
            public List<OrgType> applyDataFilter(List<OrgType> list) {
                return createChildrenTableFilter(list);
            }
        };

        List<IColumn<OrgType, String>> tableColumns = createChildrenTableColumns();
        TablePanel table = new TablePanel(ID_CHILDREN_TABLE, tableProvider, tableColumns, 10);
        table.setOutputMarkupId(true);
        form.add(table);
    }

    private List<IColumn<OrgType, String>> createChildrenTableColumns() {
        List<IColumn<OrgType, String>> columns = new ArrayList<>();

        columns.add(new LinkColumn<OrgType>(new Model<>("Name"), "name", "name"){

            @Override
            public void onClick(AjaxRequestTarget target, IModel<OrgType> rowModel) {
                OrgUnitTreePanel.this.editChildrenPerformed(target, rowModel);
            }
        });
        columns.add(new PropertyColumn<OrgType, String>(new Model<>("Display Name"), "displayName", "displayName"));
        columns.add(new PropertyColumn<OrgType, String>(new Model<>("Type"), "orgType", "orgType"));
        columns.add(new PropertyColumn<OrgType, String>(new Model<>("Locality"), "locality", "locality"));
        columns.add(new AbstractColumn<OrgType, String>(new Model<>("Governors")) {

            @Override
            public void populateItem(Item<ICellPopulator<OrgType>> cellItem, String componentId, final IModel<OrgType> rowModel) {
                cellItem.add(new Label(componentId, new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return Integer.toString(rowModel.getObject().getGovernors().size());
                    }
                }));
            }
        });
        columns.add(new AbstractColumn<OrgType, String>(new Model<>("Origin")) {

            @Override
            public void populateItem(Item<ICellPopulator<OrgType>> cellItem, String componentId, final IModel<OrgType> rowModel) {
                cellItem.add(new Label(componentId, new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return rowModel.getObject().getFederationIdentifier() == null ? "Origin" : "Copy";
                    }
                }));
            }
        });
        columns.add(new AbstractColumn<OrgType, String>(new Model<>("Members")) {

            @Override
            public void populateItem(Item<ICellPopulator<OrgType>> cellItem, String componentId, final IModel<OrgType> rowModel) {
                cellItem.add(new Label(componentId, new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return Integer.toString(getNumberOfOrgUnitMembers(rowModel));
                    }
                }));
            }
        });
        columns.add(new EditDeleteButtonColumn<OrgType>(new Model<>("Actions")){

            @Override
            public void editPerformed(AjaxRequestTarget target, IModel<OrgType> rowModel) {
                OrgUnitTreePanel.this.editChildrenPerformed(target, rowModel);
            }

            @Override
            public void removePerformed(AjaxRequestTarget target, IModel<OrgType> rowModel) {
                OrgUnitTreePanel.this.removeChildrenPerformed(target, rowModel);
            }
        });

        return columns;
    }

    private int getNumberOfOrgUnitMembers(IModel<OrgType> orgModel){
        int count = 0;
        if(orgModel == null || orgModel.getObject() == null){
            return 0;
        }

        String orgUid = orgModel.getObject().getUid();

        try {
            List<UserType> userList = getPageBase().getModelService().getAllObjectsOfType(UserType.class);

            for(UserType user: userList){
                if(user.getOrgUnitAssignments().contains(orgUid)){
                    count++;
                }
            }

        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Could not retrieve all Org. units from the repository.");
        }

        return count;
    }

    private List<OrgType> createChildrenTableFilter(List<OrgType> list){
        List<OrgType> filteredList = new ArrayList<>();
        String selectedOrgUid = selected.getObject().getUid();

        for(OrgType org: list){
            for(ObjectReferenceType parentRef: org.getParentOrgUnits()){
                if(selectedOrgUid.equals(parentRef.getUid())){
                    filteredList.add(org);
                }
            }
        }

        return filteredList;
    }

    private void initMemberTable(Form form) {
        ObjectDataProvider tableProvider = new ObjectDataProvider<UserType>(this, UserType.class) {

            @Override
            public List<UserType> applyDataFilter(List<UserType> list) {
                return crateMemberTableFilter(list);
            }
        };

        List<IColumn<UserType, String>> tableColumns = createMemberTableColumns();
        TablePanel table = new TablePanel(ID_MEMBER_TABLE, tableProvider, tableColumns, 10);
        table.setOutputMarkupId(true);
        form.add(table);
    }

    private List<IColumn<UserType, String>> createMemberTableColumns() {
        List<IColumn<UserType, String>> columns = new ArrayList<>();

        columns.add(new LinkColumn<UserType>(new Model<>("Name"), "name", "name"){

            @Override
            public void onClick(AjaxRequestTarget target, IModel<UserType> rowModel) {
                OrgUnitTreePanel.this.editMemberPerformed(target, rowModel);
            }
        });
        columns.add(new PropertyColumn<UserType, String>(new Model<>("Given Name"), "givenName", "givenName"));
        columns.add(new PropertyColumn<UserType, String>(new Model<>("Family Name"), "familyName", "familyName"));
        columns.add(new PropertyColumn<UserType, String>(new Model<>("E-mail Address"), "emailAddress", "emailAddress"));

        columns.add(new EditDeleteButtonColumn<UserType>(new Model<>("Actions")){

            @Override
            public void editPerformed(AjaxRequestTarget target, IModel<UserType> rowModel) {
                OrgUnitTreePanel.this.editMemberPerformed(target, rowModel);
            }

            @Override
            public void removePerformed(AjaxRequestTarget target, IModel<UserType> rowModel) {
                OrgUnitTreePanel.this.removeMemberPerformed(target, rowModel);
            }
        });

        return columns;
    }

    private List<UserType> crateMemberTableFilter(List<UserType> list){
        List<UserType> filteredList = new ArrayList<>();
        String selectedOrgUid = selected.getObject().getUid();

        for(UserType user: list){
            for(AssignmentType orgRef: user.getOrgUnitAssignments()){
                if(selectedOrgUid.equals(orgRef.getUid())){
                    filteredList.add(user);
                }
            }
        }

        return filteredList;
    }

    private TableTree getTree() {
        return (TableTree) get(ID_TREE_CONTAINER + ":" + ID_TREE);
    }

    private TablePanel getChildrenTable(){
        return (TablePanel) get(ID_FORM + ":" + ID_CHILDREN_TABLE);
    }

    private TablePanel getMemberTable(){
        return (TablePanel) get(ID_FORM + ":" + ID_MEMBER_TABLE);
    }

    private OrgType getRootOrgUnitFromProvider() {
        TableTree<OrgType, String> tree = getTree();
        ITreeProvider<OrgType> provider = tree.getProvider();
        Iterator<? extends OrgType> iterator = provider.getRoots();

        return iterator.hasNext() ? iterator.next() : null;
    }

    private void editRootPerformed(AjaxRequestTarget target){
        if(rootUidModel == null){
            error("Could not edit root org. unit.");
            target.add(getPageBase().getFeedbackPanel());
            return;
        }

        String rootOrgUid = rootUidModel.getObject();

        PageParameters parameters = new PageParameters();
        parameters.add(PageBase.UID_PAGE_PARAMETER_NAME, rootOrgUid);
        setResponsePage(PageOrg.class, parameters);
    }

    private void deleteRootPerformed(AjaxRequestTarget target){
        String rootOrgUid = rootUidModel.getObject();
        ModelService modelService = (ModelService) getPageBase().getModelService();

        try {
            OrgType root = modelService.readObject(OrgType.class, rootOrgUid);

            if(root == null){
                LOGGER.error("Could not find root org. unit to delete.");
                error("Could not find root org. unit to delete.");
                target.add(getPageBase().getFeedbackPanel());
                return;
            }

            if(root.getFederationIdentifier() != null){
                removeOriginLink(root);
            }

            modelService.deleteObject(root);
            LOGGER.info("Root org. unit with uid: '" + rootOrgUid + "' was deleted.");

        } catch (DatabaseCommunicationException | ObjectNotFoundException e) {
            LOGGER.error("Could not delete root org. unit with uid: '" + rootOrgUid + "'.");
            error("Could not delete root org. unit with uid: '" + rootOrgUid + "'.");
        }

        setResponsePage(PageOrgList.class);
    }

    private void selectTreeItemPerformed(AjaxRequestTarget target) {
        TablePanel childrenTable = getChildrenTable();
        childrenTable.setCurrentPage(0);

        TablePanel memberTable = getMemberTable();
        memberTable.setCurrentPage(0);

        target.add(childrenTable, memberTable);
    }

    private void treeCollapsePerformed(AjaxRequestTarget target){
        TableTree tree = getTree();
        TreeStateModel model = (TreeStateModel) tree.getDefaultModel();
        model.collapseAll();

        target.add(tree);
    }

    private void treeExpandPerformed(AjaxRequestTarget target){
        TableTree tree = getTree();
        TreeStateModel model = (TreeStateModel) tree.getDefaultModel();
        model.expandAll();

        target.add(tree);
    }

    private void editChildrenPerformed(AjaxRequestTarget target, IModel<OrgType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            error("Couldn't edit selected org. unit. It is no longer available.");
            target.add(getPageBase().getFeedbackPanel());
            return;
        }

        PageParameters parameters = new PageParameters();
        parameters.add(PageBase.UID_PAGE_PARAMETER_NAME, rowModel.getObject().getUid());

        setResponsePage(PageOrg.class, parameters);
    }

    private void editMemberPerformed(AjaxRequestTarget target, IModel<UserType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            error("Couldn't edit selected user. It is no longer available.");
            target.add(getPageBase().getFeedbackPanel());
            return;
        }

        PageParameters parameters = new PageParameters();
        parameters.add(PageBase.UID_PAGE_PARAMETER_NAME, rowModel.getObject().getUid());

        setResponsePage(PageUser.class, parameters);
    }

    private void removeChildrenPerformed(AjaxRequestTarget target, IModel<OrgType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            error("Object selected to delete does not exist.");
            target.add(getPageBase().getFeedbackPanel());
            return;
        }

        OrgType org = rowModel.getObject();
        String orgName = org.getName();

        try {
            if(org.getFederationIdentifier() != null){
                removeOriginLink(org);
            }

            getPageBase().getModelService().deleteObject(org);
        } catch (GeneralException e){
            LOGGER.error("Could not delete org. unit: '" + orgName + "'. Reason: ", e);
            error("Could not delete org. unit: '" + orgName + "'. Reason: " + e.getExceptionMessage());
            target.add(getPageBase().getFeedbackPanel());
            return;
        }

        LOGGER.info("Org. unit '" + orgName + "' was successfully deleted from the system.");
        success("Org. unit '" + orgName + "' was successfully deleted from the system.");
        target.add(getPageBase().getFeedbackPanel(), getChildrenTable());
    }

    private void removeOriginLink(OrgType org) throws DatabaseCommunicationException {
        String federationMemberName = org.getFederationIdentifier().getFederationMemberId();

        SimpleRestResponse response = getPageBase().getFederationServiceClient()
                .createRemoveOrgLinkRequest(getPageBase().getFederationMemberByName(federationMemberName), org.getFederationIdentifier());

        int status = response.getStatus();
        String message = response.getMessage();

        if(HttpStatus.OK_200 == status){
            success("Link to deleted org. unit removed from origin. Message: " + message);
        } else {
            error("Link to deleted org. unit NOT removed from origin. Message: " + message);
        }
    }

    private void removeMemberPerformed(AjaxRequestTarget target, IModel<UserType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            error("Object selected to delete does not exist.");
            target.add(getPageBase().getFeedbackPanel());
            return;
        }

        UserType user = rowModel.getObject();
        String userName = user.getName();

        try {
            getPageBase().getModelService().deleteObject(user);
        } catch (GeneralException e){
            LOGGER.error("Could not delete user: '" + userName + "'. Reason: ", e);
            error("Could not delete user: '" + userName + "'. Reason: " + e.getExceptionMessage());
            target.add(getPageBase().getFeedbackPanel());
            return;
        }

        LOGGER.info("User '" + userName + "' was successfully deleted from the system.");
        success("User '" + userName + "' was successfully deleted from the system.");
        target.add(getPageBase().getFeedbackPanel(), getMemberTable());
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
