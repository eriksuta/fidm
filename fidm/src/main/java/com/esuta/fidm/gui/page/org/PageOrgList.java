package com.esuta.fidm.gui.page.org;

import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.gui.page.dashboard.PageDashboard;
import com.esuta.fidm.gui.page.org.component.OrgUnitTreePanel;
import com.esuta.fidm.gui.page.org.component.TabbedPanel;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.repository.schema.OrgType;
import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 *
 *  based on implementation by lazyman
 *  (https://github.com/Evolveum/midpoint/blob/482d29f2d42bd99dc3ee816e61c19510c6bb266a/gui/admin-gui/src/main/java/com/evolveum/midpoint/web/page/admin/users/PageOrgTree.java)
 * */
public class PageOrgList extends PageBase {

    private static final Logger LOGGER = Logger.getLogger(PageOrgList.class);

    private static final String ID_ORG_UNIT_TAB_PANEL = "orgUnitTabPanel";

    public PageOrgList(){
        initLayout();
    }

    private void initLayout(){
        final IModel<List<ITab>> tabModel = new LoadableModel<List<ITab>>(false) {

            @Override
            protected List<ITab> load() {
                List<OrgType> roots = loadOrgRoots();
                List<ITab> tabs = new ArrayList<>();

                for(OrgType root: roots){
                    final String uid = root.getUid();

                    tabs.add(new AbstractTab(createTabTitle(root)) {

                        @Override
                        public WebMarkupContainer getPanel(String panelId) {
                            return new OrgUnitTreePanel(panelId, new Model<>(uid));
                        }
                    });
                }

                if(tabs.isEmpty()){
                    getSession().warn("There is currently no org. unit structure defined.");
                    throw new RestartResponseException(PageDashboard.class);
                }

                return tabs;
            }
        };

        TabbedPanel tabbedPanel = new TabbedPanel<>(ID_ORG_UNIT_TAB_PANEL, tabModel, new Model<>(0));
        tabbedPanel.setOutputMarkupId(true);
        add(tabbedPanel);
    }

    private IModel<String> createTabTitle(final OrgType root){
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                if(root == null){
                    return "Name Not Defined";
                }

                return root.getDisplayName();
            }
        };
    }

    private List<OrgType> loadOrgRoots(){
        List<OrgType> roots = new ArrayList<>();

        try {
            List<OrgType> allOrgUnits = getModelService().getAllObjectsOfType(OrgType.class);

            for(OrgType org: allOrgUnits){
                if(org.getParentOrgUnits().isEmpty()){
                    roots.add(org);
                }
            }

            LOGGER.debug("There are currently " + roots.size() + " root in org. unit hierarchy.");
        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Couldn't retrieve the list of Org. units from the repository.");
            error("Couldn't retrieve the list of Org. units from the repository.");
        }

        return roots;
    }
}
