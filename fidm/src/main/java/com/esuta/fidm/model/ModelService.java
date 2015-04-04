package com.esuta.fidm.model;

import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.ObjectAlreadyExistsException;
import com.esuta.fidm.infra.exception.ObjectNotFoundException;
import com.esuta.fidm.repository.api.RepositoryService;
import com.esuta.fidm.repository.schema.core.AssignmentType;
import com.esuta.fidm.repository.schema.core.ObjectType;
import com.esuta.fidm.repository.schema.core.OrgType;
import com.esuta.fidm.repository.schema.core.UserType;

import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 * */
public class ModelService implements IModelService{

    /**
     *  Single ModelService instance
     * */
    private static ModelService instance = null;
    private RepositoryService repositoryService;
    private InducementProcessor inducementProcessor;

    private ModelService(){
        initModelService();
    }

    public static ModelService getInstance(){
        if(instance == null){
            instance = new ModelService();
        }

        return instance;
    }

    private void initModelService(){
        // init repository service
        repositoryService = RepositoryService.getInstance();

        // init inducement processor
        inducementProcessor = InducementProcessor.getInstance();
        inducementProcessor.pushModelService(this);
    }

    private boolean isLocalOrgUnit(OrgType org){
        return org.getFederationIdentifier() == null;
    }

    @Override
    public <T extends ObjectType> T readObject(Class<T> type, String uid) throws DatabaseCommunicationException {
        return repositoryService.readObject(type, uid);
    }

    @Override
    public <T extends ObjectType> T readObjectByName(Class<T> type, String name) throws DatabaseCommunicationException {
        return repositoryService.readObjectByName(type, name);
    }

    @Override
    public <T extends ObjectType> T createObject(T object) throws ObjectAlreadyExistsException, DatabaseCommunicationException {
        if(object instanceof UserType){
            inducementProcessor.handleUserInducements((UserType)object);
        }

        return repositoryService.createObject(object);
    }

    @Override
    public <T extends ObjectType> void deleteObject(T object) throws ObjectNotFoundException, DatabaseCommunicationException {
        repositoryService.deleteObject(object);
    }

    @Override
    public <T extends ObjectType> T updateObject(T object) throws ObjectNotFoundException, DatabaseCommunicationException {
        if(object instanceof UserType){
            inducementProcessor.handleUserInducements((UserType) object);
        }

        return repositoryService.updateObject(object);
    }

    @Override
    public <T extends ObjectType> Long countObjects(Class<T> type) throws DatabaseCommunicationException {
        return repositoryService.countObjects(type);
    }

    @Override
    public <T extends ObjectType> List<T> getAllObjectsOfType(Class<T> type) throws DatabaseCommunicationException {
        return repositoryService.getAllObjectsOfType(type);
    }

    @Override
    public UserType recomputeUser(UserType user) throws DatabaseCommunicationException, ObjectNotFoundException {
        return updateObject(user);
    }

    @Override
    public void recomputeOrganizationalUnit(OrgType orgUnit) throws DatabaseCommunicationException, ObjectNotFoundException {
        if(orgUnit == null){
            return;
        }

        String orgUid = orgUnit.getUid();

        //Retrieve all members of org. unit
        List<UserType> members = new ArrayList<>();
        List<UserType> users = getAllObjectsOfType(UserType.class);

        for(UserType user: users){
            for(AssignmentType assignment: user.getOrgUnitAssignments()){
                if(orgUid.equals(assignment.getUid())){
                    members.add(user);
                }
            }
        }

        //And recompute them
        for(UserType user: members){
            recomputeUser(user);
        }
    }
}
