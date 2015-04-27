package com.esuta.fidm.gui.component.modal;

import com.esuta.fidm.gui.component.WebMiscUtil;
import com.esuta.fidm.gui.component.model.LoadableModel;
import com.esuta.fidm.repository.schema.core.SharingPolicyType;
import com.esuta.fidm.repository.schema.core.SharingRuleType;
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

    private static final String ID_DEFAULT_SV_TOLERANCE = "defaultSVTolerance";
    private static final String ID_DEFAULT_MV_TOLERANCE = "defaultMVTolerance";
    private static final String ID_RULE_REPEATER = "ruleRepeater";
    private static final String ID_ATTRIBUTE_LABEL = "attributeLabel";
    private static final String ID_RULE_DESCRIPTION = "ruleDescription";
    private static final String ID_BUTTON_CONTINUE = "continueButton";

    private boolean initialized;
    private SharingPolicyType policy;
    private IModel<List<SharingRuleType>> model;

    public SharingPolicyViewerDialog(String id, final IModel<List<SharingRuleType>> model,
                                     SharingPolicyType policy) {
        super(id);

        this.policy = policy;
        this.model = new LoadableModel<List<SharingRuleType>>(false) {

            @Override
            protected List<SharingRuleType> load() {
                return model == null ? new ArrayList<SharingRuleType>() : model.getObject();
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

    public void updateModel(List<SharingRuleType> rules, SharingPolicyType policy){
        this.policy = policy;
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
        Label defaultSingleValueTolerance = new Label(ID_DEFAULT_SV_TOLERANCE, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return policy.getDefaultSingleValueTolerance().toString();
            }
        });
        content.add(defaultSingleValueTolerance);

        Label defaultMultiValueTolerance = new Label(ID_DEFAULT_MV_TOLERANCE, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return policy.getDefaultMultiValueTolerance().toString();
            }
        });
        content.add(defaultMultiValueTolerance);

        ListView ruleRepeater = new ListView<SharingRuleType>(ID_RULE_REPEATER, model) {

            @Override
            protected void populateItem(final ListItem<SharingRuleType> item) {
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
