package com.esuta.fidm.gui.component.form;

import com.esuta.fidm.gui.component.behavior.VisibleEnableBehavior;
import com.esuta.fidm.gui.component.model.LoadableModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *  @author shood
 * */
public class MultiValueTextPanel<T extends Serializable> extends Panel{

    private static final String ID_INPUT = "input";
    private static final String ID_INPUT_CONTAINER = "inputContainer";
    private static final String ID_FEEDBACK = "feedback";
    private static final String ID_REPEATER = "repeater";
    private static final String ID_ADD = "add";
    private static final String ID_REMOVE = "remove";
    private static final String ID_BUTTON_CONTAINER = "buttonContainer";

    private static final String CLASS_MULTI_VALUE = "multivalue-form";

    private IModel<List<T>> model;
    private boolean prepareIfEmpty;

    public MultiValueTextPanel(String id, IModel<List<T>> model, String inputSize, boolean required) {
        this(id, model, inputSize, required, false);
    }

    public MultiValueTextPanel(String id, IModel<List<T>> model, String inputSize, boolean required, boolean prepareIfEmpty) {
        super(id);
        this.model = model;
        this.prepareIfEmpty = prepareIfEmpty;
        setOutputMarkupId(true);

        initLayout(inputSize, required);
    }

    protected IModel<List<T>> getModel(){
        return model;
    }

    protected IModel<List<T>> getPreparedModel(){
        if(model == null){
            model = new LoadableModel<List<T>>(false) {

                @Override
                protected List<T> load() {
                    return new ArrayList<>();
                }
            };
        }

        if(getModel().getObject().isEmpty() && prepareIfEmpty){
            getModel().getObject().add(createNewEmptyItem());
        }

        return model;
    }

    private void initLayout(final String inputSize, final boolean required){
        ListView repeater = new ListView<T>(ID_REPEATER, getPreparedModel()) {

            @Override
            protected void populateItem(final ListItem<T> item) {
                WebMarkupContainer inputContainer = new WebMarkupContainer(ID_INPUT_CONTAINER);
                inputContainer.add(AttributeAppender.prepend("class", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        StringBuilder sb = new StringBuilder();
                        if (StringUtils.isNotEmpty(inputSize)) {
                            sb.append(inputSize).append(' ');
                        }
                        if (item.getIndex() > 0 && StringUtils.isNotEmpty(getOffsetClass())) {
                            sb.append(getOffsetClass()).append(' ');
                            sb.append(CLASS_MULTI_VALUE);
                        }

                        return sb.toString();
                    }
                }));
                item.add(inputContainer);

                TextField input = new TextField<>(ID_INPUT, createTextModel(item.getModel()));
                input.add(new AjaxFormComponentUpdatingBehavior("onblur") {

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                    }
                });
                input.setRequired(required);
                inputContainer.add(input);

                FeedbackPanel feedback = new FeedbackPanel(ID_FEEDBACK, new ComponentFeedbackMessageFilter(input));
                inputContainer.add(feedback);

                WebMarkupContainer buttonGroup = new WebMarkupContainer(ID_BUTTON_CONTAINER);
                buttonGroup.add(AttributeAppender.append("class", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        if (item.getIndex() > 0 && StringUtils.isNotEmpty(getOffsetClass())) {
                            return CLASS_MULTI_VALUE;
                        }

                        return null;
                    }
                }));
                item.add(buttonGroup);

                initButtons(buttonGroup, item);
            }
        };
        add(repeater);
    }

    private void initButtons(WebMarkupContainer buttonGroup, final ListItem<T> item) {
        AjaxLink add = new AjaxLink(ID_ADD) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                addValuePerformed(target, item);
            }
        };
        add.add(new VisibleEnableBehavior() {

            @Override
            public boolean isVisible() {
                return isAddButtonVisible(item);
            }
        });
        buttonGroup.add(add);

        AjaxLink remove = new AjaxLink(ID_REMOVE) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                removeValuePerformed(target, item);
            }
        };
        remove.add(new VisibleEnableBehavior() {

            @Override
            public boolean isVisible() {
                return isRemoveButtonVisible(item);
            }
        });
        buttonGroup.add(remove);
    }

    protected boolean isAddButtonVisible(ListItem<T> item) {
        int size = getModel().getObject().size();

        if (size <= 1) {
            return true;
        }

        if (item.getIndex() == size - 1) {
            return true;
        }

        return false;
    }

    protected boolean isRemoveButtonVisible(ListItem<T> item) {
        int size = getModel().getObject().size();

        if (size > 1) {
            return true;
        }

        return false;
    }

    protected void addValuePerformed(AjaxRequestTarget target, ListItem<T> item) {
        List<T> objects = getModel().getObject();
        objects.add(createNewEmptyItem());

        target.add(this);
    }

    protected T createNewEmptyItem() {
        return null;
    }

    protected void removeValuePerformed(AjaxRequestTarget target, ListItem<T> item) {
        List<T> objects = getModel().getObject();
        Iterator<T> iterator = objects.iterator();

        while (iterator.hasNext()) {
            T object = iterator.next();
            if (object.equals(item.getModelObject())) {
                iterator.remove();
                break;
            }
        }

        if(getModel().getObject().isEmpty()){
            getModel().getObject().add(createNewEmptyItem());
        }

        target.add(this);
    }

    protected IModel<String> createTextModel(final IModel<T> model) {

        return new IModel<String>() {
            @Override
            public String getObject() {
                T obj = model.getObject();
                return obj != null ? obj.toString() : null;
            }

            @Override
            public void setObject(String object) {
                model.setObject((T) object);
            }

            @Override
            public void detach() {
            }
        };
    }

    protected String getOffsetClass() {
        return "col-md-offset-4";
    }
}
