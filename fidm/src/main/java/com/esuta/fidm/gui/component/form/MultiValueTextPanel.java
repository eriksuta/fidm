package com.esuta.fidm.gui.component.form;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *  @author shood
 * */
public class MultiValueTextPanel<T extends Serializable> extends Panel{

    private static final String ID_INPUT = "input";
    private static final String ID_REPEATER = "repeater";
    private static final String ID_ADD = "add";
    private static final String ID_REMOVE = "remove";
    private static final String ID_BUTTON_CONTAINER = "buttonContainer";

    private static final String CSS_DISABLED = " disabled";

    private IModel<List<T>> model;

    public MultiValueTextPanel(String id, IModel<List<T>> model, boolean required) {
        super(id);
        this.model = model;
        setOutputMarkupId(true);

        initLayout(required);
    }

    protected IModel<List<T>> getModel(){
        return model;
    }

    private void initLayout(final boolean required){
        if(getModel().getObject() == null){
            getModel().setObject(new ArrayList<>(Arrays.asList(createNewEmptyItem())));
        } else if(getModel().getObject().isEmpty()){
            getModel().getObject().add(createNewEmptyItem());
        }


        ListView repeater = new ListView<T>(ID_REPEATER, getModel()) {

            @Override
            protected void populateItem(final ListItem<T> item) {
                TextField input = new TextField<>(ID_INPUT, createTextModel(item.getModel()));
                input.add(new AjaxFormComponentUpdatingBehavior("onblur") {

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                    }
                });
                input.add(AttributeAppender.replace("placeholder", createEmptyItemPlaceholder()));
                input.setRequired(required);
                item.add(input);

                WebMarkupContainer buttonGroup = new WebMarkupContainer(ID_BUTTON_CONTAINER);
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
        add.add(new AttributeAppender("class", getPlusClassModifier(item)));
        buttonGroup.add(add);

        AjaxLink remove = new AjaxLink(ID_REMOVE) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                removeValuePerformed(target, item);
            }
        };
        remove.add(new AttributeAppender("class", getMinusClassModifier()));
        buttonGroup.add(remove);
    }

    protected String getPlusClassModifier(ListItem<T> item){
        int size = getModel().getObject().size();
        if (size <= 1) {
            return "";
        }
        if (item.getIndex() == size - 1) {
            return "";
        }

        return CSS_DISABLED;
    }

    protected String getMinusClassModifier(){
        int size = getModel().getObject().size();
        if (size > 1) {
            return "";
        }

        return CSS_DISABLED;
    }

    protected void addValuePerformed(AjaxRequestTarget target, ListItem<T> item) {
        List<T> objects = getModel().getObject();
        objects.add(createNewEmptyItem());

        target.add(this);
    }

    protected T createNewEmptyItem() {
        return (T)"";
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

    protected IModel<String> createEmptyItemPlaceholder(){
        return new Model<>("Set value");
    }
}
