package com.esuta.fidm.model;

import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.ObjectAlreadyExistsException;
import com.esuta.fidm.infra.exception.ObjectNotFoundException;
import com.esuta.fidm.repository.api.RepositoryService;
import com.esuta.fidm.repository.schema.ObjectType;
import com.esuta.fidm.repository.schema.UserType;

import java.util.List;

/**
 *  @author shood
 *
 *  TODO - add logging to model service
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
    public <T extends ObjectType> void updateObject(T object) throws ObjectNotFoundException, DatabaseCommunicationException {
        if(object instanceof UserType){
            inducementProcessor.handleUserInducements((UserType) object);
        }

        repositoryService.updateObject(object);
    }

    @Override
    public <T extends ObjectType> Long countObjects(Class<T> type) throws DatabaseCommunicationException {
        return repositoryService.countObjects(type);
    }

    @Override
    public <T extends ObjectType> List<T> getAllObjectsOfType(Class<T> type) throws DatabaseCommunicationException {
        return repositoryService.getAllObjectsOfType(type);
    }
}
