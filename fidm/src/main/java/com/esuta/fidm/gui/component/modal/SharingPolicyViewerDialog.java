package com.esuta.fidm.gui.component.modal;

import com.esuta.fidm.gui.component.WebMiscUtil;
import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.repository.schema.core.FederationSharingRuleType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 * */
public class SharingPolicyViewerDialog extends ModalWindow {

    private static final String ID_RULE_REPEATER = "ruleRepeater";
    private static final String ID_ATTRIBUTE_LABEL = "attributeLabel";
    private static final String ID_RULE_DESCRIPTION = "ruleDescription";
    private static final String ID_BUTTON_CONTINUE = "continueButton";

    private boolean initialized;
    private IModel<List<FederationSharingRuleType>> model;

    public SharingPolicyViewerDialog(String id, final IModel<List<FederationSharingRuleType>> model) {
        super(id);

        this.model = new LoadableModel<List<FederationSharingRuleType>>(false) {

            @Override
            protected List<FederationSharingRuleType> load() {
                return model == null ? new ArrayList<FederationSharingRuleType>() : model.getObject();
            }
        };

        setTitle("View Sharing Rules");
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

    public void updateModel(List<FederationSharingRuleType> rules){
        model.setObject(rules);
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
        ListView ruleRepeater = new ListView<FederationSharingRuleType>(ID_RULE_REPEATER, model) {

            @Override
            protected void populateItem(final ListItem<FederationSharingRuleType> item) {
                final String attributeName = item.getModelObject().getAttributeName();

                Label attributeLabel = new Label(ID_ATTRIBUTE_LABEL, new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return attributeName;
                    }
                });
                item.add(attributeLabel);

                Label ruleLabel = new Label(ID_RULE_DESCRIPTION, new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        StringBuilder sb = new StringBuilder();

                        if(WebMiscUtil.isOrgAttributeSingleValue(attributeName)){
                            sb.append(item.getModelObject().getSingleValueTolerance());
                            sb.append(" (");
                            sb.append(WebMiscUtil.getSingleValueSharingRuleDescription(item.getModelObject().getSingleValueTolerance()));
                            sb.append(")");
                        } else {
                            sb.append(item.getModelObject().getMultiValueTolerance());
                            sb.append(" (");
                            sb.append(WebMiscUtil.getMultiValueSharingRuleDescription(item.getModelObject().getMultiValueTolerance()));
                            sb.append(")");
                        }

                        return sb.toString();
                    }
                });
                item.add(ruleLabel);
            }
        };
        content.add(ruleRepeater);

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
