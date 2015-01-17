package com.esuta.fidm.gui.page.resource;

import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.GeneralException;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.repository.schema.ResourceType;
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

/**
 *  @author shood
 * */
public class PageResource extends PageBase {

    private transient Logger LOGGER = Logger.getLogger(PageResource.class);

    public static final String ID_MAIN_FORM = "mainForm";
    public static final String ID_NAME = "name";
    public static final String ID_DESCRIPTION = "description";
    public static final String ID_TYPE = "type";
    public static final String ID_BUTTON_SAVE = "saveButton";
    public static final String ID_BUTTON_CANCEL = "cancelButton";

    private IModel<ResourceType> model;

    public PageResource(){
        this(null);
    }

    public PageResource(PageParameters parameters){
        super(parameters);

        model = new LoadableModel<ResourceType>(false) {

            @Override
            protected ResourceType load() {
                return loadResource();
            }
        };

        initLayout();
    }

    private ResourceType loadResource(){
        if(!isEditingResource()){
            return new ResourceType();
        }

        PageParameters parameters = getPageParameters();
        String uid = parameters.get(UID_PAGE_PARAMETER_NAME).toString();
        ResourceType resource;

        try {
            resource = getModelService().readObject(ResourceType.class, uid);
        } catch (DatabaseCommunicationException exc){
            error("Couldn't retrieve resource with oid: '" + uid + "' from the repository. Reason: " + exc.getExceptionMessage());
            LOGGER.error("Couldn't retrieve resource with oid: '" + uid + "' from the repository. Reason: ", exc);
            throw new RestartResponseException(PageResourceList.class);
        }

        return resource;
    }

    private boolean isEditingResource(){
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

        TextArea description = new TextArea<>(ID_DESCRIPTION, new PropertyModel<String>(model, "description"));
        mainForm.add(description);

        TextField type = new TextField<>(ID_TYPE, new PropertyModel<String>(model, "resourceType"));
        mainForm.add(type);

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
        setResponsePage(PageResourceList.class);
    }

    private void savePerformed(AjaxRequestTarget target){
        ModelService modelService = getModelService();
        ResourceType resource;

        if(model == null || model.getObject() == null){
            return;
        }

        resource = model.getObject();

        try{

            if(!isEditingResource()){
                modelService.createObject(resource);
            } else {
                modelService.updateObject(resource);
            }

        } catch (GeneralException e){
            LOGGER.error("Can't add resource: ", e);
            error("Can't add resource with name: '" + resource.getName() + "'. Reason: " + e.getExceptionMessage());
        }

        getSession().success("Resource '" + resource.getName() + "' has been saved successfully.");
        LOGGER.info("Resource '" + resource.getName() + "' has been saved successfully.");
        setResponsePage(PageResourceList.class);
        target.add(getFeedbackPanel());
    }
}
