package com.esuta.fidm.gui.page.org.component;

import com.esuta.fidm.gui.component.data.ObjectDataProvider;
import com.esuta.fidm.gui.component.data.column.EditDeleteButtonColumn;
import com.esuta.fidm.gui.component.data.table.TablePanel;
import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.org.component.data.OrgTreeDataProvider;
import com.esuta.fidm.gui.page.org.component.data.SelectableFolderContent;
import com.esuta.fidm.gui.page.org.component.data.TreeStateSet;
import com.esuta.fidm.repository.schema.ObjectType;
import com.esuta.fidm.repository.schema.OrgType;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.tree.ISortableTreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.TableTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.table.TreeColumn;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

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
//    private static final String ID_TREE_MENU = "treeMenu";
    private static final String ID_TREE_CONTAINER = "treeContainer";
    private static final String ID_TREE = "tree";
    private static final String ID_FORM = "form";
    private static final String ID_TABLE = "table";

    private IModel<String> rootOidModel;
    private IModel<OrgType> selected = new LoadableModel<OrgType>() {

        @Override
        protected OrgType load() {
            return getRootOrgUnitFromProvider();
        }
    };

    public OrgUnitTreePanel(String id, IModel<String> rootOid) {
        super(id);
        this.rootOidModel = rootOid;

        initLayout();
    }

    protected void initLayout() {
        WebMarkupContainer treeHeader = new WebMarkupContainer(ID_TREE_HEADER);
        treeHeader.setOutputMarkupId(true);
        add(treeHeader);

        //TODO  - add buttons to collapse/expand to header

        ISortableTreeProvider provider = new OrgTreeDataProvider(this, rootOidModel);
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

        initTable();
    }

    private void initTable() {
        Form form = new Form(ID_FORM);
        form.setOutputMarkupId(true);
        add(form);

        ObjectDataProvider tableProvider = new ObjectDataProvider<ObjectType>(this, ObjectType.class) {

            @Override
            public List<ObjectType> applyDataFilter(List<ObjectType> list) {
                return createTableFilter(list);
            }
        };

        List<IColumn<ObjectType, String>> tableColumns = createTableColumns();
        TablePanel table = new TablePanel(ID_TABLE, tableProvider, tableColumns, 10);
        table.setOutputMarkupId(true);
        form.add(table);
    }

    private List<IColumn<ObjectType, String>> createTableColumns() {
        List<IColumn<ObjectType, String>> columns = new ArrayList<>();

        columns.add(new PropertyColumn<ObjectType, String>(new Model<>("Name"), "name", "name"));
        columns.add(new EditDeleteButtonColumn<ObjectType>(new Model<>("Actions")){

            @Override
            public void editPerformed(AjaxRequestTarget target, IModel<ObjectType> rowModel) {
//                OrgUnitTreePanel.this.editObjectTypePerformed(target, rowModel);
            }

            @Override
            public void removePerformed(AjaxRequestTarget target, IModel<ObjectType> rowModel) {
//                OrgUnitTreePanel.this.removeObjectTypePerformed(target, rowModel);
            }
        });

        return columns;
    }

    /**
     *  TODO - right now, we are just working with OrgType objects, add other objects later
     * */
    private List<ObjectType> createTableFilter(List<ObjectType> list){
        List<ObjectType> filteredList = new ArrayList<>();
        String selectedOrgUid = selected.getObject().getUid();

        for(ObjectType objectType: list){
            if(objectType instanceof OrgType){
                OrgType org = (OrgType) objectType;
                if(selectedOrgUid.equals(org.getUid())){
                    filteredList.add(objectType);
                }
            }
        }

        return filteredList;
    }

    private TableTree getTree() {
        return (TableTree) get(ID_TREE_CONTAINER + ":" + ID_TREE);
    }

    private TablePanel getTable(){
        return (TablePanel) get(ID_FORM + ":" + ID_TABLE);
    }

    private OrgType getRootOrgUnitFromProvider() {
        TableTree<OrgType, String> tree = getTree();
        ITreeProvider<OrgType> provider = tree.getProvider();
        Iterator<? extends OrgType> iterator = provider.getRoots();

        return iterator.hasNext() ? iterator.next() : null;
    }

    private void selectTreeItemPerformed(AjaxRequestTarget target) {
        TablePanel table = getTable();
        table.setCurrentPage(0);

        target.add(table);
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
