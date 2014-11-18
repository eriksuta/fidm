package com.esuta.fidm.model;

import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.ObjectAlreadyExistsException;
import com.esuta.fidm.infra.exception.ObjectNotFoundException;
import com.esuta.fidm.repository.schema.ObjectType;

import java.util.List;

/**
 *  @author shood
 *
 *  TODO - define and implements some search capabilities
 * */
public interface IModelService {

    /**
     *  <p>
     *      Returns the object from model based on provided uid
     *  </p>
     *
     *  @param type
     *          Type, or class, of an object to retrieve
     *
     *  @param uid
     *          (Unique identifier of an object, both in system and in database)
     *
     *  @return Retrieved object, a subclass of ObjectType
     *          Returns null, if there is no object in model with provided uid
     *
     *  @throws com.esuta.fidm.infra.exception.DatabaseCommunicationException
     *          When communication with database is not established, or was lost
     * */
    <T extends ObjectType> T readObject(Class<T> type, String uid) throws DatabaseCommunicationException;

    /**
     *  <p>
     *      Returns the object from model based on provided name
     *  </p>
     *
     *  @param type
     *          Type, or class, of an object to retrieve
     *
     *  @param name
     *          (Unique identifier - name - of an object, both in system and in database)
     *
     *  @return Retrieved object, a subclass of ObjectType
     *          Returns null, if there is no object in model with provided name
     *
     *  @throws DatabaseCommunicationException
     *          When communication with database is not established, or was lost
     * */
    <T extends ObjectType> T readObjectByName(Class<T> type, String name) throws DatabaseCommunicationException;

    /**
     *  <p>
     *      Writes a provided object to model
     *  </p>
     *
     *  @param object
     *          An object, subclass of ObjectType, that is to be added
     *          to the model
     *
     *  @throws com.esuta.fidm.infra.exception.ObjectAlreadyExistsException
     *          When object that is to be added already exists
     *          in the model
     *
     *  @throws DatabaseCommunicationException
     *          When communication with database is not established, or was lost
     *
     * */
    <T extends ObjectType> void createObject(T object) throws ObjectAlreadyExistsException, DatabaseCommunicationException;

    /**
     *  <p>
     *      Deletes a provided object from the model
     *  </p>
     *
     *  @param object
     *          An object, subclass of ObjectType, that is to be deleted
     *          from the model
     *
     *  @throws com.esuta.fidm.infra.exception.ObjectNotFoundException
     *          The system could not find the requested object in the repository
     *
     *  @throws DatabaseCommunicationException
     *          When communication with database is not established, or was lost
     *
     * */
    <T extends ObjectType> void deleteObject(T object) throws ObjectNotFoundException, DatabaseCommunicationException;

    /**
     *  <p>
     *      Updates object in the model with new attribute values
     *  </p>
     *
     *  @param object
     *          An object, subclass of ObjectType, that is to be modified
     *          in the model
     *
     *  @throws ObjectNotFoundException
     *          The system could not find the requested object in the model
     *
     *  @throws DatabaseCommunicationException
     *          When communication with database is not established, or was lost
     *
     * */
    <T extends ObjectType> void updateObject(T object) throws ObjectNotFoundException, DatabaseCommunicationException;

    /**
     *  <p>
     *      Returns a number of objects of defined type in model
     *  </p>
     *
     *  @param type
     *          A type of objects to count
     *
     *  @return a number of objects found
     *
     *  @throws DatabaseCommunicationException
     *          When communication with database is not established, or was lost
     *
     * */
    <T extends ObjectType> Integer countObjects(Class<T> type) throws DatabaseCommunicationException;

    /**
     *  <p>
     *      Returns a List of all objects of defined type from the model
     *  </p>
     *
     *  @param type
     *          A type of object to retrieve
     *
     *  @return List of objects found in the model
     *          An empty list, if there are no objects of provided type in the model
     *
     *  @throws DatabaseCommunicationException
     *          When communication with database is not established, or was lost
     *
     * */
    <T extends ObjectType> List<T> getAllObjectsOfType(Class<T> type) throws DatabaseCommunicationException;
}
