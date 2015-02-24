package com.esuta.fidm.gui.page.config;

import com.esuta.fidm.gui.component.WebMiscUtil;
import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.ObjectNotFoundException;
import com.esuta.fidm.repository.schema.core.ObjectType;
import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *  @author shood
 * */
public class PageDebugObject extends PageBase{

    enum ObjectFormat{
        XML,
        JSON
    }

    private transient Logger LOGGER = Logger.getLogger(PageDebugObject.class);

    private static final String ID_MAIN_FORM = "mainForm";
    private static final String ID_FORMAT_CHOOSER = "objectFormatChooser";
    private static final String ID_OBJECT_EDITOR = "objectEditor";
    private static final String ID_BUTTON_SAVE = "saveButton";
    private static final String ID_BUTTON_CANCEL = "cancelButton";

    private IModel<ObjectType> model;
    private Class<? extends ObjectType> type;
    private ObjectFormat objectFormat = ObjectFormat.XML;
    private IModel<String> objectRepresentation;

    public PageDebugObject(PageParameters parameters, Class<? extends ObjectType> type){
        super(parameters);

        this.type = type;
        model = new LoadableModel<ObjectType>(false) {

            @Override
            protected ObjectType load() {
                return loadObject();
            }
        };

        objectRepresentation = new LoadableModel<String>() {

            @Override
            protected String load() {
                return loadObjectRepresentation();
            }
        };

        initLayout();
    }

    private ObjectType loadObject(){
        if(!isEditingObject()){
            return new ObjectType();
        }

        PageParameters parameters = getPageParameters();
        String uid = parameters.get(UID_PAGE_PARAMETER_NAME).toString();
        ObjectType object;

        try {
            object = getModelService().readObject(type, uid);
        } catch (DatabaseCommunicationException exc){
            error("Couldn't retrieve object with oid: '" + uid + "' from the repository. Reason: " + exc.getExceptionMessage());
            LOGGER.error("Couldn't retrieve object with oid: '" + uid + "' from the repository. Reason: ", exc);
            throw new RestartResponseException(PageDebugList.class);
        }

        return object;
    }

    private String loadObjectRepresentation(){
        if(objectFormat.equals(ObjectFormat.XML)){

            try {
                return WebMiscUtil.prepareObjectTypeInXml(model.getObject());
            } catch (Exception e) {
                LOGGER.error("Could not create XML representation of object.");
                throw new RestartResponseException(PageDebugList.class);
            }

        } else if(objectFormat.equals(ObjectFormat.JSON)){
            return WebMiscUtil.prepareObjectTypeInJson(model.getObject());
        }

        return null;
    }

    private boolean isEditingObject(){
        PageParameters parameters = getPageParameters();
        return !parameters.get(UID_PAGE_PARAMETER_NAME).isEmpty();
    }

    private void initLayout(){
        DropDownChoice objectFormatChooser = new DropDownChoice<>(ID_FORMAT_CHOOSER, new PropertyModel<ObjectFormat>(this, "objectFormat"),
                WebMiscUtil.createReadonlyModelFromEnum(ObjectFormat.class), new EnumChoiceRenderer<ObjectFormat>(this));
        objectFormatChooser.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                objectFormatChangePerformed(target);
            }
        });
        add(objectFormatChooser);


        Form mainForm = new Form(ID_MAIN_FORM);
        mainForm.setOutputMarkupId(true);
        add(mainForm);

        TextArea editorArea = new TextArea<>(ID_OBJECT_EDITOR, objectRepresentation);
        editorArea.setOutputMarkupId(true);
        mainForm.add(editorArea);

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
        setResponsePage(PageDebugList.class);
    }

    private Form getMainForm(){
        return (Form) get(ID_MAIN_FORM);
    }

    private void objectFormatChangePerformed(AjaxRequestTarget target){
        TextArea editorArea = new TextArea<>(ID_OBJECT_EDITOR, objectRepresentation);
        editorArea.setOutputMarkupId(true);
        getMainForm().replace(editorArea);

        target.add(getMainForm());
    }

    private void savePerformed(AjaxRequestTarget target){
        ObjectType objectToSave = null;

        if(ObjectFormat.XML.equals(objectFormat)){
            objectToSave = WebMiscUtil.xmlToObject(objectRepresentation.getObject());
        } else if(ObjectFormat.JSON.equals(objectFormat)){
            objectToSave = WebMiscUtil.jsonToObject(objectRepresentation.getObject(), type);
        }

        try {
            if(objectToSave != null){
                getModelService().updateObject(objectToSave);
            }
        } catch (ObjectNotFoundException | DatabaseCommunicationException e) {
            LOGGER.error("Could not save object with uid: '" + objectToSave.getUid() + "' to repository.");
            error("Could not save object with uid: '" + objectToSave.getUid() + "' to repository.");
            target.add(getFeedbackPanel());
        }

        if(objectToSave != null){
            getSession().success("Object with uid: '" + objectToSave.getUid() + "' saved.");
            LOGGER.info("Object with uid: '" + objectToSave.getUid() + "' saved.");
        }

        setResponsePage(PageDebugList.class);
        target.add(getFeedbackPanel());
    }
}
