package com.esuta.fidm.gui.page.federation;

import com.esuta.fidm.gui.component.data.ObjectDataProvider;
import com.esuta.fidm.gui.component.data.column.EditDeleteButtonColumn;
import com.esuta.fidm.gui.component.data.table.TablePanel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.GeneralException;
import com.esuta.fidm.repository.schema.FederationMemberType;
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
public class PageFederationList extends PageBase{

    private transient Logger LOGGER = Logger.getLogger(PageFederationList.class);

    private static final String ID_MAIN_FORM = "mainForm";
    private static final String ID_TABLE = "table";

    public PageFederationList() {
        initLayout();
    }

    private void initLayout(){
        Form form = new Form(ID_MAIN_FORM);
        form.setOutputMarkupId(true);
        add(form);

        List<IColumn> columns = createColumns();
        ObjectDataProvider<FederationMemberType> provider = new ObjectDataProvider<>(getPage(), FederationMemberType.class);

        TablePanel table = new TablePanel(ID_TABLE, provider, columns, 10);
        table.setOutputMarkupId(true);
        form.add(table);
    }

    private List<IColumn> createColumns(){
        List<IColumn> columns = new ArrayList<>();

        columns.add(new PropertyColumn<FederationMemberType, String>(new Model<>("Name"), "name", "name"));
        columns.add(new PropertyColumn<FederationMemberType, String>(new Model<>("DisplayName"), "displayName", "displayName"));
        columns.add(new PropertyColumn<FederationMemberType, String>(new Model<>("Address"), "webAddress", "webAddress"));
        columns.add(new PropertyColumn<FederationMemberType, String>(new Model<>("Locality"), "locality", "locality"));
        columns.add(new PropertyColumn<FederationMemberType, String>(new Model<>("Status"), "status"));

        columns.add(new EditDeleteButtonColumn<FederationMemberType>(new Model<>("Actions")){

            @Override
            public void editPerformed(AjaxRequestTarget target, IModel<FederationMemberType> rowModel) {
                PageFederationList.this.editPerformed(target, rowModel);
            }

            @Override
            public void removePerformed(AjaxRequestTarget target, IModel<FederationMemberType> rowModel) {
                PageFederationList.this.removePerformed(target, rowModel);
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

    private void editPerformed(AjaxRequestTarget target, IModel<FederationMemberType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            error("Couldn't edit selected federation member. It is no longer available.");
            target.add(getFeedbackPanel());
            return;
        }

        PageParameters parameters = new PageParameters();
        parameters.add(UID_PAGE_PARAMETER_NAME, rowModel.getObject().getUid());
        setResponsePage(new PageFederation(parameters));
    }

    private void removePerformed(AjaxRequestTarget target, IModel<FederationMemberType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            error("Object selected to delete does not exist.");
            target.add(getFeedbackPanel());
            return;
        }

        FederationMemberType federationMember = rowModel.getObject();
        String federationMemberName = federationMember.getName();

        try {
            getModelService().deleteObject(federationMember);
        } catch (GeneralException e){
            LOGGER.error("Could not delete federation member: '" + federationMemberName + "'. Reason: ", e);
            error("Could not delete federation member: '" + federationMemberName + "'. Reason: " + e.getExceptionMessage());
            target.add(getFeedbackPanel());
            return;
        }

        LOGGER.info("Federation Member '" + federationMemberName + "' was successfully deleted from the system.");
        success("Federation Member '" + federationMemberName + "' was successfully deleted from the system.");
        target.add(getFeedbackPanel(), getMainForm());
    }
}
