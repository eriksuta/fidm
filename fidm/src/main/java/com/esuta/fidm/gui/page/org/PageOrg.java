package com.esuta.fidm.gui.page.org;

import com.esuta.fidm.gui.component.form.MultiValueTextPanel;
import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.GeneralException;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.repository.schema.OrgType;
import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.io.Serializable;
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

    private IModel<OrgType> model;

    public PageOrg(){
        model = new LoadableModel<OrgType>() {
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

        MultiValueTextPanel orgType = new MultiValueTextPanel<String>(ID_ORG_TYPE,
                new PropertyModel<List<String>>(model, "orgType"), null, false, true){

            @Override
            protected String createNewEmptyItem() {
                return new String();
            }

            @Override
            protected IModel<String> createTextModel(IModel<String> model) {
                return model;
            }
        };
        mainForm.add(orgType);

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
    }

    private void cancelPerformed(){
        setResponsePage(PageOrgList.class);
    }

    private void savePerformed(AjaxRequestTarget target){
        ModelService modelService = getModelService();
        OrgType orgUnit;

        if(model == null || model.getObject() == null){
            return;
        }

        orgUnit = model.getObject();

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
