package com.esuta.fidm.repository.api;

import com.esuta.fidm.infra.ObjectAlreadyExistsException;
import com.esuta.fidm.infra.ObjectNotFoundException;
import com.esuta.fidm.repository.schema.ObjectType;

import java.util.List;

/**
 *  @author shood
 * */
public interface IRepositoryService {

    /**
     *  <p>
     *      Returns the object from repository based on provided uid
     *  </p>
     *
     *  @param type
     *          Type, or class, of an object to retrieve
     *
     *  @param uid
     *          (Unique identifier of an object, both in system and in database)
     *
     *  @return Retrieved object, a subclass of ObjectType
     *
     *  @throws ObjectNotFoundException
     *          The system could not find the requested object in the repository
     *
     * */
    <T extends ObjectType> T readObject(Class<T> type, String uid) throws ObjectNotFoundException;

    /**
     *  <p>
     *      Writes a provided object to repository
     *  </p>
     *
     *  @param object
     *          An object, subclass of ObjectType, that is to be added
     *          to the repository
     *
     *  @throws ObjectAlreadyExistsException
     *          When object that is to be added already exists
     *          in the repository
     *
     * */
    <T extends ObjectType> void createObject(T object) throws ObjectAlreadyExistsException;

    /**
     *  <p>
     *      Deletes a provided object from the repository
     *  </p>
     *
     *  @param object
     *          An object, subclass of ObjectType, that is to be deleted
     *          from the repository
     *
     *  @throws ObjectNotFoundException
     *          The system could not find the requested object in the repository
     *
     * */
    <T extends ObjectType> void deleteObject(T object) throws ObjectNotFoundException;

    /**
     *  <p>
     *      Updates object in the repository with new attribute values
     *  </p>
     *
     *  @param object
     *          An object, subclass of ObjectType, that is to be modified
     *          in the repository
     *
     *  @throws ObjectNotFoundException
     *          The system could not find the requested object in the repository
     * */
    <T extends ObjectType> void updateObject(T object) throws ObjectNotFoundException;

    /**
     *  <p>
     *      Returns a number of objects of defined type in repository
     *  </p>
     *
     *  @param type
     *          A type of objects to count
     *
     *  @return a number of objects found
     * */
    <T extends ObjectType> Integer countObjects(Class<T> type);

    /**
     *  <p>
     *      Returns a List of all objects of defined type from the repository
     *  </p>
     *
     *  @param type
     *          A type of object to retrieve
     *
     *  @return List of objects found in the repository
     * */
    <T extends ObjectType> List<T> getAllObjectsOfType(Class<T> type);
}
