package com.esuta.fidm.repository.api;

import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.ObjectAlreadyExistsException;
import com.esuta.fidm.infra.exception.ObjectNotFoundException;
import com.esuta.fidm.repository.schema.core.ObjectType;
import com.esuta.fidm.repository.schema.core.SystemConfigurationType;
import com.objectdb.o._NoResultException;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

/**
 *  @author shood
 * */
public class RepositoryService implements IRepositoryService{

    private static final transient Logger LOGGER = Logger.getLogger(RepositoryService.class);

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

        entityManager.clear();

        if(type == null || uid == null){
            return null;
        }

        T object;
        String typeName = type.getSimpleName();

        try {
            TypedQuery<T> query = entityManager.createQuery("SELECT o FROM " + typeName + " o WHERE o.uid='" + uid + "'", type);
            object = query.getSingleResult();
        } catch (_NoResultException e){
            LOGGER.error("Could not retrieve object with uid: '" + uid + "' from the repository.", e);
            object = null;
        }

        return object;
    }

    @Override
    public <T extends ObjectType> T readObjectByName(Class<T> type, String name) throws DatabaseCommunicationException{
        if(entityManager == null){
            throw new DatabaseCommunicationException();
        }

        entityManager.clear();

        if(type == null || name == null){
            return null;
        }

        T object;
        String typeName = type.getSimpleName();

        try {
            TypedQuery<T> query = entityManager.createQuery("SELECT o FROM " + typeName + " o WHERE o.name='" + name + "'", type);
            object = query.getSingleResult();
        } catch (_NoResultException e){
            //We do not need to log this exception. In many cases, this is expected behavior.
            object = null;
        }

        return object;
    }

    @Override
    public <T extends ObjectType> T createObject(T object) throws ObjectAlreadyExistsException, DatabaseCommunicationException {
        if(entityManager == null){
            throw new DatabaseCommunicationException();
        }

        if(object == null){
            return null;
        }

        T objectInRepository;
        objectInRepository = (T)readObjectByName(object.getClass(), object.getName());

        if(objectInRepository != null){
            throw new ObjectAlreadyExistsException("Can't create object.", null, null, object.getName());
        }

        if(object.getUid() == null){
            object.setUid(UUID.randomUUID().toString());
        }

        entityManager.getTransaction().begin();
        entityManager.persist(object);
        LOGGER.debug("New object of type: '" + object.getClass().getSimpleName() +
                "' created with uid: '" + object.getUid() + "'.");
        entityManager.getTransaction().commit();

        return object;
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
    public <T extends ObjectType> T updateObject(T object) throws ObjectNotFoundException, DatabaseCommunicationException {
        if(entityManager == null){
            throw new DatabaseCommunicationException();
        }

        if(object == null){
            return null;
        }

        T retrievedObject = (T)readObject(object.getClass(), object.getUid());

        if(retrievedObject == null){
            throw new ObjectNotFoundException("Can't update object.", object.getUid());
        }

        deleteObject(retrievedObject);

        T objectToReturn = null;
        try {
            objectToReturn = createObject(object);
        } catch (ObjectAlreadyExistsException e){
            //this should not happen
        }

        return objectToReturn;
    }

    @Override
    public <T extends ObjectType> Long countObjects(Class<T> type) throws DatabaseCommunicationException{
        if(entityManager == null){
            throw new DatabaseCommunicationException();
        }

        if(type == null){
            return null;
        }

        String typeName = type.getSimpleName();
        TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(o) FROM " + typeName + " o", Long.class);

        return query.getSingleResult();
    }

    @Override
    public <T extends ObjectType> List<T> getAllObjectsOfType(Class<T> type) throws DatabaseCommunicationException{
        if(entityManager == null){
            throw new DatabaseCommunicationException();
        }

        entityManager.clear();

        if(type == null){
            return null;
        }

        String typeName = type.getSimpleName();
        TypedQuery<T> query = entityManager.createQuery("SELECT o FROM " + typeName + " AS o", type);

        return query.getResultList();
    }
}
