package com.esuta.fidm.gui.component.nav;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 * */
public class NavigationMenuTopLevelItem implements Serializable {

    private IModel<String> name;
    private String iconClass;
    private Class<? extends WebPage> page;
    private List<NavigationMenuSecondLevelItem> items;

    public NavigationMenuTopLevelItem(IModel<String> name, String iconClass, Class<? extends WebPage> page){
        this.name = name;
        this.iconClass = iconClass;
        this.page = page;
    }

    public void addSecondLevelItem(NavigationMenuSecondLevelItem item){
        getItems().add(item);
    }

    public IModel<String> getName() {
        return name;
    }

    public void setName(IModel<String> name) {
        this.name = name;
    }

    public Class<? extends WebPage> getPage() {
        return page;
    }

    public void setPage(Class<? extends WebPage> page) {
        this.page = page;
    }

    public List<NavigationMenuSecondLevelItem> getItems() {
        if(items == null){
            items = new ArrayList<NavigationMenuSecondLevelItem>();
        }

        return items;
    }

    public void setItems(List<NavigationMenuSecondLevelItem> items) {
        this.items = items;
    }

    public String getIconClass() {
        return iconClass;
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }
}
