package com.esuta.fidm.gui.page.org;

import com.esuta.fidm.gui.component.WebMiscUtil;
import com.esuta.fidm.gui.component.behavior.VisibleEnableBehavior;
import com.esuta.fidm.gui.component.data.ObjectDataProvider;
import com.esuta.fidm.gui.component.data.column.EditDeleteButtonColumn;
import com.esuta.fidm.gui.component.data.table.TablePanel;
import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.ObjectAlreadyExistsException;
import com.esuta.fidm.infra.exception.ObjectNotFoundException;
import com.esuta.fidm.repository.schema.core.FederationSharingPolicyType;
import com.esuta.fidm.repository.schema.core.FederationSharingRuleType;
import com.esuta.fidm.repository.schema.core.MultiValueTolerance;
import com.esuta.fidm.repository.schema.core.SingleValueTolerance;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 * */
public class PageSharingPolicy extends PageBase{

    private static final Logger LOGGER = Logger.getLogger(PageSharingPolicy.class);

    private static final String ID_LIST_FORM = "listForm";
    private static final String ID_POLICY_TABLE = "sharingPolicyTable";
    private static final String ID_BUTTON_ADD_POLICY = "addPolicyButton";

    private static final String ID_POLICY_FORM = "policyForm";
    private static final String ID_POLICY_CONTAINER = "policyContainer";
    private static final String ID_POLICY_LABEL = "policyLabel";
    private static final String ID_NAME = "name";
    private static final String ID_DISPLAY_NAME = "displayName";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_DEFAULT_SV_TOLERANCE = "defaultSingleValueTolerance";
    private static final String ID_DEFAULT_MV_TOLERANCE = "defaultMultiValueTolerance";
    private static final String ID_RULES_CONTAINER = "rulesContainer";
    private static final String ID_BUTTON_ADD_RULE = "addRuleButton";
    private static final String ID_RULE_REPEATER = "repeater";
    private static final String ID_RULE_HEADER = "ruleHeader";
    private static final String ID_RULE_DELETE_LINK = "ruleDelete";
    private static final String ID_RULE_LABEL = "ruleLabel";
    private static final String ID_RULE_BODY_CONTAINER = "ruleBodyContainer";
    private static final String ID_RULE_ATTRIBUTE = "ruleAttributeSelect";
    private static final String ID_RULE_SV_TOLERANCE = "singleValueTolerance";
    private static final String ID_RULE_MV_TOLERANCE = "multiValueTolerance";

    private static final String ID_BUTTON_SAVE = "saveButton";
    private static final String ID_BUTTON_CANCEL = "cancelButton";

    private IModel<FederationSharingPolicyType> selected;

    public PageSharingPolicy(){
        selected = new LoadableModel<FederationSharingPolicyType>(false) {

            @Override
            protected FederationSharingPolicyType load() {
                //By default, no policy is selected on page load
                return null;
            }
        };

        initLayout();
    }

    private void initLayout(){
        Form listForm = new Form(ID_LIST_FORM);
        listForm.setOutputMarkupId(true);
        add(listForm);

        Form policyForm = new Form(ID_POLICY_FORM);
        policyForm.setOutputMarkupId(true);
        add(policyForm);

        initPolicyList(listForm);
        initPolicyContainer(policyForm);
    }

    private Form getListForm(){
        return (Form) get(ID_LIST_FORM);
    }

    private Form getPolicyForm(){
        return (Form) get(ID_POLICY_FORM);
    }

    private WebMarkupContainer getRuleContainer(){
        return (WebMarkupContainer) get(ID_POLICY_FORM + ":" + ID_POLICY_CONTAINER + ":" + ID_RULES_CONTAINER);
    }

    private void initPolicyList(Form form){
        ObjectDataProvider<FederationSharingPolicyType> provider = new ObjectDataProvider<>(getPage(), FederationSharingPolicyType.class);
        List<IColumn> columns = createSharingPolicyColumns();

        TablePanel table = new TablePanel(ID_POLICY_TABLE, provider, columns, 10);
        table.setOutputMarkupId(true);
        form.add(table);

        AjaxLink addPolicy = new AjaxLink(ID_BUTTON_ADD_POLICY) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                addPolicyPerformed(target);
            }
        };
        form.add(addPolicy);
    }

    private List<IColumn> createSharingPolicyColumns(){
        List<IColumn> columns = new ArrayList<>();

        columns.add(new PropertyColumn<FederationSharingPolicyType, String>(new Model<>("Name"), "name", "name"));
        columns.add(new PropertyColumn<FederationSharingPolicyType, String>(new Model<>("DisplayName"), "displayName", "displayName"));
        columns.add(new AbstractColumn<FederationSharingPolicyType, String>(new Model<>("Rules")) {

            @Override
            public void populateItem(Item<ICellPopulator<FederationSharingPolicyType>> cellItem, String componentId, final IModel<FederationSharingPolicyType> rowModel) {
                cellItem.add(new Label(componentId, new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return Integer.toString(rowModel.getObject().getRules().size());
                    }
                }));
            }
        });
        columns.add(new EditDeleteButtonColumn<FederationSharingPolicyType>(new Model<>("Actions")){

            @Override
            public void editPerformed(AjaxRequestTarget target, IModel<FederationSharingPolicyType> rowModel) {
                PageSharingPolicy.this.editPolicyPerformed(target, rowModel);
            }

            @Override
            public void removePerformed(AjaxRequestTarget target, IModel<FederationSharingPolicyType> rowModel) {
                PageSharingPolicy.this.removePolicyPerformed(target, rowModel);
            }
        });

        return columns;
    }

    private void initPolicyContainer(Form form){
        WebMarkupContainer policyContainer = new WebMarkupContainer(ID_POLICY_CONTAINER);
        policyContainer.setOutputMarkupId(true);
        policyContainer.setOutputMarkupPlaceholderTag(true);
        policyContainer.add(new VisibleEnableBehavior(){

            @Override
            public boolean isVisible() {
                return selected.getObject() != null;
            }
        });
        form.add(policyContainer);

        Label policyLabel = new Label(ID_POLICY_LABEL, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return "Create/Edit policy (" + selected.getObject().getName() + ")";
            }
        });
        policyContainer.add(policyLabel);

        TextField policyName = new TextField<>(ID_NAME, new PropertyModel<String>(selected, "name"));
        policyName.setRequired(true);
        policyContainer.add(policyName);

        TextField policyDisplayName = new TextField<>(ID_DISPLAY_NAME, new PropertyModel<String>(selected, "displayName"));
        policyDisplayName.setRequired(true);
        policyContainer.add(policyDisplayName);

        TextArea policyDescription = new TextArea<>(ID_DESCRIPTION, new PropertyModel<String>(selected, "description"));
        policyContainer.add(policyDescription);

        final DropDownChoice defaultSingleValueTolerance = new DropDownChoice<>(ID_DEFAULT_SV_TOLERANCE,
                new PropertyModel<SingleValueTolerance>(selected, "defaultSingleValueTolerance"),
                WebMiscUtil.createReadonlyModelFromEnum(SingleValueTolerance.class),
                new EnumChoiceRenderer<SingleValueTolerance>(this));
        defaultSingleValueTolerance.setRequired(true);
        policyContainer.add(defaultSingleValueTolerance);

        final DropDownChoice defaultMultiValueTolerance = new DropDownChoice<>(ID_DEFAULT_MV_TOLERANCE,
                new PropertyModel<MultiValueTolerance>(selected, "defaultMultiValueTolerance"),
                WebMiscUtil.createReadonlyModelFromEnum(MultiValueTolerance.class),
                new EnumChoiceRenderer<MultiValueTolerance>(this));
        defaultMultiValueTolerance.setRequired(true);
        policyContainer.add(defaultMultiValueTolerance);

        WebMarkupContainer rulesContainer = new WebMarkupContainer(ID_RULES_CONTAINER);
        rulesContainer.setOutputMarkupId(true);
        policyContainer.add(rulesContainer);

        AjaxSubmitLink addRule = new AjaxSubmitLink(ID_BUTTON_ADD_RULE) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                addRulePerformed(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(getFeedbackPanel());
            }
        };
        rulesContainer.add(addRule);

        ListView ruleRepeater = new ListView<FederationSharingRuleType>(ID_RULE_REPEATER,
                new PropertyModel<List<FederationSharingRuleType>>(selected, "rules")) {

            @Override
            protected void populateItem(final ListItem<FederationSharingRuleType> item) {
                WebMarkupContainer ruleHeader = new WebMarkupContainer(ID_RULE_HEADER);
                ruleHeader.setOutputMarkupId(true);
                ruleHeader.add(new AttributeModifier("href", createCollapseItemId(item, true)));
                item.add(ruleHeader);

                Label ruleLabel = new Label(ID_RULE_LABEL, new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return createRuleLabel(item.getModelObject());
                    }
                });
                ruleHeader.add(ruleLabel);

                AjaxLink delete = new AjaxLink(ID_RULE_DELETE_LINK) {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        deleteRulePerformed(target, item.getModelObject());
                    }
                };
                ruleHeader.add(delete);

                WebMarkupContainer ruleBody = new WebMarkupContainer(ID_RULE_BODY_CONTAINER);
                ruleBody.setOutputMarkupId(true);
                ruleBody.setOutputMarkupPlaceholderTag(true);
                ruleBody.setMarkupId(createCollapseItemId(item, false).getObject());
                item.add(ruleBody);

                final DropDownChoice singleValueTolerance = new DropDownChoice<>(ID_RULE_SV_TOLERANCE,
                        new PropertyModel<SingleValueTolerance>(item.getModelObject(), "singleValueTolerance"),
                        WebMiscUtil.createReadonlyModelFromEnum(SingleValueTolerance.class),
                        new EnumChoiceRenderer<SingleValueTolerance>(this));
                singleValueTolerance.add(new VisibleEnableBehavior(){

                    @Override
                    public boolean isEnabled() {
                        String attributeName = item.getModelObject().getAttributeName();

                        return !(attributeName == null || attributeName.isEmpty()) &&
                                WebMiscUtil.isOrgAttributeSingleValue(item.getModelObject().getAttributeName());

                    }
                });
                singleValueTolerance.setOutputMarkupId(true);
                ruleBody.add(singleValueTolerance);

                final DropDownChoice multiValueTolerance = new DropDownChoice<>(ID_RULE_MV_TOLERANCE,
                        new PropertyModel<MultiValueTolerance>(item.getModelObject(), "multiValueTolerance"),
                        WebMiscUtil.createReadonlyModelFromEnum(MultiValueTolerance.class),
                        new EnumChoiceRenderer<MultiValueTolerance>(this));
                multiValueTolerance.add(new VisibleEnableBehavior(){

                    @Override
                    public boolean isEnabled() {
                        String attributeName = item.getModelObject().getAttributeName();

                        return !(attributeName == null || attributeName.isEmpty()) &&
                                !WebMiscUtil.isOrgAttributeSingleValue(item.getModelObject().getAttributeName());

                    }
                });
                multiValueTolerance.setOutputMarkupId(true);
                ruleBody.add(multiValueTolerance);

                DropDownChoice attributeSelector = new DropDownChoice<>(ID_RULE_ATTRIBUTE, new PropertyModel<String>(item.getModelObject(), "attributeName"),
                        new AbstractReadOnlyModel<List<String>>() {

                            @Override
                            public List<String> getObject() {
                                return WebMiscUtil.createOrgAttributeList();
                            }
                        }, new IChoiceRenderer<String>() {

                    @Override
                    public String getDisplayValue(String object) {
                        return object;
                    }

                    @Override
                    public String getIdValue(String object, int index) {
                        return Integer.toString(index);
                    }
                });
                attributeSelector.setRequired(true);
                attributeSelector.add(new AjaxFormComponentUpdatingBehavior("onchange") {

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        target.add(multiValueTolerance, singleValueTolerance);
                    }
                });
                ruleBody.add(attributeSelector);
            }
        };
        ruleRepeater.setOutputMarkupId(true);
        rulesContainer.add(ruleRepeater);

        initPolicyButtons(policyContainer);
    }

    private void initPolicyButtons(WebMarkupContainer container){
        AjaxSubmitLink savePolicy = new AjaxSubmitLink(ID_BUTTON_SAVE) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                savePolicyPerformed(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(getFeedbackPanel());
            }
        };
        container.add(savePolicy);

        AjaxSubmitLink cancel = new AjaxSubmitLink(ID_BUTTON_CANCEL) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                cancelPerformed(target);
            }
        };
        cancel.setDefaultFormProcessing(false);
        container.add(cancel);
    }

    private IModel<String> createCollapseItemId(final ListItem<FederationSharingRuleType> item, final boolean includeSelector){
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                StringBuilder sb = new StringBuilder();

                if(includeSelector){
                    sb.append("#");
                }

                sb.append("collapse").append(item.getId());

                return sb.toString();
            }
        };
    }

    private String createRuleLabel(FederationSharingRuleType rule){
        StringBuilder sb = new StringBuilder();

        if(rule.getAttributeName() == null){
            return "Create new rule";
        }

        sb.append(rule.getAttributeName());

        if(rule.getSingleValueTolerance() != null){
            sb.append(" - (");
            sb.append(rule.getSingleValueTolerance());
            sb.append(")");
        } else if(rule.getMultiValueTolerance() != null){
            sb.append(" - (");
            sb.append(rule.getMultiValueTolerance());
            sb.append(")");
        }

        return sb.toString();
    }

    private void addPolicyPerformed(AjaxRequestTarget target){
        selected.setObject(new FederationSharingPolicyType());
        target.add(getPolicyForm());
    }

    private void editPolicyPerformed(AjaxRequestTarget target, IModel<FederationSharingPolicyType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            warn("Could not edit sharing policy. Malformed data.");
            target.add(getFeedbackPanel());
            return;
        }

        selected.setObject(rowModel.getObject());
        target.add(getPolicyForm());
    }

    private void removePolicyPerformed(AjaxRequestTarget target, IModel<FederationSharingPolicyType> rowModel){
        if(rowModel == null || rowModel.getObject() == null){
            warn("Could not remove sharing policy. Malformed data.");
            target.add(getFeedbackPanel());
            return;
        }

        FederationSharingPolicyType policy = rowModel.getObject();

        try {
            getModelService().deleteObject(policy);
            success("Sharing policy: '" + policy.getName() + "'(" + policy.getUid() + ") removed.");
            LOGGER.info("Sharing policy: '" + policy.getName() + "'(" + policy.getUid() + ") removed.");
        } catch (ObjectNotFoundException e) {
            LOGGER.error("Could not remove sharing policy with name: '" + policy.getName() + "'. Policy not found in repository.", e);
            error("Could not remove sharing policy with name: '" + policy.getName() + "'. Policy not found in repository. Reason: " + e);
        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Could not remove sharing policy with name: '" + policy.getName() + "'. Repository error.", e);
            error("Could not remove sharing policy with name: '" + policy.getName() + "'. Repository error. Reason: " + e);
        }

        selected.setObject(null);
        target.add(getFeedbackPanel(), getPolicyForm(), getListForm());
    }

    private void deleteRulePerformed(AjaxRequestTarget target, FederationSharingRuleType rule){
        if(rule == null || selected.getObject() == null){
            warn("Could not remove sharing rule. Malformed data.");
            target.add(getFeedbackPanel());
            return;
        }

        selected.getObject().getRules().remove(rule);
        target.add(getRuleContainer());
    }

    private void addRulePerformed(AjaxRequestTarget target){
        if(selected.getObject() == null){
            warn("Could not add sharing rule. Malformed data.");
            target.add(getFeedbackPanel());
            return;
        }

        selected.getObject().getRules().add(new FederationSharingRuleType());
        target.add(getRuleContainer());
    }

    private void cancelPerformed(AjaxRequestTarget target){
        selected.setObject(null);
        target.add(getPolicyForm());
    }

    private void savePolicyPerformed(AjaxRequestTarget target){
        if(selected == null || selected.getObject() == null){
            warn("Could not save policy. Malformed data.");
            target.add(getFeedbackPanel());
            return;
        }

        FederationSharingPolicyType policy = selected.getObject();

        try {
            if(policy.getUid() == null){
                //We are creating new sharing policy

                FederationSharingPolicyType created = getModelService().createObject(policy);
                success("New Federation sharing policy created: '" + created.getName() + "'(" + created.getUid() + ").");
                LOGGER.info("New Federation sharing policy created: '" + created.getName() + "'(" + created.getUid() + ").");
                selected.setObject(null);
                target.add(getFeedbackPanel(), getListForm(), getPolicyForm());

            } else {
                getModelService().updateObject(policy);
                success("Federation sharing policy updated: '" + policy.getName() + "'(" + policy.getUid() + ").");
                LOGGER.info("Federation sharing policy updated: '" + policy.getName() + "'(" + policy.getUid() + ").");
                selected.setObject(null);
                target.add(getFeedbackPanel(), getListForm(), getPolicyForm());
            }

        } catch (ObjectAlreadyExistsException e) {
            LOGGER.error("Could not create a sharing policy with name: '" + policy.getName() + "'. It already exists.", e);
            error("Could not create a sharing policy with name: '" + policy.getName() + "'. It already exists. Reason: " + e);
            target.add(getFeedbackPanel());
        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Could not create a sharing policy with name: '" + policy.getName() + "'. Repository error.", e);
            error("Could not create a sharing policy with name: '" + policy.getName() + "'. Repository error. Reason: " + e);
            target.add(getFeedbackPanel());
        } catch (ObjectNotFoundException e) {
            LOGGER.error("Could not update a sharing policy with name: '" + policy.getName() + "'. Policy does not exist.", e);
            error("Could not update a sharing policy with name: '" + policy.getName() + "'. Policy does not exist. Reason: " + e);
            target.add(getFeedbackPanel());
        }
    }
}
