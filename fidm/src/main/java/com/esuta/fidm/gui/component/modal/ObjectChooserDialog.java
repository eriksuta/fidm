package com.esuta.fidm.gui.component.modal;

import com.esuta.fidm.gui.component.data.ObjectDataProvider;
import com.esuta.fidm.gui.component.data.column.EditDeleteButtonColumn;
import com.esuta.fidm.gui.component.data.table.TablePanel;
import com.esuta.fidm.repository.schema.ObjectType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 * */
public class ObjectChooserDialog<T extends ObjectType> extends ModalWindow{

    private static final String ID_TABLE = "table";
    private static final String ID_CANCEL = "cancelButton";

    private boolean initialized;
    private Class<T> type;

    public ObjectChooserDialog(String id, Class<T> type){
        super(id);

        this.type = type;
        setTitle(getChooserTitle());
        showUnloadConfirmation(false);
        setCssClassName(ModalWindow.CSS_CLASS_BLUE);
        setCookieName(ObjectChooserDialog.class.getSimpleName() + ((int) (Math.random() * 100)));
        setResizable(getResizable());
        setInitialWidth(getInitialWidth());
        setInitialHeight(getInitialHeight());
        setWidthUnit("px");

        WebMarkupContainer content = new WebMarkupContainer(getContentId());
        content.setOutputMarkupId(true);
        setContent(content);
    }

    @Override
    protected void onBeforeRender(){
        super.onBeforeRender();

        if(initialized){
            return;
        }

        initLayout((WebMarkupContainer) get(getContentId()));
        initialized = true;
    }

    private void initLayout(WebMarkupContainer content){
        List<IColumn<T, String>> columns = initColumns();

        ObjectDataProvider<T> provider = new ObjectDataProvider<T>(this, type){

            @Override
            public List<T> applyDataFilter(List<T> list) {
                return ObjectChooserDialog.this.applyObjectFilter(list);
            }
        };

        TablePanel table = new TablePanel(ID_TABLE, provider, columns, 10);
        table.setOutputMarkupId(true);
        content.add(table);

        AjaxLink cancelButton = new AjaxLink<String>(ID_CANCEL) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                cancelPerformed(target);
            }
        };
        content.add(cancelButton);
    }

    private List<IColumn<T, String>> initColumns(){
        List<IColumn<T, String>> columns = new ArrayList<>();

        columns.add(new PropertyColumn<T, String>(new Model<>("Name"), "name"));

        List<IColumn<T, String>> customColumns = createCustomColumns();
        if(customColumns != null){
            columns.addAll(customColumns);
        }

        columns.add(new EditDeleteButtonColumn<T>(new Model<>("Actions")){

            @Override
            public void editPerformed(AjaxRequestTarget target, IModel<T> rowModel) {
                ObjectChooserDialog.this.objectChoosePerformed(target, rowModel);
            }

            @Override
            public boolean getRemoveVisible() {
                return false;
            }
        });

        return columns;
    }

    /**
     *  Override to create a filter that would filter retrieved data
     * */
    public List<T> applyObjectFilter(List<T> list){
        return list;
    }

    /**
     *  Override to enhance the table in chooser with custom columns
     * */
    protected List<IColumn<T, String>> createCustomColumns(){
        return null;
    }

    /**
     *  Override to provide a reaction for cancel button
     * */
    public void cancelPerformed(AjaxRequestTarget target){
        close(target);
    }

    /**
     *  Override to provide a reaction for object choose event
     * */
    public void objectChoosePerformed(AjaxRequestTarget target, IModel<T> rowModel){}

    /**
     *  Override to provide modal window title
     * */
    public String getChooserTitle(){
        return "Choose Object";
    }

    /**
     *  Override to define if window should/should not be resizable
     * */
    public boolean getResizable(){
        return true;
    }

    /**
     *  Override to provide initial width of the modal window
     * */
    public int getInitialWidth(){
        return 500;
    }

    /**
     *  Override to provide initial height of the modal window
     * */
    public int getInitialHeight(){
        return 500;
    }
}
