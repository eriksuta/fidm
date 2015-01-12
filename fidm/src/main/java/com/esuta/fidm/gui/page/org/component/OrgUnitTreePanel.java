package com.esuta.fidm.gui.page.org.component;

import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.repository.schema.OrgType;
import org.apache.log4j.Logger;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.TableTree;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.Serializable;
import java.util.Iterator;

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

//        InlineMenu treeMenu = new InlineMenu(ID_TREE_MENU, new Model((Serializable) createTreeMenu()));
//        treeHeader.add(treeMenu);

        //TODO - continue here
    }










    private TableTree getTree() {
        return (TableTree) get(ID_TREE_CONTAINER + ":" + ID_TREE);
    }

    private OrgType getRootOrgUnitFromProvider() {
        TableTree<OrgType, String> tree = getTree();
        ITreeProvider<OrgType> provider = tree.getProvider();
        Iterator<? extends OrgType> iterator = provider.getRoots();

        return iterator.hasNext() ? iterator.next() : null;
    }
}
