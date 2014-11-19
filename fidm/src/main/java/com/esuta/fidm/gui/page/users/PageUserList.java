package com.esuta.fidm.gui.page.users;

import com.esuta.fidm.gui.component.data.ObjectDataProvider;
import com.esuta.fidm.gui.component.data.column.EditDeleteButtonColumn;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.GeneralException;
import com.esuta.fidm.repository.schema.UserType;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 * */
public class PageUserList extends PageBase {

    private transient Logger LOGGER = Logger.getLogger(PageUserList.class);

    private static final String ID_MAIN_FORM = "mainForm";
    private static final String ID_TABLE = "table";

    public PageUserList(){
        initLayout();
    }

    private void initLayout(){
        Form form = new Form(ID_MAIN_FORM);
        form.setOutputMarkupId(true);
        add(form);

        List<IColumn> columns = createColumns();
        ObjectDataProvider<UserType> provider = new ObjectDataProvider<UserType>(getPage(), UserType.class);

        DataTable table = new AjaxFallbackDefaultDataTable(ID_TABLE, columns, provider, 10);
        table.setOutputMarkupId(true);
        form.add(table);
    }

    private List<IColumn> createColumns(){
        List<IColumn> columns = new ArrayList<IColumn>();

        columns.add(new PropertyColumn<UserType, String>(new Model<String>("Name"), "name", "name"));
        columns.add(new PropertyColumn<UserType, String>(new Model<String>("Given Name"), "givenName", "givenName"));
        columns.add(new PropertyColumn<UserType, String>(new Model<String>("Family Name"), "familyName", "familyName"));
        columns.add(new PropertyColumn<UserType, String>(new Model<String>("E-mail"), "emailAddress", "emailAddress"));
        columns.add(new PropertyColumn<UserType, String>(new Model<String>("Locality"), "locality", "locality"));

        columns.add(new EditDeleteButtonColumn<UserType>(new Model<String>("Actions")){

            @Override
            public void editPerformed(AjaxRequestTarget target, IModel<UserType> rowModel) {
                PageUserList.this.editPerformed(target, rowModel);
            }

            @Override
            public void removePerformed(AjaxRequestTarget target, IModel<UserType> rowModel) {
                PageUserList.this.removePerformed(target, rowModel);
            }
        });

        return columns;
    }

    private DataTable getDataTable(){
        return (DataTable) get(ID_MAIN_FORM + ":" + ID_TABLE);
    }

    private void editPerformed(AjaxRequestTarget target, IModel<UserType> rowModel){
        //TODO
    }

    private void removePerformed(AjaxRequestTarget target, IModel<UserType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            error("Object selected to delete does not exist.");
            target.add(getFeedbackPanel());
            return;
        }

        UserType user = rowModel.getObject();
        String userName = user.getName();

        try {
            getModelService().deleteObject(user);
        } catch (GeneralException e){
            LOGGER.error("Could not delete user: '" + userName + "'. Reason: ", e);
            error("Could not delete user: '" + userName + "'. Reason: " + e.getExceptionMessage());
            target.add(getFeedbackPanel());
            return;
        }

        success("User '" + userName + "' was successfully deleted from the system.");
        target.add(getFeedbackPanel(), getDataTable());
    }
}
