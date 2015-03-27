package com.esuta.fidm.gui.component.form;

import com.esuta.fidm.gui.component.behavior.VisibleEnableBehavior;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *  @author shood
 * */
public class MultiValueTextEditPanel<T extends Serializable> extends Panel {

    private static final String ID_REPEATER = "repeater";
    private static final String ID_TEXT = "input";
    private static final String ID_BUTTON_GROUP = "buttonGroup";
    private static final String ID_ADD = "add";
    private static final String ID_REMOVE = "delete";
    private static final String ID_EDIT = "edit";
    private static final String ID_ADD_IF_EMPTY = "addIfEmpty";

    private static final String CSS_DISABLED = " disabled";

    private IModel<List<T>> model;

    public MultiValueTextEditPanel(String id, IModel<List<T>> model, boolean inputEnabled){
        super(id);
        this.model = model;
        setOutputMarkupId(true);

        initLayout(inputEnabled);
    }

    public IModel<List<T>> getModel(){
        return model;
    }

    private void initLayout(final boolean inputEnabled){
        AjaxLink addIfEmpty = new AjaxLink(ID_ADD_IF_EMPTY) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                addFirstPerformed(target);
            }
        };
        addIfEmpty.add(new VisibleEnableBehavior(){

            @Override
            public boolean isVisible() {
                return getModel() == null || getModel().getObject() == null || getModel().getObject().isEmpty();
            }
        });
        addIfEmpty.add(new AttributeAppender("class", new AbstractReadOnlyModel<String>(){

            @Override
            public String getObject() {
                return isAddDisabled() ? CSS_DISABLED : null;
            }
        }));
        add(addIfEmpty);

        ListView repeater = new ListView<T>(ID_REPEATER, getModel()){

            @Override
            protected void populateItem(final ListItem<T> item) {
                TextField text = new TextField<>(ID_TEXT, createTextModel(item.getModel()));
                text.add(new AjaxFormComponentUpdatingBehavior("onblur") {

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {}
                });
                text.add(AttributeAppender.replace("placeholder", createEmptyItemPlaceholder()));

                if(!inputEnabled){
                    text.add(new AttributeModifier("disabled","disabled"));
                }
                item.add(text);

                WebMarkupContainer buttonGroup = new WebMarkupContainer(ID_BUTTON_GROUP);
                item.add(buttonGroup);
                initButtons(buttonGroup, item);
            }
        };
        repeater.add(new VisibleEnableBehavior(){

            @Override
            public boolean isVisible() {
                return getModel() != null && getModel().getObject() != null && !getModel().getObject().isEmpty();
            }
        });
        add(repeater);
    }

    private void initButtons(WebMarkupContainer buttonGroup, final ListItem<T> item) {
        AjaxLink edit = new AjaxLink(ID_EDIT) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                editPerformed(target, item.getModelObject());
            }
        };
        edit.add(new AttributeAppender("class", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                if(buttonsDisabled()){
                    return " disabled";
                }
                return null;
            }
        }));
        buttonGroup.add(edit);

        AjaxLink add = new AjaxLink(ID_ADD) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                addValuePerformed(target);
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
        if(isAddDisabled()){
            return CSS_DISABLED;
        }

        if(buttonsDisabled()){
            return CSS_DISABLED;
        }

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
        if(isMinusDisabled()){
            return CSS_DISABLED;
        }

        int size = getModel().getObject().size();
        if (size > 0) {
            return "";
        }

        return CSS_DISABLED;
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

    protected void addFirstPerformed(AjaxRequestTarget target){
        if(getModel().getObject() == null){
            getModel().setObject(new ArrayList<T>());
        }

        addValuePerformed(target);
    }

    protected void addValuePerformed(AjaxRequestTarget target){
        List<T> objects = getModel().getObject();
        objects.add(createNewEmptyItem());

        performAddValueHook(target);
        target.add(this);
    }

    protected void removeValuePerformed(AjaxRequestTarget target, ListItem<T> item){
        List<T> objects = getModel().getObject();
        Iterator<T> iterator = objects.iterator();
        while (iterator.hasNext()) {
            T object = iterator.next();
            if (object.equals(item.getModelObject())) {
                iterator.remove();
                break;
            }
        }

        performRemoveValueHook(target, item);
        target.add(this);
    }

    /**
     *  Override to provide a capability to create an empty item
     * */
    protected T createNewEmptyItem(){
        return null;
    }

    /**
     *  Override to provide a placeholder IModel to the input field
     * */
    protected IModel<String> createEmptyItemPlaceholder(){
        return new Model<>("Set value");
    }

    /**
     *  Override to tell if the buttons should be disabled
     * */
    protected boolean buttonsDisabled(){
        return false;
    }

    /**
     *  Override to provide functionality for item edit event
     * */
    protected void editPerformed(AjaxRequestTarget target, T object){}

    /**
     *  Override to provide custom hook when adding new value
     * */
    protected void performAddValueHook(AjaxRequestTarget target){}

    /**
     *  Override to provide custom hook when removing value from list
     * */
    protected void performRemoveValueHook(AjaxRequestTarget target, ListItem<T> item){}

    /**
     *  Override to provide additional enabled/disabled behavior for
     *  plus (add) button
     * */
    protected boolean isAddDisabled(){
        return false;
    }

    /**
     *  Override to provide additional enabled/disabled behavior for
     *  minus (remove) button
     * */
    protected boolean isMinusDisabled(){
        return false;
    }
}
