package com.esuta.fidm.gui.component.nav;

import com.esuta.fidm.gui.component.behavior.VisibleEnableBehavior;
import com.esuta.fidm.gui.page.config.PageAbout;
import com.esuta.fidm.gui.page.config.PageDebugList;
import com.esuta.fidm.gui.page.config.PageSystemConfiguration;
import com.esuta.fidm.gui.page.dashboard.PageDashboard;
import com.esuta.fidm.gui.page.federation.PageFederation;
import com.esuta.fidm.gui.page.federation.PageFederationList;
import com.esuta.fidm.gui.page.org.PageOrg;
import com.esuta.fidm.gui.page.org.PageOrgList;
import com.esuta.fidm.gui.page.resource.PageResource;
import com.esuta.fidm.gui.page.resource.PageResourceList;
import com.esuta.fidm.gui.page.roles.PageRole;
import com.esuta.fidm.gui.page.roles.PageRoleList;
import com.esuta.fidm.gui.page.users.PageAccount;
import com.esuta.fidm.gui.page.users.PageUser;
import com.esuta.fidm.gui.page.users.PageUserList;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 * */
public class RightNavigationMenu extends Panel {

    private static final String ID_NAV_ITEM = "navigationItem";
    private static final String ID_TOP_NAV_ITEM = "topLevelNavigationItem";
    private static final String ID_ICON = "icon";
    private static final String ID_LABEL = "label";
    private static final String ID_ARROW = "arrow";
    private static final String ID_SECOND_LEVEL_CONTAINER = "secondLevelContainer";
    private static final String ID_SECOND_NAV_ITEM = "secondLevelNavigationItem";
    private static final String ID_SECOND_NAV_ITEM_LINK = "secondLevelNavigationItemLink";
    private static final String ID_SECOND_NAV_ITEM_LABEL = "secondLevelNavigationItemLabel";

    private Class<? extends WebPage> clazz;

    public RightNavigationMenu(String id, Class<? extends WebPage> clazz){
        super(id);

        this.clazz = clazz;
        initLayout(createMenuItemList());
    }

    public List<NavigationMenuTopLevelItem> createMenuItemList(){
        List<NavigationMenuTopLevelItem> menuItemList = new ArrayList<>();

        //init Dashboard
        menuItemList.add(new NavigationMenuTopLevelItem(createStringResource("page.link.dashboard"), "fa fa-dashboard fa-fw", PageDashboard.class));

        //init User section
        NavigationMenuTopLevelItem userSection = new NavigationMenuTopLevelItem(createStringResource("page.link.user"), "fa fa-users fa-fw", null);
        userSection.addSecondLevelItem(new NavigationMenuSecondLevelItem(createStringResource("page.link.user.new"), PageUser.class));
        userSection.addSecondLevelItem(new NavigationMenuSecondLevelItem(createStringResource("page.link.user.list"), PageUserList.class));
        userSection.addSecondLevelItem(new NavigationMenuSecondLevelItem(createStringResource("page.link.user.account"), PageAccount.class));
        menuItemList.add(userSection);

        //init Resource section
        NavigationMenuTopLevelItem resourceSection = new NavigationMenuTopLevelItem(createStringResource("page.link.resource"), "fa fa-database fa-fw", null);
        resourceSection.addSecondLevelItem(new NavigationMenuSecondLevelItem(createStringResource("page.link.resource.new"), PageResource.class));
        resourceSection.addSecondLevelItem(new NavigationMenuSecondLevelItem(createStringResource("page.link.resource.list"), PageResourceList.class));
        menuItemList.add(resourceSection);

        //init Roles section
        NavigationMenuTopLevelItem roleSection = new NavigationMenuTopLevelItem(createStringResource("page.link.role"), "fa fa-suitcase fa-fw", null);
        roleSection.addSecondLevelItem(new NavigationMenuSecondLevelItem(createStringResource("page.link.role.new"), PageRole.class));
        roleSection.addSecondLevelItem(new NavigationMenuSecondLevelItem(createStringResource("page.link.role.list"), PageRoleList.class));
        menuItemList.add(roleSection);

        //init Org section
        NavigationMenuTopLevelItem orgSection = new NavigationMenuTopLevelItem(createStringResource("page.link.org"), "fa fa-sitemap fa-fw", null);
        orgSection.addSecondLevelItem(new NavigationMenuSecondLevelItem(createStringResource("page.link.org.new"), PageOrg.class));
        orgSection.addSecondLevelItem(new NavigationMenuSecondLevelItem(createStringResource("page.link.org.list"), PageOrgList.class));
        menuItemList.add(orgSection);

        //init Federation section
        NavigationMenuTopLevelItem federationSection = new NavigationMenuTopLevelItem(createStringResource("page.link.federation"), "fa fa-institution fa-fw", null);
        federationSection.addSecondLevelItem(new NavigationMenuSecondLevelItem(createStringResource("page.link.federation.new"), PageFederation.class));
        federationSection.addSecondLevelItem(new NavigationMenuSecondLevelItem(createStringResource("page.link.federation.list"), PageFederationList.class));
        menuItemList.add(federationSection);

        //init Configuration section
        NavigationMenuTopLevelItem configSection = new NavigationMenuTopLevelItem(createStringResource("page.link.config"), "fa fa-wrench fa-fw", null);
        configSection.addSecondLevelItem(new NavigationMenuSecondLevelItem(createStringResource("page.link.config.basic"), PageSystemConfiguration.class));
        configSection.addSecondLevelItem(new NavigationMenuSecondLevelItem(createStringResource("page.link.config.debug"), PageDebugList.class));
        configSection.addSecondLevelItem(new NavigationMenuSecondLevelItem(createStringResource("page.link.config.about"), PageAbout.class));
        menuItemList.add(configSection);

        return menuItemList;
    }

    private void initLayout(List<NavigationMenuTopLevelItem> menuItems){
        ListView<NavigationMenuTopLevelItem> rightMenuBar = new ListView<NavigationMenuTopLevelItem>(ID_NAV_ITEM, menuItems) {

            @Override
            protected void populateItem(ListItem<NavigationMenuTopLevelItem> item) {
                initNavigationMenuItem(item);
            }
        };
        add(rightMenuBar);

        //TODO - init right user panel
    }

    private void initNavigationMenuItem(ListItem<NavigationMenuTopLevelItem> item){
        final NavigationMenuTopLevelItem topItem = item.getModelObject();

        if(isMenuSubItemActive(topItem)){
            item.add(new AttributeAppender("class", "active"));
        }

        WebMarkupContainer topItemLink;
        if(topItem.getPage() != null){
            topItemLink = new BookmarkablePageLink(ID_TOP_NAV_ITEM, topItem.getPage());

            if(topItem.getPage().equals(clazz)){
                topItemLink.add(new AttributeAppender("class", "active"));
            }
        } else {
            topItemLink = new WebMarkupContainer(ID_TOP_NAV_ITEM);
        }
        item.add(topItemLink);

        Label icon = new Label(ID_ICON);
        icon.add(new AttributeAppender("class", topItem.getIconClass()));
        topItemLink.add(icon);

        Label label = new Label(ID_LABEL, topItem.getName());
        topItemLink.add(label);

        Label arrow = new Label(ID_ARROW);
        arrow.add(new VisibleEnableBehavior(){

            @Override
            public boolean isVisible() {
                return !topItem.getItems().isEmpty();
            }
        });
        topItemLink.add(arrow);

        WebMarkupContainer secondLevelContainer = new WebMarkupContainer(ID_SECOND_LEVEL_CONTAINER);
        secondLevelContainer.add(new VisibleEnableBehavior(){

            @Override
            public boolean isVisible() {
                return !topItem.getItems().isEmpty();
            }
        });
        item.add(secondLevelContainer);

        secondLevelContainer.add(new ListView<NavigationMenuSecondLevelItem>(ID_SECOND_NAV_ITEM, topItem.getItems()) {

            @Override
            protected void populateItem(ListItem<NavigationMenuSecondLevelItem> secondLevelItem) {
                initSecondLevelMenuItem(secondLevelItem);
            }
        });
    }

    private void initSecondLevelMenuItem(ListItem<NavigationMenuSecondLevelItem> item){
        NavigationMenuSecondLevelItem secondLevelItem = item.getModelObject();

        BookmarkablePageLink link = new BookmarkablePageLink(ID_SECOND_NAV_ITEM_LINK, secondLevelItem.getPage());

        if(secondLevelItem.getPage().equals(clazz)){
            link.add(new AttributeAppender("class", "active"));
        }

        item.add(link);

        link.add(new Label(ID_SECOND_NAV_ITEM_LABEL, secondLevelItem.getName()));
    }

    private boolean isMenuSubItemActive(NavigationMenuTopLevelItem item){
        List<NavigationMenuSecondLevelItem> list = item.getItems();

        if(list.isEmpty()){
            return false;
        }

        for(NavigationMenuSecondLevelItem secondLevelItem: list){
            if(secondLevelItem.getPage().equals(clazz)){
                return true;
            }
        }

        return false;
    }

    public StringResourceModel createStringResource(String resourceKey, Object... objects){
        return new StringResourceModel(resourceKey, this, new Model<String>(), resourceKey, objects);
    }

}
