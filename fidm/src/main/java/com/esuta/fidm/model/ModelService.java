package com.esuta.fidm.model;

import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.ObjectAlreadyExistsException;
import com.esuta.fidm.infra.exception.ObjectNotFoundException;
import com.esuta.fidm.repository.api.RepositoryService;
import com.esuta.fidm.repository.schema.ObjectType;

import java.util.List;

/**
 *  @author shood
 *
 *  TODO - This implementation is only a simple RepositoryService wrapper. This will change in the future.
 * */
public class ModelService implements IModelService{

    /**
     *  Single RepositoryService instance
     * */
    private static ModelService instance = null;
    private RepositoryService repositoryService;

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
        repositoryService = RepositoryService.getInstance();
    }

    @Override
    public <T extends ObjectType> T readObject(Class<T> type, String uid) throws DatabaseCommunicationException {
        return repositoryService.readObject(type, uid);
    }

    @Override
    public <T extends ObjectType> T readObjectByName(Class<T> type, String name) throws DatabaseCommunicationException {
        return readObjectByName(type, name);
    }

    @Override
    public <T extends ObjectType> void createObject(T object) throws ObjectAlreadyExistsException, DatabaseCommunicationException {
        repositoryService.createObject(object);
    }

    @Override
    public <T extends ObjectType> void deleteObject(T object) throws ObjectNotFoundException, DatabaseCommunicationException {
        repositoryService.deleteObject(object);
    }

    @Override
    public <T extends ObjectType> void updateObject(T object) throws ObjectNotFoundException, DatabaseCommunicationException {
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
