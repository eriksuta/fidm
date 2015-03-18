package com.esuta.fidm.gui.component.data;

import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.model.federation.client.ObjectInformationResponse;
import com.esuta.fidm.model.federation.service.ObjectInformation;

import com.esuta.fidm.repository.schema.core.FederationMemberType;
import com.esuta.fidm.repository.schema.support.FederationIdentifierType;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.eclipse.jetty.http.HttpStatus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *  TODO - unite common methods with ObjectDataProvider class - make a superclass - refactor
 *
 *  A data provider that retrieve a set of information objects about
 *  federation objects using REST Api.
 *
 *  @author shood
 * */
public class FederationObjectInformationProvider extends SortableDataProvider<ObjectInformation, String>{

    private static final Logger LOGGER = Logger.getLogger(FederationObjectInformationProvider.class);

    private Component component;
    private List<FederationIdentifierType> objectIdentifierList;
    private List<ObjectInformation> data;
    private List<ObjectInformation> currentPageData;

    public FederationObjectInformationProvider(Component component, List<FederationIdentifierType> identifiers){
        this(component, identifiers, true);
    }

    public FederationObjectInformationProvider(Component component, List<FederationIdentifierType> identifiers,
                                               boolean useDefaultSort){
        if(component == null || identifiers == null){
            return;
        }

        this.component = component;
        this.objectIdentifierList = identifiers;

        if(useDefaultSort){
            setSort("name", SortOrder.ASCENDING);
        }
    }

    public List<ObjectInformation> getData(){
        if(data == null){
            data = new ArrayList<>();
        }

        return data;
    }

    public List<ObjectInformation> getCurrentPageData(){
        if(currentPageData == null){
            currentPageData = new ArrayList<>();
        }

        return currentPageData;
    }

    protected PageBase getPageBase(){
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
    public Iterator<? extends ObjectInformation> iterator(long first, long count) {
        getData().clear();
        getCurrentPageData().clear();
        List<ObjectInformation> dataList = new ArrayList<>();
        List<ObjectInformation> filteredDataList;

        try {
            dataList.addAll(retrieveObjectInformationList());
            filteredDataList = applyDataFilter(dataList);

            for(ObjectInformation o: filteredDataList){
                getData().add(o);
            }

            getCurrentPageData().addAll(getData().subList((int)first, (int)(first + count)));
        } catch (Exception e){
            LOGGER.error("Could not retrieve information about federation objects. Reason: ", e);
        }

        return getCurrentPageData().iterator();
    }

    /**
     *  Override to provide a custom filter
     * */
    public List<ObjectInformation> applyDataFilter(List<ObjectInformation> list){
        return list;
    }

    @Override
    public long size() {
        long count = 0;

        try {
            List<ObjectInformation> dataList = retrieveObjectInformationList();
            List<ObjectInformation> filteredDataList = applyDataFilter(dataList);
            count = filteredDataList.size();
        } catch (Exception e){
            LOGGER.error("Could not retrieve the size of information federation objects. Reason: ", e);
        }

        return count;
    }

    private List<ObjectInformation> retrieveObjectInformationList() throws DatabaseCommunicationException {
        List<ObjectInformation> dataList = new ArrayList<>();

        for(FederationIdentifierType identifier: objectIdentifierList){
            FederationMemberType federationMember = getPageBase().getFederationMemberByName(identifier.getFederationMemberId());
            ObjectInformationResponse response = getPageBase().getFederationServiceClient().createGetObjectInformationRequest(federationMember, identifier);

            if(HttpStatus.OK_200 == response.getStatus()){
                dataList.add(response.getInformationObject());
            } else {
                LOGGER.error("Could not retrieve information about object. Response: " + response.getMessage());
            }
        }

        return dataList;
    }

    @Override
    public IModel<ObjectInformation> model(ObjectInformation object) {
        return new Model<>(object);
    }
}
