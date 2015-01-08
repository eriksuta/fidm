package com.esuta.fidm.gui.component.modal;

import com.esuta.fidm.gui.component.data.ObjectDataProvider;
import com.esuta.fidm.gui.component.data.column.EditDeleteButtonColumn;
import com.esuta.fidm.gui.component.data.table.TablePanel;
import com.esuta.fidm.repository.schema.ObjectType;
import com.esuta.fidm.repository.schema.RoleType;
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
public class AssignablePopupDialog<T extends ObjectType> extends ModalWindow{

    private static final String ID_TABLE = "table";
    private static final String ID_CANCEL = "cancelButton";

    private boolean initialized;
    private Class<T> type;

    public AssignablePopupDialog(String id, Class<T> type){
        super(id);

        this.type = type;
        setTitle("Assign Role/Org. Unit");
        showUnloadConfirmation(false);
        setCssClassName(ModalWindow.CSS_CLASS_BLUE);
        setCookieName(AssignablePopupDialog.class.getSimpleName() + ((int) (Math.random() * 100)));
        setResizable(true);
        setInitialWidth(500);
        setInitialHeight(500);
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

        ObjectDataProvider<T> provider = new ObjectDataProvider<>(this, type);

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

        if(RoleType.class.equals(type)){
            columns.add(new PropertyColumn<T, String>(new Model<>("Display Name"), "displayName"));
            columns.add(new PropertyColumn<T, String>(new Model<>("Type"), "roleType"));
        }

        columns.add(new EditDeleteButtonColumn<T>(new Model<>("Actions")){

            @Override
            public void editPerformed(AjaxRequestTarget target, IModel<T> rowModel) {
                AssignablePopupDialog.this.addPerformed(target, rowModel);
            }

            @Override
            public boolean getRemoveVisible() {
                return false;
            }
        });

        return columns;
    }

    public void cancelPerformed(AjaxRequestTarget target){
        close(target);
    }

    public void addPerformed(AjaxRequestTarget target, IModel<T> rowModel){
        //Override me
    }
}
