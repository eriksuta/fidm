package com.esuta.fidm.gui.page.org;

import com.esuta.fidm.gui.component.behavior.VisibleEnableBehavior;
import com.esuta.fidm.gui.component.data.ObjectDataProvider;
import com.esuta.fidm.gui.component.data.column.EditDeleteButtonColumn;
import com.esuta.fidm.gui.component.data.table.TablePanel;
import com.esuta.fidm.gui.component.form.MultiValueTextEditPanel;
import com.esuta.fidm.gui.component.form.MultiValueTextPanel;
import com.esuta.fidm.gui.component.modal.ObjectChooserDialog;
import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.gui.page.users.PageUser;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.GeneralException;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.repository.schema.OrgType;
import com.esuta.fidm.repository.schema.UserType;
import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 * */
public class PageOrg extends PageBase {

    private transient Logger LOGGER = Logger.getLogger(PageOrg.class);

    private static final String ID_MAIN_FORM = "mainForm";
    private static final String ID_NAME = "name";
    private static final String ID_DISPLAY_NAME = "displayName";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_LOCALITY = "locality";
    private static final String ID_ORG_TYPE = "orgType";
    private static final String ID_PARENT_ORG_UNIT = "parentOrgUnits";
    private static final String ID_BUTTON_SAVE = "saveButton";
    private static final String ID_BUTTON_CANCEL = "cancelButton";
    private static final String ID_MEMBERS_CONTAINER = "memberContainer";
    private static final String ID_MEMBERS_TABLE = "membersTable";
    private static final String ID_GOVERNORS_CONTAINER = "governorsContainer";
    private static final String ID_BUTTON_ADD_GOVERNOR = "addGovernor";
    private static final String ID_GOVERNORS_TABLE = "governorsTable";

    private static final String ID_PARENT_ORG_UNIT_CHOOSER = "parentOrgUnitChooser";
    private static final String ID_GOVERNOR_CHOOSER = "governorChooser";

    private IModel<OrgType> model;

    public PageOrg(){
        this(null);
    }

    public PageOrg(PageParameters parameters){
        super(parameters);

        model = new LoadableModel<OrgType>(false) {
            @Override
            protected OrgType load() {
                return loadOrgUnit();
            }
        };

        initLayout();
    }

    private OrgType loadOrgUnit(){
        if(!isEditingOrgUnit()){
            return new OrgType();
        }

        PageParameters parameters = getPageParameters();
        String uid = parameters.get(UID_PAGE_PARAMETER_NAME).toString();
        OrgType role;

        try {
            role = getModelService().readObject(OrgType.class, uid);
        } catch (DatabaseCommunicationException exc){
            error("Couldn't retrieve org. unit with oid: '" + uid + "' from the repository. Reason: " + exc.getExceptionMessage());
            LOGGER.error("Couldn't retrieve org. unit with oid: '" + uid + "' from the repository. Reason: ", exc);
            throw new RestartResponseException(PageOrgList.class);
        }

        return role;
    }

    private boolean isEditingOrgUnit(){
        PageParameters parameters = getPageParameters();
        return !parameters.get(UID_PAGE_PARAMETER_NAME).isEmpty();
    }

    private void initLayout(){
        Form mainForm = new Form(ID_MAIN_FORM);
        mainForm.setOutputMarkupId(true);
        add(mainForm);

        TextField name = new TextField<>(ID_NAME, new PropertyModel<String>(model, "name"));
        name.setRequired(true);
        mainForm.add(name);

        TextField displayName = new TextField<>(ID_DISPLAY_NAME, new PropertyModel<String>(model, "displayName"));
        displayName.setRequired(true);
        mainForm.add(displayName);

        TextArea description = new TextArea<>(ID_DESCRIPTION, new PropertyModel<String>(model, "description"));
        mainForm.add(description);

        TextField locality = new TextField<>(ID_LOCALITY, new PropertyModel<String>(model, "locality"));
        mainForm.add(locality);

        MultiValueTextPanel orgType = new MultiValueTextPanel<>(ID_ORG_TYPE,
                new PropertyModel<List<String>>(model, "orgType"), false);
        mainForm.add(orgType);

        MultiValueTextEditPanel parentOrgUnit = new MultiValueTextEditPanel<String>(ID_PARENT_ORG_UNIT,
                new PropertyModel<List<String>>(model, "parentOrgUnits"), false, true){

            @Override
            protected IModel<String> createTextModel(IModel<String> model) {
                return createParentOrgUnitDisplayModel(model);
            }

            @Override
            protected String createNewEmptyItem() {
                return "";
            }

            @Override
            protected void editPerformed(AjaxRequestTarget target, String object) {
                PageOrg.this.editParentOrgUnitPerformed(target);
            }
        };
        mainForm.add(parentOrgUnit);

        AjaxSubmitLink cancel = new AjaxSubmitLink(ID_BUTTON_CANCEL) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                cancelPerformed();
            }
        };
        cancel.setDefaultFormProcessing(false);
        mainForm.add(cancel);

        AjaxSubmitLink save = new AjaxSubmitLink(ID_BUTTON_SAVE) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                savePerformed(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(getFeedbackPanel());
            }
        };
        mainForm.add(save);

        initModalWindows();
        initContainers(mainForm);
    }

    private void initModalWindows(){
        ModalWindow parentOrgUnitChooser = new ObjectChooserDialog<OrgType>(ID_PARENT_ORG_UNIT_CHOOSER, OrgType.class){

            @Override
            public void objectChoosePerformed(AjaxRequestTarget target, IModel<OrgType> rowModel) {
                parentOrgUnitChoosePerformed(target, rowModel);
            }

            @Override
            public String getChooserTitle() {
                return "Choose parent org. unit";
            }

            @Override
            public List<OrgType> applyObjectFilter(List<OrgType> list) {
                return applyParentOrgChooserFilter(list);
            }
        };
        add(parentOrgUnitChooser);

        ModalWindow governorChooser = new ObjectChooserDialog<UserType>(ID_GOVERNOR_CHOOSER, UserType.class){

            @Override
            public void objectChoosePerformed(AjaxRequestTarget target, IModel<UserType> rowModel) {
                governorChoosePerformed(target, rowModel);
            }

            @Override
            public String getChooserTitle() {
                return "Choose parent org. unit";
            }

            @Override
            public List<UserType> applyObjectFilter(List<UserType> list) {
                return applyGovernorChooserFilter(list);
            }
        };
        add(governorChooser);
    }

    private void initContainers(Form mainForm){
        //Members Container
        WebMarkupContainer membersContainer = new WebMarkupContainer(ID_MEMBERS_CONTAINER);
        membersContainer.setOutputMarkupId(true);
        membersContainer.setOutputMarkupPlaceholderTag(true);
        mainForm.add(membersContainer);

        List<IColumn> membersColumns = createMemberColumns();
        final ObjectDataProvider membersProvider = new ObjectDataProvider<UserType>(getPage(), UserType.class){

            @Override
            public List<UserType> applyDataFilter(List<UserType> list) {
                List<UserType> memberList = new ArrayList<>();

                if(model != null && model.getObject() != null){
                    String orgUid = model.getObject().getUid();

                    for(UserType user: list){
                        if(user.getOrgUnitAssignments().contains(orgUid)){
                            memberList.add(user);
                        }
                    }
                }

                return memberList;
            }
        };

        TablePanel membersTable = new TablePanel(ID_MEMBERS_TABLE, membersProvider, membersColumns, 10);
        membersTable.add(new VisibleEnableBehavior(){

            @Override
            public boolean isVisible() {
                return membersProvider.size() > 0;
            }
        });
        membersTable.setOutputMarkupId(true);
        membersContainer.add(membersTable);

        //Governors Container
        WebMarkupContainer governorsContainer = new WebMarkupContainer(ID_GOVERNORS_CONTAINER);
        governorsContainer.setOutputMarkupId(true);
        governorsContainer.setOutputMarkupPlaceholderTag(true);
        mainForm.add(governorsContainer);

        AjaxLink addOrgUnit = new AjaxLink(ID_BUTTON_ADD_GOVERNOR) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                addGovernorPerformed(target);
            }
        };
        governorsContainer.add(addOrgUnit);

        List<IColumn> governorsColumn = createGovernorColumns();
        final ObjectDataProvider governorsProvider = new ObjectDataProvider<UserType>(getPage(), UserType.class){

            @Override
            public List<UserType> applyDataFilter(List<UserType> list) {
                List<UserType> managersList = new ArrayList<>();

                if(model != null && model.getObject() != null){
                    List<String> managersUid = model.getObject().getGovernors();

                    for(UserType user: list){
                        if(managersUid.contains(user.getUid())){
                            managersList.add(user);
                        }
                    }
                }

                return managersList;
            }
        };

        TablePanel governorsTable = new TablePanel(ID_GOVERNORS_TABLE, governorsProvider, governorsColumn, 10);
        governorsTable.add(new VisibleEnableBehavior(){

            @Override
            public boolean isVisible() {
                return governorsProvider.size() > 0;
            }
        });
        governorsTable.setOutputMarkupId(true);
        governorsContainer.add(governorsTable);
    }

    private List<IColumn> createMemberColumns(){
        List<IColumn> columns = new ArrayList<>();

        columns.add(new PropertyColumn<UserType, String>(new Model<>("Name"), "name", "name"));
        columns.add(new PropertyColumn<UserType, String>(new Model<>("Given Name"), "givenName", "givenName"));
        columns.add(new PropertyColumn<UserType, String>(new Model<>("Family Name"), "familyName", "familyName"));
        columns.add(new PropertyColumn<UserType, String>(new Model<>("E-mail"), "emailAddress", "emailAddress"));
        columns.add(new PropertyColumn<UserType, String>(new Model<>("Locality"), "locality", "locality"));

        columns.add(new EditDeleteButtonColumn<UserType>(new Model<>("Actions")){

            @Override
            public void editPerformed(AjaxRequestTarget target, IModel<UserType> rowModel) {
                PageOrg.this.editMemberPerformed(target, rowModel);
            }

            @Override
            public boolean getRemoveVisible() {
                return false;
            }
        });

        return columns;
    }

    private List<IColumn> createGovernorColumns(){
        List<IColumn> columns = new ArrayList<>();

        columns.add(new PropertyColumn<UserType, String>(new Model<>("Name"), "name", "name"));
        columns.add(new PropertyColumn<UserType, String>(new Model<>("Given Name"), "givenName", "givenName"));
        columns.add(new PropertyColumn<UserType, String>(new Model<>("Family Name"), "familyName", "familyName"));
        columns.add(new PropertyColumn<UserType, String>(new Model<>("E-mail"), "emailAddress", "emailAddress"));
        columns.add(new PropertyColumn<UserType, String>(new Model<>("Locality"), "locality", "locality"));

        columns.add(new EditDeleteButtonColumn<UserType>(new Model<>("Actions")){

            @Override
            public void editPerformed(AjaxRequestTarget target, IModel<UserType> rowModel) {
                PageOrg.this.editGovernorPerformed(target, rowModel);
            }

            @Override
            public void removePerformed(AjaxRequestTarget target, IModel<UserType> rowModel) {
                PageOrg.this.removeGovernorPerformed(target, rowModel);
            }
        });

        return columns;
    }

    private Form getMainForm(){
        return (Form) get(ID_MAIN_FORM);
    }

    private WebMarkupContainer getMembersContainer(){
        return (WebMarkupContainer) get(ID_MAIN_FORM + ":" + ID_MEMBERS_CONTAINER);
    }

    private WebMarkupContainer getGovernorsContainer(){
        return (WebMarkupContainer) get(ID_MAIN_FORM + ":" + ID_GOVERNORS_CONTAINER);
    }

    private IModel<String> createParentOrgUnitDisplayModel(final IModel<String> uidModel){
        return new LoadableModel<String>() {

            @Override
            protected String load() {
                if(uidModel == null || uidModel.getObject() == null){
                    return null;
                }

                String uid = uidModel.getObject();
                OrgType parent = null;

                try {
                    parent = getModelService().readObject(OrgType.class, uid);
                } catch (DatabaseCommunicationException e) {
                    LOGGER.error("Parent org. unit with uid: '" + uid + "' does not exist.");
                    error("Parent org. unit with uid: '" + uid + "' does not exist.");
                }

                if(parent == null){
                    return null;
                }

                return parent.getDisplayName();
            }
        };
    }

    private void governorChoosePerformed(AjaxRequestTarget target, IModel<UserType> governorModel){
        if(governorModel == null || governorModel.getObject() == null){
            return;
        }

        if(model.getObject() == null){
            return;
        }

        String governorUid = governorModel.getObject().getUid();
        model.getObject().getGovernors().add(governorUid);

        ModalWindow window = (ModalWindow) get(ID_GOVERNOR_CHOOSER);
        window.close(target);
        target.add(getGovernorsContainer());
    }

    /**
     *  TODO - filter org. unit that can be chosen as parents, some cases:
     *      * any org. unit from sub-tree (to prevent cycles in org. unit hierarchy)
     * */
    private List<OrgType> applyParentOrgChooserFilter(List<OrgType> list){
        List<OrgType> newOrgList = new ArrayList<>();

        if(model.getObject() == null){
            return list;
        }

        List<String> parentOrgUnits = model.getObject().getParentOrgUnits();
        String currentOrgUid = model.getObject().getUid();

        for(OrgType org: list){
            if(!parentOrgUnits.contains(org.getUid()) && !currentOrgUid.equals(org.getUid())){
                newOrgList.add(org);
            }
        }

        return newOrgList;
    }

    private List<UserType> applyGovernorChooserFilter(List<UserType> list){
        List<UserType> newUserList = new ArrayList<>();

        if(model.getObject() == null){
            return list;
        }

        List<String> currentGovernors = model.getObject().getGovernors();

        for(UserType user: list){
            if(!currentGovernors.contains(user.getUid())){
                newUserList.add(user);
            }
        }

        return newUserList;
    }

    private void editParentOrgUnitPerformed(AjaxRequestTarget target){
        ModalWindow modal = (ModalWindow) get(ID_PARENT_ORG_UNIT_CHOOSER);
        modal.show(target);
    }

    private void parentOrgUnitChoosePerformed(AjaxRequestTarget target, IModel<OrgType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            error("Chosen value is not a valid org. unit.");
            target.add(getFeedbackPanel());
            return;
        }

        if(model == null || model.getObject() == null){
            error("Couldn't add parent org. to this org. unit. Invalid org. unit model, please refresh this page.");
            target.add(getFeedbackPanel());
            return;
        }

        String uid = rowModel.getObject().getUid();
        model.getObject().getParentOrgUnits().add(uid);

        ModalWindow dialog = (ModalWindow) get(ID_PARENT_ORG_UNIT_CHOOSER);
        dialog.close(target);
        target.add(getMainForm());
    }

    private void editMemberPerformed(AjaxRequestTarget target, IModel<UserType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            error("Couldn't edit selected user. It is no longer available.");
            target.add(getFeedbackPanel());
            return;
        }

        PageParameters parameters = new PageParameters();
        parameters.add(UID_PAGE_PARAMETER_NAME, rowModel.getObject().getUid());
        setResponsePage(new PageUser(parameters));
    }

    /*
    *   Since governor is also a UserType, we can use editMemberPerformed() method
    * */
    private void editGovernorPerformed(AjaxRequestTarget target, IModel<UserType> rowModel){
        editMemberPerformed(target, rowModel);
    }

    private void addGovernorPerformed(AjaxRequestTarget target){
        ModalWindow modal = (ModalWindow) get(ID_GOVERNOR_CHOOSER);
        modal.show(target);
    }

    private void removeGovernorPerformed(AjaxRequestTarget target, IModel<UserType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            error("Couldn't remove selected governor assignment. Something went wrong.");
            target.add(getFeedbackPanel());
            return;
        }

        String governorUid = rowModel.getObject().getUid();
        model.getObject().getGovernors().remove(governorUid);
        success("Governor with uid: '" + governorUid + "' was removed successfully");
        target.add(getGovernorsContainer(), getFeedbackPanel());
    }

    private void cancelPerformed(){
        setResponsePage(PageOrgList.class);
    }

    private void savePerformed(AjaxRequestTarget target){
        ModelService modelService = getModelService();
        OrgType orgUnit;

        if(model == null || model.getObject() == null){
            error("Couldn't save org. unit.");
            target.add(getFeedbackPanel());
            return;
        }

        orgUnit = model.getObject();

        //Filtering empty org. unit types
        List<String> orgTypes = new ArrayList<>(orgUnit.getOrgType());
        for(String type: orgTypes){
            if(type == null || type.isEmpty()){
                orgUnit.getOrgType().remove(type);
            }
        }

        //Filtering empty org. unit parents
        List<String> orgUnitParents = new ArrayList<>(orgUnit.getParentOrgUnits());
        for(String parent: orgUnitParents){
            if(parent == null || parent.isEmpty()){
                orgUnit.getParentOrgUnits().remove(parent);
            }
        }

        try{
            if(!isEditingOrgUnit()){
                modelService.createObject(orgUnit);
            } else {
                modelService.updateObject(orgUnit);
            }

        } catch (GeneralException e){
            LOGGER.error("Can't add org. unit: ", e);
            error("Can't add org. unit with name: '" + orgUnit.getName() + "'. Reason: " + e.getExceptionMessage());
        }

        getSession().success("Org. Unit '" + orgUnit.getName() + "' has been saved successfully.");
        LOGGER.info("Org. Unit '" + orgUnit.getName() + "' has been saved successfully.");
        setResponsePage(PageOrgList.class);
        target.add(getFeedbackPanel());
    }
}
