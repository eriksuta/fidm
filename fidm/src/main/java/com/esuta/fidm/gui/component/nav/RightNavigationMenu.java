package com.esuta.fidm.gui.component.nav;

import com.esuta.fidm.gui.component.behavior.VisibleEnableBehavior;
import com.esuta.fidm.gui.page.dashboard.PageDashboard;
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
        List<NavigationMenuTopLevelItem> menuItemList = new ArrayList<NavigationMenuTopLevelItem>();

        //init Dashboard
        menuItemList.add(new NavigationMenuTopLevelItem(createStringResource("page.link.dashboard"), "fa fa-dashboard fa-fw", PageDashboard.class));

        //init User section
        NavigationMenuTopLevelItem userSection = new NavigationMenuTopLevelItem(createStringResource("page.link.user"), "fa fa-users fa-fw", null);
        userSection.addSecondLevelItem(new NavigationMenuSecondLevelItem(createStringResource("page.link.user.new"), PageUser.class));
        userSection.addSecondLevelItem(new NavigationMenuSecondLevelItem(createStringResource("page.link.user.list"), PageUserList.class));
        menuItemList.add(userSection);

        //TODO - init other sections


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
