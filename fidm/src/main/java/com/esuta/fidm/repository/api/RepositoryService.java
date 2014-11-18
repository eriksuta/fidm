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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

        //TODO - consider moving this to model layer
        if(object.getUid() == null){
            object.setUid(UUID.randomUUID().toString());
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

        T retrievedObject = (T)readObject(object.getClass(), object.getUid());

        if(retrievedObject == null){
            throw new ObjectNotFoundException("Can't delete object. ", object.getUid());
        }

        entityManager.getTransaction().begin();
        entityManager.remove(retrievedObject);
        entityManager.getTransaction().commit();
    }

    /**
     *  TODO - right now, update operation consists from read, delete and then create
     *  operation - this is a bad, temporary placeholder solution, repair this
     *  ASAP
     * */
    @Override
    public <T extends ObjectType> void updateObject(T object) throws ObjectNotFoundException, DatabaseCommunicationException {
        if(entityManager == null){
            throw new DatabaseCommunicationException();
        }

        if(object == null){
            return;
        }

        T retrievedObject = (T)readObject(object.getClass(), object.getUid());

        if(retrievedObject == null){
            throw new ObjectNotFoundException("Can't update object.", object.getUid());
        }

        deleteObject(retrievedObject);

        try {
            createObject(object);
        } catch (ObjectAlreadyExistsException e){
            //this should not happen
        }
    }

    @Override
    public <T extends ObjectType> Integer countObjects(Class<T> type) throws DatabaseCommunicationException{
        if(entityManager == null){
            throw new DatabaseCommunicationException();
        }

        if(type == null){
            return null;
        }

        String typeName = type.getSimpleName();
        TypedQuery<Integer> query = entityManager.createQuery("SELECT COUNT(o) FROM " + typeName + " o", Integer.class);

        return query.getSingleResult();
    }

    @Override
    public <T extends ObjectType> List<T> getAllObjectsOfType(Class<T> type) throws DatabaseCommunicationException{
        if(entityManager == null){
            throw new DatabaseCommunicationException();
        }

        if(type == null){
            return null;
        }

        String typeName = type.getSimpleName();
        TypedQuery<T> query = entityManager.createQuery("SELECT o FROM " + typeName + " AS o", type);

        return query.getResultList();
    }
}
