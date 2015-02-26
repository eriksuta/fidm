package com.esuta.fidm.gui.page.config;

import com.esuta.fidm.gui.component.data.ObjectDataProvider;
import com.esuta.fidm.gui.component.data.column.EditDeleteButtonColumn;
import com.esuta.fidm.gui.component.data.table.TablePanel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.GeneralException;
import com.esuta.fidm.repository.schema.core.*;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 * */
public class PageDebugList extends PageBase{

    private transient Logger LOGGER = Logger.getLogger(PageDebugList.class);

    private static final String ID_OBJECT_TYPE_CHOOSER = "objectTypeChoice";
    private static final String ID_MAIN_FORM = "mainForm";
    private static final String ID_OBJECT_TABLE = "objectTable";

    private Class<? extends ObjectType> objectType = AccountType.class;

    public PageDebugList(){
        initLayout();
    }

    private void initLayout(){
        DropDownChoice objectTypeChooser = new DropDownChoice<>(ID_OBJECT_TYPE_CHOOSER,
                new PropertyModel<Class<? extends ObjectType>>(this, "objectType"), createObjectTypeList(),
                new IChoiceRenderer<Class<? extends ObjectType>>() {
            @Override
            public Object getDisplayValue(Class<? extends ObjectType> object) {
                return object.getSimpleName();
            }

            @Override
            public String getIdValue(Class<? extends ObjectType> object, int index) {
                return Integer.toString(index);
            }
        });
        objectTypeChooser.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                updateObjectTablePerformed(target);
            }
        });
        add(objectTypeChooser);

        Form form = new Form(ID_MAIN_FORM);
        form.setOutputMarkupId(true);
        add(form);

        List<IColumn> columns = createColumns();
        ObjectDataProvider objectProvider = new ObjectDataProvider<>(getPage(), objectType);

        TablePanel table = new TablePanel(ID_OBJECT_TABLE, objectProvider, columns, 10);
        table.setOutputMarkupId(true);
        form.add(table);
    }

    private List<IColumn> createColumns(){
        List<IColumn> columns = new ArrayList<>();

        columns.add(new PropertyColumn<ObjectType, String>(new Model<>("Name"), "name", "name"));
        columns.add(new PropertyColumn<ObjectType, String>(new Model<>("UID"), "uid", "uid"));

        columns.add(new EditDeleteButtonColumn<ObjectType>(new Model<>("Actions")){

            @Override
            public void editPerformed(AjaxRequestTarget target, IModel<ObjectType> rowModel) {
                PageDebugList.this.editObjectPerformed(target, rowModel);
            }

            @Override
            public void removePerformed(AjaxRequestTarget target, IModel<ObjectType> rowModel) {
                PageDebugList.this.removeObjectPerformed(target, rowModel);
            }
        });

        return columns;
    }

    /**
     *  Add another objectTypes introduced later in the development
     * */
    private List<Class<? extends ObjectType>> createObjectTypeList(){
        List<Class<? extends ObjectType>> list = new ArrayList<>();
        list.add(AccountType.class);
        list.add(FederationMemberType.class);
        list.add(OrgType.class);
        list.add(ResourceType.class);
        list.add(RoleType.class);
        list.add(SystemConfigurationType.class);
        list.add(UserType.class);
        return list;
    }

    private Form getMainForm(){
        return (Form) get(ID_MAIN_FORM);
    }

    private TablePanel getTable(){
        return (TablePanel) get(ID_MAIN_FORM + ":" + ID_OBJECT_TABLE);
    }

    private ObjectDataProvider getTableDataProvider(){
        return (ObjectDataProvider) getTable().getDataTable().getDataProvider();
    }

    private void updateObjectTablePerformed(AjaxRequestTarget target){
        ObjectDataProvider provider = getTableDataProvider();
        provider.setType(objectType);

        TablePanel table = new TablePanel(ID_OBJECT_TABLE, provider, createColumns(), 10);
        getMainForm().replace(table);

        target.add(getMainForm());
    }

    private void editObjectPerformed(AjaxRequestTarget target, IModel<ObjectType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            error("Couldn't edit selected object. It is no longer available.");
            target.add(getFeedbackPanel());
            return;
        }

        ObjectType object = rowModel.getObject();
        PageParameters parameters = new PageParameters();
        parameters.add(UID_PAGE_PARAMETER_NAME, object.getUid());

        if(object instanceof AccountType){
            setResponsePage(new PageDebugObject(parameters, AccountType.class));
        } else if(object instanceof OrgType){
            setResponsePage(new PageDebugObject(parameters, OrgType.class));
        } else if(object instanceof ResourceType){
            setResponsePage(new PageDebugObject(parameters, ResourceType.class));
        } else if(object instanceof RoleType){
            setResponsePage(new PageDebugObject(parameters, RoleType.class));
        } else if(object instanceof UserType){
            setResponsePage(new PageDebugObject(parameters, UserType.class));
        } else if(object instanceof FederationMemberType){
            setResponsePage(new PageDebugObject(parameters, FederationMemberType.class));
        } else if(object instanceof SystemConfigurationType){
            setResponsePage(new PageDebugObject(parameters, SystemConfigurationType.class));
        }
    }

    private void removeObjectPerformed(AjaxRequestTarget target, IModel<ObjectType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            error("Object selected to delete does not exist.");
            target.add(getFeedbackPanel());
            return;
        }

        ObjectType object = rowModel.getObject();
        String objectName = object.getName();

        try {
            getModelService().deleteObject(object);
        } catch (GeneralException e){
            LOGGER.error("Could not delete object: '" + objectName + "'. Reason: ", e);
            error("Could not delete object: '" + objectName + "'. Reason: " + e.getExceptionMessage());
            target.add(getFeedbackPanel());
            return;
        }

        LOGGER.info("Object '" + objectName + "' was successfully deleted from the system.");
        success("Object '" + objectName + "' was successfully deleted from the system.");
        target.add(getFeedbackPanel(), getMainForm());
    }
}
