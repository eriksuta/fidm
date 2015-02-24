package com.esuta.fidm.gui.component.data;

import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.repository.schema.core.ObjectType;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *  @author shood
 * */
public class ObjectDataProvider<T extends ObjectType> extends SortableDataProvider<T, String>{

    private transient Logger LOGGER = Logger.getLogger(ObjectDataProvider.class);

    private Component component;
    private Class<T> type;
    private List<T> data;
    private List<T> currentPageData;

    public ObjectDataProvider(Component component, Class<T> type){
        this(component, type, true);
    }

    public ObjectDataProvider(Component component, Class<T> type, boolean useDefaultSort){
        if(component == null || type == null){
            return;
        }

        this.component = component;
        this.type = type;

        if(useDefaultSort){
            setSort("name", SortOrder.ASCENDING);
        }
    }

    public List<T> getData(){
        if(data == null){
            data = new ArrayList<>();
        }

        return data;
    }

    public List<T> getCurrentPageData(){
        if(currentPageData == null){
            currentPageData = new ArrayList<>();
        }

        return currentPageData;
    }

    private PageBase getPageBase(){
        if(component instanceof PageBase){
            return (PageBase) component;
        }

        if(component.getPage() instanceof PageBase){
            return (PageBase) component.getPage();
        }

        throw new IllegalStateException("Provided component is nor an instance of 'PageBase' neither is it placed on page of that instance.");
    }

    protected ModelService getModelService() throws IllegalStateException{
        return getPageBase().getModelService();
    }

    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        getData().clear();
        getCurrentPageData().clear();
        List<T> dataList;
        List<T> filteredDataList;

        try {
            dataList = getModelService().getAllObjectsOfType(type);
            filteredDataList = applyDataFilter(dataList);

            for(T o: filteredDataList){
                getData().add(o);
            }

            getCurrentPageData().addAll(getData().subList((int)first, (int)(first + count)));
        } catch (Exception e){
            LOGGER.error("Could not create an iterator object for data of type: '" + type.getSimpleName() + "'. Could not read objects from model.");
        }

        return getCurrentPageData().iterator();
    }

    /**
     *  Override to provide a custom filter
     * */
    public List<T> applyDataFilter(List<T> list){
        return list;
    }

    @Override
    public long size() {
        long count = 0;

        try {
            List<T> dataList = getModelService().getAllObjectsOfType(type);
            List<T> filteredDataList = applyDataFilter(dataList);
            count = filteredDataList.size();
        } catch (Exception e){
            LOGGER.error("Couldn't count the number of objects of type '" + type.getSimpleName() + "' in the model. Reason: ", e);
        }

        return count;
    }

    @Override
    public IModel<T> model(T object) {
        return new Model<>(object);
    }

    public Class<T> getType() {
        return type;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }
}
