package com.esuta.fidm.gui.component.model;

import org.apache.wicket.model.IModel;

/**
 *  @author shood
 *
 *  This IModel implementation is loosely based on LoadableModel
 *  class from Evolveum/MidPoint (https://github.com/Evolveum/midpoint/blob/a6c023945dbea34db69a8ff17c9a61b7184c42cc/gui/admin-gui/src/main/java/com/evolveum/midpoint/web/component/util/LoadableModel.java)
 *  Autor of class: Viliam Repan
 * */
public abstract class LoadableModel<T> implements IModel<T> {

    private T object;
    private boolean loaded = false;
    private boolean alwaysReload;

    public LoadableModel(){
        this(null, true);
    }

    public LoadableModel(boolean alwaysReload){
        this(null, alwaysReload);
    }

    public LoadableModel(T object){
        this(object, true);
    }

    public LoadableModel(T object, boolean alwaysReload){
        this.object = object;
        this.alwaysReload = alwaysReload;
    }

    @Override
    public T getObject() {
        if(!loaded){
            setObject(load());
            onLoad();
            this.loaded = true;
        }

        if(object instanceof IModel){
            IModel model = (IModel) object;
            return (T)model.getObject();
        }

        return object;
    }

    @Override
    public void setObject(T object) {
        if(this.object instanceof IModel){
            ((IModel<T>)this.object).setObject(object);
        } else {
            this.object = object;
        }

        this.loaded = true;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void reset(){
        loaded = false;
    }

    @Override
    public void detach() {
        if(loaded && alwaysReload){
            this.loaded = false;
            onDetach();
        }
    }

    protected abstract T load();

    protected void onLoad(){}

    protected void onDetach(){}
}
