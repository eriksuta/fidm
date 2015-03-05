package com.esuta.fidm.gui.page.federation;

import com.esuta.fidm.gui.component.data.ObjectDataProvider;
import com.esuta.fidm.gui.component.data.column.EditDeleteButtonColumn;
import com.esuta.fidm.gui.component.data.column.LinkColumn;
import com.esuta.fidm.gui.component.data.table.TablePanel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.ObjectNotFoundException;
import com.esuta.fidm.model.federation.client.SimpleRestResponse;
import com.esuta.fidm.repository.schema.core.FederationMemberType;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.eclipse.jetty.http.HttpStatus;

import java.io.IOException;
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

        columns.add(new LinkColumn<FederationMemberType>(new Model<>("Name"), "name", "name"){

            @Override
            public void onClick(AjaxRequestTarget target, IModel<FederationMemberType> rowModel) {
                editPerformed(target, rowModel);
            }
        });

        columns.add(new PropertyColumn<FederationMemberType, String>(new Model<>("Federation Identifier"), "federationMemberName", "federationMemberName"));
        columns.add(new PropertyColumn<FederationMemberType, String>(new Model<>("DisplayName"), "displayName", "displayName"));
        columns.add(new PropertyColumn<FederationMemberType, String>(new Model<>("Address"), "port", "port"));
        columns.add(new PropertyColumn<FederationMemberType, String>(new Model<>("Port"), "webAddress", "webAddress"));
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

        //Can't request deletion, when another deletion request is already in progress
        if(FederationMemberType.FederationMemberStatusType.DELETE_REQUESTED.equals(federationMember.getStatus())){
            warn("Can't request deletion, when another deletion request is already in progress");
            target.add(getFeedbackPanel(), getMainForm());
            return;
        }

        try {
            SimpleRestResponse response = getFederationServiceClient().createFederationDeletionRequest(federationMember);

            if(HttpStatus.OK_200 == response.getStatus()){
                federationMember.setStatus(FederationMemberType.FederationMemberStatusType.DELETE_REQUESTED);
                getModelService().updateObject(federationMember);
                LOGGER.info("Federation deletion request accepted. Member: '" + federationMember.getFederationMemberName()
                        + "'(" + federationMember.getUid() + ").");
                info("Federation deletion request accepted. Member: '" + federationMember.getFederationMemberName()
                        + "'(" + federationMember.getUid() + ").");
            } else {
                LOGGER.error("Federation membership deletion request not successful. Reason: " + response.getMessage());
                getSession().error("Federation membership deletion request not successful. Reason: " + response.getMessage());
            }

        } catch (IOException e) {
            LOGGER.error("Could not create a REST request to federation member deletion. Federation member: '"
                    + federationMember.getFederationMemberName() + "'. Reason: ", e);

            getSession().error("Could not create a REST request to federation member deletion. Federation member: '"
                    + federationMember.getFederationMemberName() + "'. Reason: " + e);
        } catch (ObjectNotFoundException | DatabaseCommunicationException e) {
            LOGGER.error("Could not update federation member: '" + federationMember.getFederationMemberName()
                    + "'(" + federationMember.getUid() + ").", e);
            getSession().error("Could not update federation member: '" + federationMember.getFederationMemberName()
                    + "'(" + federationMember.getUid() + ")." + e);
        }

        target.add(getFeedbackPanel(), getMainForm());
    }
}
