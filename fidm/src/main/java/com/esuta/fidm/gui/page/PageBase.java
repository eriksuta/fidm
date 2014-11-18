package com.esuta.fidm.gui.page;

import com.esuta.fidm.gui.component.nav.RightNavigationMenu;
import com.esuta.fidm.model.ModelService;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *  @author shood
 * */
public abstract class PageBase extends WebPage{

    //Every page is able to communicate with model through this ModelService instance
    private ModelService modelService;

    private static final String ID_TITLE = "title";
    private static final String ID_SUBTITLE = "subtitle";
    private static final String ID_RIGHT_MENU_PANEL = "rightMenuPanel";

    public PageBase(){
        this(null);
    }

    public PageBase(PageParameters parameters){
        super(parameters);

        initLayout();
    }

    private void initLayout(){
        Label title = new Label(ID_TITLE, createPageTitleModel());
        add(title);

        Label subtitle = new Label(ID_SUBTITLE, createPageSubtitleModel());
        add(subtitle);

        RightNavigationMenu rightMenu = new RightNavigationMenu(ID_RIGHT_MENU_PANEL, this.getClass());
        add(rightMenu);

    }

    protected IModel<String> createPageTitleModel(){
        return createStringResource("page.title");
    }

    protected IModel<String> createPageSubtitleModel(){
        return createStringResource("page.subtitle");
    }

    public StringResourceModel createStringResource(String resourceKey, Object... objects){
        return new StringResourceModel(resourceKey, this, new Model<String>(), resourceKey, objects);
    }

    public ModelService getModelService(){
        if(modelService == null){
            modelService = ModelService.getInstance();
        }

        return modelService;
    }



}
