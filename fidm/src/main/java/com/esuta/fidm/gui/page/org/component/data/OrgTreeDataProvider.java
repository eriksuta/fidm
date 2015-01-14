package com.esuta.fidm.gui.page.org.component.data;

import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.repository.schema.OrgType;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableTreeProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *  @author shood
 *
 *  implementation based on OrgTreeProvider by lazyman,
 *  (https://github.com/Evolveum/midpoint/blob/a6c023945dbea34db69a8ff17c9a61b7184c42cc/gui/admin-gui/src/main/java/com/evolveum/midpoint/web/page/admin/users/component/OrgTreeProvider.java)
 * */
public class OrgTreeDataProvider extends SortableTreeProvider<OrgType, String>{

    private static final Logger LOGGER = Logger.getLogger(OrgTreeDataProvider.class);

    private Component component;
    private IModel<String> rootUidModel;
    private OrgType root;

    public OrgTreeDataProvider(Component component, IModel<String> rootUidModel){
        this.component = component;
        this.rootUidModel = rootUidModel;
    }

    private PageBase getPage() {
        return (PageBase) component.getPage();
    }

    private ModelService getModelService() {
        PageBase page = (PageBase) component.getPage();
        return page.getModelService();
    }

    @Override
    public Iterator<? extends OrgType> getRoots() {
        List<OrgType> roots = new ArrayList<>();

        if(root == null){
            try {
                OrgType root = getModelService().readObject(OrgType.class, rootUidModel.getObject());
                roots.add(root);
            } catch (DatabaseCommunicationException e) {
                LOGGER.error("Couldn't retrieve root org. units from the repository.");
            }
        }

        return roots.iterator();
    }

    @Override
    public boolean hasChildren(OrgType node) {
        return true;
    }

    @Override
    public Iterator<? extends OrgType> getChildren(OrgType node) {
        List<OrgType> children = new ArrayList<>();
        String nodeUid = node.getUid();

        try {
            List<OrgType> allOrgUnits = getModelService().getAllObjectsOfType(OrgType.class);

            for(OrgType org: allOrgUnits){
                if(org.getParentOrgUnits().contains(nodeUid)){
                    children.add(org);
                }
            }

            LOGGER.debug("Found " + children.size() + " children org. units for org. unit with uid: '" + nodeUid + "'.");
        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Couldn't retrieve children org. units for org. unit with oid: '" + nodeUid + "' from the repository");
        }


        return children.iterator();
    }

    @Override
    public IModel<OrgType> model(OrgType object) {
        return new Model<>(object);
    }
}
