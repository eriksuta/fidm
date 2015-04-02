package com.esuta.fidm.model;

import com.esuta.fidm.gui.component.WebMiscUtil;
import com.esuta.fidm.model.util.JsonUtil;
import com.esuta.fidm.repository.schema.core.ModificationType;
import com.esuta.fidm.repository.schema.core.OrgType;
import com.esuta.fidm.repository.schema.support.AttributeModificationType;
import com.esuta.fidm.repository.schema.support.ObjectModificationType;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 *  This is a simple object that takes two objects of some type,
 *  compares them and prepares a set of changes in JSON format
 *  that are further processed.
 *  Currently, this works only for org. units. (OrgType)
 *
 *  @author shood
 * */
public class ObjectChangeProcessor {

    private static final Logger LOGGER = Logger.getLogger(ObjectChangeProcessor.class);

    /**
     *  Single ObjectChangeProcessor instance
     * */
    private static ObjectChangeProcessor instance = null;

    private ObjectChangeProcessor(){}

    public static ObjectChangeProcessor getInstance(){
        if(instance == null){
            instance = new ObjectChangeProcessor();
        }

        return instance;
    }

    /**
     *  This method generates an ObjectModificationType object. This object has a
     *  list of changes that were made on an org. unit.
     * */
    public ObjectModificationType getOrgModifications(OrgType oldOrg, OrgType newOrg){
        ObjectModificationType objectModification = new ObjectModificationType();

        for(Field field: OrgType.class.getDeclaredFields()){
            field.setAccessible(true);

            try {

                //Handling for single-value attributes
                if(WebMiscUtil.isOrgAttributeSingleValue(field.getName())){
                    Object oldValue = field.get(oldOrg);
                    Object newValue = field.get(newOrg);

                    AttributeModificationType modification = new AttributeModificationType();
                    modification.setAttribute(field.getName());
                    modification.setOldValue(JsonUtil.objectToJson(oldValue));
                    modification.setNewValue(JsonUtil.objectToJson(newValue));

                    if(oldValue == null && newValue != null){
                        modification.setModificationType(ModificationType.ADD);
                    } else if (newValue == null && oldValue != null){
                        modification.setModificationType(ModificationType.DELETE);
                    } else if(oldValue != null && newValue != null && !oldValue.equals(newValue)){
                        modification.setModificationType(ModificationType.MODIFY);
                    } else {
                        continue;
                    }

                    objectModification.getModificationList().add(modification);

                // Handling for multi-value attributes
                } else {
                    ArrayList oldList = (ArrayList)field.get(oldOrg);
                    ArrayList newList = (ArrayList)field.get(newOrg);
                    int oldListSize = 0;
                    int newListSize = 0;

                    if(oldList != null){
                        oldListSize = oldList.size();
                    }

                    if(newList != null){
                        newListSize = newList.size();
                    }

                    if(oldListSize > newListSize){
                        //There are more deletion modifications

                    } else if(newListSize > oldListSize){
                        //There are more addition modifications
                    } else {
                        //There are only modify modifications, or possibly non at all
                    }
                }

            } catch (IllegalAccessException e) {
                LOGGER.error("Can't compute the changes in org. units: '" + oldOrg.getName() + "' and '" + newOrg.getName() + "'.");
            }

        }

        return objectModification;
    }
}
