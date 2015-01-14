package com.esuta.fidm.gui.page.roles;

import com.esuta.fidm.gui.component.data.ObjectDataProvider;
import com.esuta.fidm.gui.component.data.column.EditDeleteButtonColumn;
import com.esuta.fidm.gui.component.data.table.TablePanel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.GeneralException;
import com.esuta.fidm.repository.schema.RoleType;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 * */
public class PageRoleList extends PageBase {

    private transient Logger LOGGER = Logger.getLogger(PageRoleList.class);

    private static final String ID_MAIN_FORM = "mainForm";
    private static final String ID_TABLE = "table";

    public PageRoleList(){
        initLayout();
    }

    private void initLayout(){
        Form form = new Form(ID_MAIN_FORM);
        form.setOutputMarkupId(true);
        add(form);

        List<IColumn> columns = createColumns();
        ObjectDataProvider<RoleType> provider = new ObjectDataProvider<>(getPage(), RoleType.class);

        TablePanel table = new TablePanel(ID_TABLE, provider, columns, 10);
        table.setOutputMarkupId(true);
        form.add(table);
    }

    private List<IColumn> createColumns(){
        List<IColumn> columns = new ArrayList<>();

        columns.add(new PropertyColumn<RoleType, String>(new Model<>("Name"), "name", "name"));
        columns.add(new PropertyColumn<RoleType, String>(new Model<>("DisplayName"), "displayName", "displayName"));
        columns.add(new PropertyColumn<RoleType, String>(new Model<>("Type"), "roleType", "roleType"));

        columns.add(new EditDeleteButtonColumn<RoleType>(new Model<>("Actions")){

            @Override
            public void editPerformed(AjaxRequestTarget target, IModel<RoleType> rowModel) {
                PageRoleList.this.editPerformed(target, rowModel);
            }

            @Override
            public void removePerformed(AjaxRequestTarget target, IModel<RoleType> rowModel) {
                PageRoleList.this.removePerformed(target, rowModel);
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

    private void editPerformed(AjaxRequestTarget target, IModel<RoleType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            error("Couldn't edit selected role. It is no longer available.");
            target.add(getFeedbackPanel());
            return;
        }

        PageParameters parameters = new PageParameters();
        parameters.add(UID_PAGE_PARAMETER_NAME, rowModel.getObject().getUid());
        setResponsePage(new PageRole(parameters));
    }

    private void removePerformed(AjaxRequestTarget target, IModel<RoleType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            error("Object selected to delete does not exist.");
            target.add(getFeedbackPanel());
            return;
        }

        RoleType role = rowModel.getObject();
        String roleName = role.getName();

        try {
            getModelService().deleteObject(role);
        } catch (GeneralException e){
            LOGGER.error("Could not delete role: '" + roleName + "'. Reason: ", e);
            error("Could not delete role: '" + roleName + "'. Reason: " + e.getExceptionMessage());
            target.add(getFeedbackPanel());
            return;
        }

        LOGGER.info("Role '" + roleName + "' was successfully deleted from the system.");
        success("Role '" + roleName + "' was successfully deleted from the system.");
        target.add(getFeedbackPanel(), getMainForm());
    }
}
