package com.esuta.fidm.repository.api;

import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.ObjectAlreadyExistsException;
import com.esuta.fidm.infra.exception.ObjectNotFoundException;
import com.esuta.fidm.repository.schema.ObjectType;
import com.esuta.fidm.repository.schema.SystemConfigurationType;
import com.objectdb.o._NoResultException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
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
    public <T extends ObjectType> T readObject(Class<T> type, String uid) throws DatabaseCommunicationException{
        if(entityManager == null){
            throw new DatabaseCommunicationException();
        }

        if(type == null || uid == null){
            return null;
        }

        T object;
        String typeName = type.getSimpleName();

        try {
            TypedQuery<T> query = entityManager.createQuery("SELECT o FROM " + typeName + " o WHERE o.uid='" + uid + "'", type);
            object = query.getSingleResult();
        } catch (_NoResultException e){
            //TODO - log message to some log
            object = null;
        }

        return object;
    }

    @Override
    public <T extends ObjectType> T readObjectByName(Class<T> type, String name) throws DatabaseCommunicationException{
        if(entityManager == null){
            throw new DatabaseCommunicationException();
        }

        if(type == null || name == null){
            return null;
        }

        T object;
        String typeName = type.getSimpleName();

        try {
            TypedQuery<T> query = entityManager.createQuery("SELECT o FROM " + typeName + " o WHERE o.name='" + name + "'", type);
            object = query.getSingleResult();
        } catch (_NoResultException e){
            //TODO - log message to some log
            object = null;
        }

        return object;
    }

    @Override
    public <T extends ObjectType> void createObject(T object) throws ObjectAlreadyExistsException, DatabaseCommunicationException {
        if(entityManager == null){
            throw new DatabaseCommunicationException();
        }

        if(object == null){
            return;
        }

        T objectInRepository;
        try {
            objectInRepository = (T)readObjectByName(object.getClass(), object.getName());
        } catch (DatabaseCommunicationException de){
            //TODO - log message to some log
            throw de;
        }

        if(objectInRepository != null){
            throw new ObjectAlreadyExistsException("Can't create object.", null, null, object.getName());
        }

        entityManager.getTransaction().begin();
        entityManager.persist(object);
        entityManager.getTransaction().commit();
    }

    @Override
    public <T extends ObjectType> void deleteObject(T object) throws ObjectNotFoundException, DatabaseCommunicationException {
        if(entityManager == null){
            throw new DatabaseCommunicationException();
        }

        if(object == null){
            return;
        }

        //TODO - when to throw ObjectNotFoundException??

        entityManager.getTransaction().begin();
        entityManager.remove(object);
        entityManager.getTransaction().commit();
    }

    @Override
    public <T extends ObjectType> void updateObject(T object) throws ObjectNotFoundException, DatabaseCommunicationException {
        if(entityManager == null){
            throw new DatabaseCommunicationException();
        }
    }

    @Override
    public <T extends ObjectType> Integer countObjects(Class<T> type) throws DatabaseCommunicationException{
        if(entityManager == null){
            throw new DatabaseCommunicationException();
        }

        return null;
    }

    @Override
    public <T extends ObjectType> List<T> getAllObjectsOfType(Class<T> type) throws DatabaseCommunicationException{
        if(entityManager == null){
            throw new DatabaseCommunicationException();
        }

        return null;
    }
}
