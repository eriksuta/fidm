package com.esuta.fidm.repository.api;

import com.esuta.fidm.infra.ObjectAlreadyExistsException;
import com.esuta.fidm.infra.ObjectNotFoundException;
import com.esuta.fidm.repository.schema.ObjectType;
import com.esuta.fidm.repository.schema.SystemConfigurationType;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

/**
 *  @author shood
 * */
public class RepositoryService implements IRepositoryService{

    /**
     *  Single RepositoryService instance
     * */
    private static RepositoryService instance = null;

    public EntityManagerFactory entityManagerFactory;
    public EntityManager entityManager;

    private RepositoryService(){}

    public static RepositoryService getInstance(){
        if(instance == null){
            instance = new RepositoryService();
        }

        return instance;
    }

    /**
     *  creates a connection to a database based on systemConfiguration object
     * */
    public void initConnection(SystemConfigurationType systemConfiguration){
        entityManagerFactory = Persistence.createEntityManagerFactory(systemConfiguration.getDbConnectionFile());
        entityManager = entityManagerFactory.createEntityManager();
    }

    /**
     *  Closes a connection to database
     * */
    public void closeConnection(){
        entityManager.close();
        entityManagerFactory.close();
    }

    @Override
    public <T extends ObjectType> T readObject(Class<T> type, String uid) throws ObjectNotFoundException {
        return null;
    }

    @Override
    public <T extends ObjectType> void createObject(T object) throws ObjectAlreadyExistsException {

    }

    @Override
    public <T extends ObjectType> void deleteObject(T object) throws ObjectNotFoundException {

    }

    @Override
    public <T extends ObjectType> void updateObject(T object) throws ObjectNotFoundException {

    }

    @Override
    public <T extends ObjectType> Integer countObjects(Class<T> type) {
        return null;
    }

    @Override
    public <T extends ObjectType> List<T> getAllObjectsOfType(Class<T> type) {
        return null;
    }
}
