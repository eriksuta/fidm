package com.esuta.fidm.gui.component.modal;

import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.model.federation.client.ObjectInformationResponse;
import com.esuta.fidm.model.federation.service.ObjectInformation;
import com.esuta.fidm.repository.schema.core.FederationMemberType;
import com.esuta.fidm.repository.schema.core.ObjectReferenceType;
import com.esuta.fidm.repository.schema.support.FederationIdentifierType;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.eclipse.jetty.http.HttpStatus;

/**
 *  @author shood
 * */
public class ObjectInformationDialog extends ModalWindow {

    private transient Logger LOGGER = Logger.getLogger(ObjectInformationDialog.class);

    private static final String ID_NAME = "name";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_BUTTON_CONTINUE = "continueButton";

    private boolean initialized;
    private ObjectReferenceType reference;
    private IModel<ObjectInformation> model;

    public ObjectInformationDialog(String id, final ObjectReferenceType reference) {
        super(id);

        this.model = new LoadableModel<ObjectInformation>() {

            @Override
            protected ObjectInformation load() {
                return loadObjectInformation(reference);
            }
        };

        setTitle("Object View");
        showUnloadConfirmation(false);
        setCssClassName(ModalWindow.CSS_CLASS_BLUE);
        setCookieName(ObjectInformationDialog.class.getSimpleName() + ((int) (Math.random() * 100)));
        setResizable(true);
        setInitialWidth(450);
        setInitialHeight(550);
        setWidthUnit("px");

        WebMarkupContainer content = new WebMarkupContainer(getContentId());
        content.setOutputMarkupId(true);
        setContent(content);
    }

    private ObjectInformation loadObjectInformation(ObjectReferenceType reference){
        if(reference == null || reference.getFederationIdentifier() == null){
            return new ObjectInformation();
        }

        FederationIdentifierType identifier = reference.getFederationIdentifier();
        FederationMemberType member = getPageBase().getFederationMemberByName(identifier.getFederationMemberId());

        try {
            ObjectInformationResponse response = getPageBase().getFederationServiceClient().createGetObjectInformationRequest(member, identifier);

            int status = response.getStatus();
            if(HttpStatus.OK_200 == status){
                return response.getInformationObject();
            } else {
                LOGGER.error("Could not get information about remote object. Message: " + response.getMessage());
            }

        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Could not get information about remote object.");
        }

        return new ObjectInformation();
    }

    public void updateReference(ObjectReferenceType ref){
        reference = ref;
    }

    public PageBase getPageBase(){
        return (PageBase) getPage();
    }

    @Override
    protected void onBeforeRender(){
        super.onBeforeRender();

        if(initialized){
            return;
        }

        initLayout((WebMarkupContainer) get(getContentId()));
        initialized = true;
    }

    private void initLayout(WebMarkupContainer content){
        Label name = new Label(ID_NAME, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return model.getObject().getObjectName();
            }
        });
        content.add(name);

        Label description = new Label(ID_DESCRIPTION, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return model.getObject().getObjectDescription();
            }
        });
        content.add(description);

        AjaxLink buttonContinue = new AjaxLink(ID_BUTTON_CONTINUE) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                continuePerformed(target);
            }
        };
        content.add(buttonContinue);
    }

    protected void continuePerformed(AjaxRequestTarget target){
        close(target);
    }
}
