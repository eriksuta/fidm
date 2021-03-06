package com.esuta.fidm.gui.page.users;

import com.esuta.fidm.gui.component.data.ObjectDataProvider;
import com.esuta.fidm.gui.component.data.column.EditDeleteButtonColumn;
import com.esuta.fidm.gui.component.data.table.TablePanel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.GeneralException;
import com.esuta.fidm.repository.schema.core.UserType;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.*;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

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
        ObjectDataProvider<UserType> provider = new ObjectDataProvider<>(getPage(), UserType.class);

        TablePanel table = new TablePanel(ID_TABLE, provider, columns, 10);
        table.setOutputMarkupId(true);
        form.add(table);
    }

    private List<IColumn> createColumns(){
        List<IColumn> columns = new ArrayList<>();

        columns.add(new PropertyColumn<UserType, String>(new Model<>("Name"), "name", "name"));
        columns.add(new PropertyColumn<UserType, String>(new Model<>("Given Name"), "givenName", "givenName"));
        columns.add(new PropertyColumn<UserType, String>(new Model<>("Family Name"), "familyName", "familyName"));
        columns.add(new PropertyColumn<UserType, String>(new Model<>("E-mail"), "emailAddress", "emailAddress"));
        columns.add(new PropertyColumn<UserType, String>(new Model<>("Locality"), "locality", "locality"));
        columns.add(new AbstractColumn<UserType, String>(new Model<>("Origin")) {

            @Override
            public void populateItem(Item<ICellPopulator<UserType>> cellItem, String componentId, final IModel<UserType> rowModel) {
                cellItem.add(new Label(componentId, new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return rowModel.getObject().getFederationIdentifier() == null ? "Origin" : "Copy";
                    }
                }));
            }
        });

        columns.add(new EditDeleteButtonColumn<UserType>(new Model<>("Actions")){

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

    private TablePanel getTablePanel(){
        return (TablePanel) get(ID_MAIN_FORM + ":" + ID_TABLE);
    }

    private Form getMainForm(){
        return (Form) get(ID_MAIN_FORM);
    }

    private void editPerformed(AjaxRequestTarget target, IModel<UserType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            error("Couldn't edit selected user. It is no longer available.");
            target.add(getFeedbackPanel());
            return;
        }

        PageParameters parameters = new PageParameters();
        parameters.add(UID_PAGE_PARAMETER_NAME, rowModel.getObject().getUid());
        setResponsePage(new PageUser(parameters));
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

        LOGGER.info("User '" + userName + "' was successfully deleted from the system.");
        success("User '" + userName + "' was successfully deleted from the system.");
        target.add(getFeedbackPanel(), getMainForm());
    }
}
