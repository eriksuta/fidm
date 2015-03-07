package com.esuta.fidm.gui.component.data;

import com.esuta.fidm.model.federation.client.GenericListRestResponse;
import com.esuta.fidm.model.federation.client.IntegerRestResponse;
import com.esuta.fidm.model.federation.client.RestFederationServiceClient;
import com.esuta.fidm.repository.schema.core.FederationMemberType;
import com.esuta.fidm.repository.schema.core.OrgType;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.eclipse.jetty.http.HttpStatus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *  This implementation of ObjectDataProvider returns values by using rest federation
 *  API to retrieve provided org. units from specific federation member. It does not
 *  work with local repository.
 *
 *  @author shood
 * */
public class ProvidedOrgDataProvider extends ObjectDataProvider<OrgType>{

    private transient Logger LOGGER = Logger.getLogger(ProvidedOrgDataProvider.class);

    private FederationMemberType federationMember;

    public ProvidedOrgDataProvider(Component component, FederationMemberType member) {
        super(component, OrgType.class);
        this.federationMember = member;
    }

    public ProvidedOrgDataProvider(Component component, boolean useDefaultSort, FederationMemberType member) {
        super(component, OrgType.class, useDefaultSort);
        this.federationMember = member;
    }

    @Override
    public Iterator<OrgType> iterator(long first, long count) {
        getData().clear();
        getCurrentPageData().clear();
        List<OrgType> dataList;

        try {
            GenericListRestResponse<OrgType> response = getRestServiceFederationClient().createGetSharedOrgUnitRequest(federationMember);

            if(HttpStatus.OK_200 == response.getStatus()){
                dataList = response.getValues();
            } else {
                dataList = new ArrayList<>();
            }

            for(OrgType org: dataList){
                getData().add(org);
            }

            getCurrentPageData().addAll(getData().subList((int)first, (int)(first + count)));
        } catch (Exception e){
            LOGGER.error("Couldn't read provided org. units from federation member: '" + federationMember.getName() + "'.", e);
        }

        return getCurrentPageData().iterator();
    }

    @Override
    public long size() {
        long count = 0;

        try {
            IntegerRestResponse response = getRestServiceFederationClient().createGetSharedOrgUnitCountRequest(federationMember);

            if(HttpStatus.OK_200 == response.getStatus()){
                count = response.getValue();
            } else {
                count = 0;
            }
        } catch (Exception e){
            LOGGER.error("Couldn't count the number of provided org. units from federation member: '" + federationMember.getName() + "'.", e);
        }

        return count;
    }

    /**
     *  Override this to provide own implementation of REST federation
     *  service client.
     * */
    public RestFederationServiceClient getRestServiceFederationClient(){
        return getPageBase().getFederationServiceClient();
    }
}
