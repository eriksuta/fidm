package com.esuta.fidm.gui.page.federation;

import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.gui.page.org.PageOrgList;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.ObjectAlreadyExistsException;
import com.esuta.fidm.model.federation.client.ObjectTypeRestResponse;
import com.esuta.fidm.repository.schema.core.FederationMemberType;
import com.esuta.fidm.repository.schema.core.OrgType;
import com.esuta.fidm.repository.schema.support.FederationIdentifierType;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.eclipse.jetty.http.HttpStatus;

/**
 *  @author shood
 * */
public class PageOrgPreview extends PageBase{

    private static final transient Logger LOGGER = Logger.getLogger(PageOrgPreview.class);

    private static final String ID_MAIN_FORM = "mainForm";
    private static final String ID_NAME = "name";
    private static final String ID_DISPLAY_NAME = "displayName";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_LOCALITY = "locality";
    private static final String ID_TYPE = "type";
    private static final String ID_PARENT_ORG = "parentOrgUnits";

    private static final String ID_GOVERNOR_LABEL = "governorsLabel";
    private static final String ID_RESOURCE_INDUCEMENT_LABEL = "resourceInducementsLabel";
    private static final String ID_ROLE_INDUCEMENT_LABEL = "roleInducementsLabel";

    private static final String ID_BUTTON_SHARE = "shareButton";
    private static final String ID_BUTTON_SHARE_HIERARCHY = "shareHierarchyButton";
    private static final String ID_BUTTON_CANCEL = "cancelButton";

    private IModel<OrgType> model;

    public PageOrgPreview(){
        this(null);
    }

    public PageOrgPreview(final OrgType org){
        model = new LoadableModel<OrgType>(false) {

            @Override
            protected OrgType load() {
                return org;
            }
        };

        initLayout();
    }

    private void initLayout(){
        Form mainForm = new Form(ID_MAIN_FORM);
        mainForm.setOutputMarkupId(true);
        add(mainForm);

        Label name = new Label(ID_NAME, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return model.getObject().getName();
            }
        });
        mainForm.add(name);

        Label displayName = new Label(ID_DISPLAY_NAME, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return model.getObject().getDisplayName();
            }
        });
        mainForm.add(displayName);

        Label description = new Label(ID_DESCRIPTION, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return model.getObject().getDescription();
            }
        });
        mainForm.add(description);

        Label locality = new Label(ID_LOCALITY, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return model.getObject().getLocality();
            }
        });
        mainForm.add(locality);

        Label type = new Label(ID_TYPE, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                StringBuilder sb = new StringBuilder();

                for(String type: model.getObject().getOrgType()){
                    sb.append(type);
                    sb.append(", ");
                }

                return sb.toString();
            }
        });
        mainForm.add(type);

        Label parentOrgLabel = new Label(ID_PARENT_ORG, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return Integer.toString(model.getObject().getParentOrgUnits().size());
            }
        });
        mainForm.add(parentOrgLabel);

        initGovernorInducementPanel(mainForm);
        initButtons(mainForm);
    }

    private void initGovernorInducementPanel(Form mainForm){
        Label governorsLabel = new Label(ID_GOVERNOR_LABEL, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return "Governors (" + model.getObject().getGovernors().size() + ")";
            }
        });
        mainForm.add(governorsLabel);

        Label resourceInducementsLabel = new Label(ID_RESOURCE_INDUCEMENT_LABEL, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return "Resource Inducements (" + model.getObject().getResourceInducements().size() + ")";
            }
        });
        mainForm.add(resourceInducementsLabel);

        Label roleInducementsLabel = new Label(ID_ROLE_INDUCEMENT_LABEL, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return "RoleInducements (" + model.getObject().getRoleInducements().size() + ")";
            }
        });
        mainForm.add(roleInducementsLabel);
    }

    private void initButtons(Form mainForm){
        AjaxSubmitLink cancel = new AjaxSubmitLink(ID_BUTTON_CANCEL) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                cancelPerformed();
            }
        };
        cancel.setDefaultFormProcessing(false);
        mainForm.add(cancel);

        AjaxSubmitLink shareHierarchy = new AjaxSubmitLink(ID_BUTTON_SHARE_HIERARCHY) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                shareHierarchyPerformed(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(getFeedbackPanel());
            }
        };
        mainForm.add(shareHierarchy);

        AjaxSubmitLink share = new AjaxSubmitLink(ID_BUTTON_SHARE) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                sharePerformed(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(getFeedbackPanel());
            }
        };
        mainForm.add(share);
    }

    public Form getMainForm(){
        return (Form) get(ID_MAIN_FORM);
    }

    private void cancelPerformed(){
        setResponsePage(PageFederationList.class);
    }

    private void shareHierarchyPerformed(AjaxRequestTarget target){
//        TODO
        warn("Not implemented yet.");
        target.add(getFeedbackPanel());
    }

    private void sharePerformed(AjaxRequestTarget target){
        if(model == null || model.getObject() == null){
            warn("Can't share the org. unit.");
            target.add(getFeedbackPanel());
            return;
        }

        OrgType orgToShare = model.getObject();
        FederationIdentifierType orgIdentifier = orgToShare.getFederationIdentifier();
        FederationMemberType member = getFederationMemberByName(orgIdentifier.getFederationMemberId());

        if(member == null){
            warn("There currently isn't a membership with federation member: '" + orgIdentifier.getFederationMemberId() + "'.");
            target.add(getFeedbackPanel());
            return;
        }

        try {
            ObjectTypeRestResponse<OrgType> response = getFederationServiceClient().createGetOrgUnitRequest(member, orgIdentifier);

            int status = response.getStatus();
            if(HttpStatus.OK_200 == status){
                OrgType org = response.getValue();
                org = getModelService().createObject(org);
                info("Org. unit sharef correctly. New org.: '" + org.getName() + "'(" + org.getUid() + ").");

            } else {
                String message = response.getMessage();
                LOGGER.error("Could not share org. unit. REST response: " + status + ", message: " + message);
                error("Could not share org. unit. REST response: " + status + ", message: " + message);
            }

        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Could not create and process request to share selected org. unit.", e);
            error("Could not create and process request to share selected org. unit. Reason: " + e);
        } catch (ObjectAlreadyExistsException e) {
            LOGGER.error("Could not create org. unit. Conflicting object already exists.", e);
            error("Could not create org. unit. Conflicting object already exists. Reason: " + e);
        }

        setResponsePage(PageOrgList.class);
        target.add(getFeedbackPanel());
    }
}
