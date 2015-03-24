package com.esuta.fidm.gui.page.org;

import com.esuta.fidm.gui.component.WebMiscUtil;
import com.esuta.fidm.gui.component.data.ObjectDataProvider;
import com.esuta.fidm.gui.component.data.column.EditDeleteButtonColumn;
import com.esuta.fidm.gui.component.data.table.TablePanel;
import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.repository.schema.core.FederationSharingPolicyType;
import com.esuta.fidm.repository.schema.core.FederationSharingRuleType;
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
public class PageSharingPolicy extends PageBase{

    private static final Logger LOGGER = Logger.getLogger(PageSharingPolicy.class);

    private static final String ID_LIST_FORM = "listForm";
    private static final String ID_POLICY_TABLE = "sharingPolicyTable";

    private static final String ID_POLICY_FORM = "policyForm";
    private static final String ID_POLICY_CONTAINER = "policyContainer";
    private static final String ID_NAME = "name";
    private static final String ID_DISPLAY_NAME = "displayName";
    private static final String ID_DESCRIPTION = "description";
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
        ObjectDataProvider<FederationSharingPolicyType> provider = new ObjectDataProvider<>(getPage(), FederationSharingPolicyType.class);
        List<IColumn> columns = createSharingPolicyColumns();

        TablePanel table = new TablePanel(ID_POLICY_TABLE, provider, columns, 10);
        table.setOutputMarkupId(true);
        form.add(table);
    }

    private List<IColumn> createSharingPolicyColumns(){
        List<IColumn> columns = new ArrayList<>();

        columns.add(new PropertyColumn<FederationSharingPolicyType, String>(new Model<>("Name"), "name", "name"));
        columns.add(new PropertyColumn<FederationSharingPolicyType, String>(new Model<>("DisplayName"), "displayName", "displayName"));
        columns.add(new AbstractColumn<FederationSharingPolicyType, String>(new Model<>("Rules")) {

            @Override
            public void populateItem(Item<ICellPopulator<FederationSharingPolicyType>> cellItem, String componentId, final IModel<FederationSharingPolicyType> rowModel) {
                add(new Label(componentId, new AbstractReadOnlyModel<String>() {

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
        form.add(policyContainer);

        TextField policyName = new TextField<>(ID_NAME, new PropertyModel<String>(selected, "name"));
        policyName.setRequired(true);
        policyContainer.add(policyName);

        TextField policyDisplayName = new TextField<>(ID_DISPLAY_NAME, new PropertyModel<String>(selected, "displayName"));
        policyDisplayName.setRequired(true);
        policyContainer.add(policyDisplayName);

        TextArea policyDescription = new TextArea<>(ID_DESCRIPTION, new PropertyModel<String>(selected, "description"));
        policyContainer.add(policyDescription);

        WebMarkupContainer rulesContainer = new WebMarkupContainer(ID_RULES_CONTAINER);
        rulesContainer.setOutputMarkupId(true);
        policyContainer.add(rulesContainer);

        AjaxLink addRule = new AjaxLink(ID_BUTTON_ADD_RULE) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                addRulePerformed(target);
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
                ruleBody.setMarkupId(createCollapseItemId(item, false).getObject());

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

                DropDownChoice singleValueTolerance = new DropDownChoice<>(ID_RULE_SV_TOLERANCE,
                        new PropertyModel<FederationSharingRuleType.SingleValueTolerance>(item.getModelObject(), "singleValueTolerance"),
                        WebMiscUtil.createReadonlyModelFromEnum(FederationSharingRuleType.SingleValueTolerance.class),
                        new EnumChoiceRenderer<FederationSharingRuleType.SingleValueTolerance>(this));
                ruleBody.add(singleValueTolerance);

                DropDownChoice multiValueTolerance = new DropDownChoice<>(ID_RULE_MV_TOLERANCE,
                        new PropertyModel<FederationSharingRuleType.MultiValueTolerance>(item.getModelObject(), "multiValueTolerance"),
                        WebMiscUtil.createReadonlyModelFromEnum(FederationSharingRuleType.MultiValueTolerance.class),
                        new EnumChoiceRenderer<FederationSharingRuleType.MultiValueTolerance>(this));
                ruleBody.add(multiValueTolerance);
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
//        TODO
        return rule.getAttributeName();
    }

    private List<String> createOrgAttributeList(){
//        TODO
        return new ArrayList<>();
    }

    private void editPolicyPerformed(AjaxRequestTarget target, IModel<FederationSharingPolicyType> rowModel){
//        TODO
        warn("Not implemented yet.");
        target.add(getFeedbackPanel());
    }

    private void removePolicyPerformed(AjaxRequestTarget target, IModel<FederationSharingPolicyType> rowModel){
//        TODO
        warn("Not implemented yet.");
        target.add(getFeedbackPanel());
    }

    private void deleteRulePerformed(AjaxRequestTarget target, FederationSharingRuleType rowModel){
//        TODO
        warn("Not implemented yet.");
        target.add(getFeedbackPanel());
    }

    private void addRulePerformed(AjaxRequestTarget target){
//       TODO
        warn("Not implemented yet.");
        target.add(getFeedbackPanel());
    }

    private void cancelPerformed(AjaxRequestTarget target){
     //       TODO
        warn("Not implemented yet.");
        target.add(getFeedbackPanel());
    }

    private void savePolicyPerformed(AjaxRequestTarget target){
//       TODO
        warn("Not implemented yet.");
        target.add(getFeedbackPanel());
    }
}
