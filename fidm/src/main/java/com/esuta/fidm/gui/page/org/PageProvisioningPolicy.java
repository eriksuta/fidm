package com.esuta.fidm.gui.page.org;

import com.esuta.fidm.gui.component.WebMiscUtil;
import com.esuta.fidm.gui.component.behavior.VisibleEnableBehavior;
import com.esuta.fidm.gui.component.data.ObjectDataProvider;
import com.esuta.fidm.gui.component.data.column.EditDeleteButtonColumn;
import com.esuta.fidm.gui.component.data.table.TablePanel;
import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.repository.schema.core.FederationProvisioningPolicyType;
import com.esuta.fidm.repository.schema.core.FederationProvisioningRuleType;
import com.esuta.fidm.repository.schema.core.ModificationType;
import com.esuta.fidm.repository.schema.core.ProvisioningBehaviorType;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
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
public class PageProvisioningPolicy extends PageBase{

    private static final Logger LOGGER = Logger.getLogger(PageProvisioningPolicy.class);

    private static final String ID_LIST_FORM = "listForm";
    private static final String ID_POLICY_TABLE = "provisioningPolicyTable";
    private static final String ID_BUTTON_ADD_POLICY = "addPolicyButton";

    private static final String ID_POLICY_FORM = "policyForm";
    private static final String ID_POLICY_CONTAINER = "policyContainer";
    private static final String ID_POLICY_LABEL = "policyLabel";
    private static final String ID_NAME = "name";
    private static final String ID_DISPLAY_NAME = "displayName";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_DEFAULT_BEHAVIOR = "defaultBehavior";

    private static final String ID_RULES_CONTAINER = "rulesContainer";
    private static final String ID_BUTTON_ADD_RULE = "addRuleButton";
    private static final String ID_REPEATER = "repeater";
    private static final String ID_RULE_HEADER = "ruleHeader";
    private static final String ID_RULE_DELETE = "ruleDelete";
    private static final String ID_RULE_LABEL = "ruleLabel";
    private static final String ID_RULE_BODY_CONTAINER = "ruleBodyContainer";
    private static final String ID_RULE_ATTRIBUTE = "ruleAttributeSelect";
    private static final String ID_RULE_CHANGE = "ruleChangeType";
    private static final String ID_RULE_BEHAVIOR = "ruleBehavior";

    private static final String ID_BUTTON_SAVE = "saveButton";
    private static final String ID_BUTTON_CANCEL = "cancelButton";

    private IModel<FederationProvisioningPolicyType> selected;

    public PageProvisioningPolicy(){
        selected = new LoadableModel<FederationProvisioningPolicyType>(false) {

            @Override
            protected FederationProvisioningPolicyType load() {
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

    private void initPolicyList(Form form){
        ObjectDataProvider<FederationProvisioningPolicyType> provider = new ObjectDataProvider<>(getPage(),
                FederationProvisioningPolicyType.class);
        List<IColumn> columns = createProvisioningPolicyColumns();

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

    private List<IColumn> createProvisioningPolicyColumns(){
        List<IColumn> columns = new ArrayList<>();

        columns.add(new PropertyColumn<FederationProvisioningPolicyType, String>(new Model<>("Name"), "name", "name"));
        columns.add(new PropertyColumn<FederationProvisioningPolicyType, String>(new Model<>("DisplayName"), "displayName", "displayName"));
        columns.add(new AbstractColumn<FederationProvisioningPolicyType, String>(new Model<>("Rules")) {

            @Override
            public void populateItem(Item<ICellPopulator<FederationProvisioningPolicyType>> cellItem, String componentId, final IModel<FederationProvisioningPolicyType> rowModel) {
                cellItem.add(new Label(componentId, new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return Integer.toString(rowModel.getObject().getRules().size());
                    }
                }));
            }
        });
        columns.add(new EditDeleteButtonColumn<FederationProvisioningPolicyType>(new Model<>("Actions")){

            @Override
            public void editPerformed(AjaxRequestTarget target, IModel<FederationProvisioningPolicyType> rowModel) {
                PageProvisioningPolicy.this.editPolicyPerformed(target, rowModel);
            }

            @Override
            public void removePerformed(AjaxRequestTarget target, IModel<FederationProvisioningPolicyType> rowModel) {
                PageProvisioningPolicy.this.removePolicyPerformed(target, rowModel);
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

        DropDownChoice defaultBehavior = new DropDownChoice<>(ID_DEFAULT_BEHAVIOR,
                new PropertyModel<ProvisioningBehaviorType>(selected, "defaultRule"),
                WebMiscUtil.createReadonlyModelFromEnum(ProvisioningBehaviorType.class),
                new EnumChoiceRenderer<ProvisioningBehaviorType>(this));
        defaultBehavior.setRequired(true);
        policyContainer.add(defaultBehavior);

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

        ListView ruleRepeater = new ListView<FederationProvisioningRuleType>(ID_REPEATER,
                new PropertyModel<List<FederationProvisioningPolicyType>>(selected, "rules")) {

            @Override
            protected void populateItem(final ListItem<FederationProvisioningRuleType> item) {
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

                AjaxLink delete = new AjaxLink(ID_RULE_DELETE) {

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

                DropDownChoice attributeSelector = new DropDownChoice<>(ID_RULE_ATTRIBUTE, new PropertyModel<String>(item.getModelObject(), "attributeName"),
                        new AbstractReadOnlyModel<List<String>>() {

                            @Override
                            public List<String> getObject() {
                                return createOrgAttributeList();
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
                ruleBody.add(attributeSelector);

                final DropDownChoice changeType = new DropDownChoice<>(ID_RULE_CHANGE,
                        new PropertyModel<ModificationType>(item.getModelObject(), "modificationType"),
                        WebMiscUtil.createReadonlyModelFromEnum(ModificationType.class),
                        new EnumChoiceRenderer<ModificationType>(this));
                ruleBody.add(changeType);

                final DropDownChoice provisioningType = new DropDownChoice<>(ID_RULE_BEHAVIOR,
                        new PropertyModel<ProvisioningBehaviorType>(item.getModelObject(), "provisioningType"),
                        WebMiscUtil.createReadonlyModelFromEnum(ProvisioningBehaviorType.class),
                        new EnumChoiceRenderer<ProvisioningBehaviorType>(this));
                ruleBody.add(provisioningType);
            }
        };
        ruleRepeater.setOutputMarkupId(true);
        rulesContainer.add(ruleRepeater);

        initPolicyButtons(policyContainer);
    }

    private IModel<String> createCollapseItemId(final ListItem<FederationProvisioningRuleType> item, final boolean includeSelector){
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

    private void addPolicyPerformed(AjaxRequestTarget target){
//      TODO
        warn("Not implemented yet.");
        target.add(getFeedbackPanel());
    }

    private void editPolicyPerformed(AjaxRequestTarget target, IModel<FederationProvisioningPolicyType> rowModel){
//      TODO
        warn("Not implemented yet.");
        target.add(getFeedbackPanel());
    }

    private void removePolicyPerformed(AjaxRequestTarget target, IModel<FederationProvisioningPolicyType> rowModel){
//      TODO
        warn("Not implemented yet.");
        target.add(getFeedbackPanel());
    }

    private void addRulePerformed(AjaxRequestTarget target){
        //      TODO
        warn("Not implemented yet.");
        target.add(getFeedbackPanel());
    }
}
