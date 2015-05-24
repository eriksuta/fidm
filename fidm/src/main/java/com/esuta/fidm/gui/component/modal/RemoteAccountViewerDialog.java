package com.esuta.fidm.gui.component.modal;

import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.repository.schema.core.AccountType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

/**
 *  @author shood
 * */
public class RemoteAccountViewerDialog extends ModalWindow{

    private static final String ID_NAME = "name";
    private static final String ID_MEMBER = "member";
    private static final String ID_RESOURCE = "resource";
    private static final String ID_PROTECTED = "protected";
    private static final String ID_BUTTON_CONTINUE = "continueButton";

    private boolean initialized;
    private IModel<AccountType> model;

    public RemoteAccountViewerDialog(String id, final AccountType account){
        super(id);

        model = new LoadableModel<AccountType>(false) {

            @Override
            protected AccountType load() {
                return account == null ? new AccountType() : account;
            }
        };

        setTitle("Remote Account Viewer");
        showUnloadConfirmation(false);
        setCssClassName(ModalWindow.CSS_CLASS_BLUE);
        setCookieName(ObjectChooserDialog.class.getSimpleName() + ((int) (Math.random() * 100)));
        setResizable(true);
        setInitialWidth(450);
        setInitialHeight(550);
        setWidthUnit("px");

        WebMarkupContainer content = new WebMarkupContainer(getContentId());
        content.setOutputMarkupId(true);
        setContent(content);
    }

    public void updateModel(AccountType account){
        if(account != null) {
            model.setObject(account);
        }
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
                return model.getObject().getName();
            }
        });
        content.add(name);

        Label member = new Label(ID_MEMBER, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return model.getObject().getFederationIdentifier().getFederationMemberId();
            }
        });
        content.add(member);

        Label resource = new Label(ID_RESOURCE, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return model.getObject().getResource().getFederationIdentifier().getUniqueAttributeValue();
            }
        });
        content.add(resource);

        Label _protected = new Label(ID_PROTECTED, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return Boolean.toString(model.getObject().is_protected());
            }
        });
        content.add(_protected);

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
