package com.esuta.fidm.gui.component.data.table;

import com.esuta.fidm.gui.component.WebMiscUtil;
import com.esuta.fidm.gui.component.data.ObjectDataProvider;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.model.federation.client.GenericListRestResponse;
import com.esuta.fidm.repository.schema.core.FederationMemberType;
import com.esuta.fidm.repository.schema.core.ObjectReferenceType;
import com.esuta.fidm.repository.schema.core.OrgType;
import com.esuta.fidm.repository.schema.core.UserType;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.eclipse.jetty.http.HttpStatus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *  @author shood
 * */
public class RemoteOrgMembersProvider extends ObjectDataProvider<UserType> {

    private static final Logger LOGGER = Logger.getLogger(RemoteOrgMembersProvider.class);
    OrgType org;

    public RemoteOrgMembersProvider(Component component, OrgType org) {
        super(component, UserType.class);
        this.org = org;
    }

    public RemoteOrgMembersProvider(Component component, OrgType org, boolean useDefaultSort) {
        super(component, UserType.class, useDefaultSort);
        this.org = org;
    }

    @Override
    public Iterator<? extends UserType> iterator(long first, long count) {
        getData().clear();
        getCurrentPageData().clear();
        List<UserType> dataList = new ArrayList<>();

        try {
            dataList.addAll(retrieveRemoteOrgMembers(org));
        } catch (NoSuchFieldException | IllegalAccessException | DatabaseCommunicationException e) {
            LOGGER.error("Could not retrieve list of remote members of org. unit: '" + org.getName() + "'.");
        }

        getData().addAll(dataList);
            getCurrentPageData().addAll(getData().subList((int)first, (int)(first + count)));

        return getCurrentPageData().iterator();
    }

    private List<UserType> retrieveRemoteOrgMembers(OrgType org)
            throws NoSuchFieldException, IllegalAccessException, DatabaseCommunicationException {

        List<UserType> users = new ArrayList<>();

        for(ObjectReferenceType memberRef: org.getCopies()){
            FederationMemberType member = getModelService().readObject(FederationMemberType.class, memberRef.getUid());

            GenericListRestResponse<UserType> response = getPageBase().getFederationServiceClient()
                    .createGetOrgUnitMembersRequest(member, WebMiscUtil.getUniqueAttributeValue(org, member.getUniqueOrgIdentifier()));

            if(HttpStatus.OK_200 == response.getStatus()){
                users.addAll(response.getValues());
            }
        }

        return users;
    }

    @Override
    public long size() {
        try {
            return retrieveRemoteOrgMembers(org).size();
        } catch (NoSuchFieldException | IllegalAccessException | DatabaseCommunicationException e) {
            LOGGER.error("Could not retrieve list of remote members of org. unit: '" + org.getName() + "'.");
        }

        return 0;
    }
}
