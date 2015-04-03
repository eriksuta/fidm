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

                    AttributeModificationType modification = prepareModification(field.getName(),
                            JsonUtil.objectToJson(oldValue), JsonUtil.objectToJson(newValue));

                    if(modification != null){
                        objectModification.getModificationList().add(modification);
                    }

                // Handling for multi-value attributes
                } else if(WebMiscUtil.isOrgAttributeMultiValue(field.getName())) {
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
                        for(int i = 0; i < oldListSize; i++){
                            if(i < newListSize){
                                String oldValue = JsonUtil.objectToJson(oldList.get(i));
                                String newValue = JsonUtil.objectToJson(newList.get(i));

                                AttributeModificationType modification = prepareModification(field.getName(), oldValue, newValue);
                                if(modification != null){
                                    objectModification.getModificationList().add(modification);
                                }

                            } else {
                                String oldValue = JsonUtil.objectToJson(oldList.get(i));
                                objectModification.getModificationList().add(prepareModification(field.getName(),
                                        oldValue, null));
                            }
                        }

                    } else if(newListSize > oldListSize){
                        //There are more addition modifications
                        for(int i = 0; i < newListSize; i++){
                            if(i < oldListSize){
                                String oldValue = JsonUtil.objectToJson(oldList.get(i));
                                String newValue = JsonUtil.objectToJson(newList.get(i));

                                AttributeModificationType modification = prepareModification(field.getName(), oldValue, newValue);
                                if(modification != null){
                                    objectModification.getModificationList().add(modification);
                                }
                            } else {
                                String newValue = JsonUtil.objectToJson(newList.get(i));
                                objectModification.getModificationList().add(prepareModification(field.getName(),
                                        null, newValue));
                            }
                        }

                    } else {
                        //There are only modify modifications, or possibly non at all
                        for(int i = 0; i < newListSize; i++){
                            String oldValue = JsonUtil.objectToJson(oldList.get(i));
                            String newValue = JsonUtil.objectToJson(newList.get(i));

                            AttributeModificationType modification = prepareModification(field.getName(), oldValue, newValue);
                            if(modification != null){
                                objectModification.getModificationList().add(modification);
                            }
                        }
                    }
                }

            } catch (IllegalAccessException e) {
                LOGGER.error("Can't compute the changes in org. units: '" + oldOrg.getName() + "' and '" + newOrg.getName() + "'.");
            }

        }

        return objectModification;
    }

    private AttributeModificationType prepareModification(String fieldName, String oldValue, String newValue){
        AttributeModificationType modification = new AttributeModificationType();
        modification.setAttribute(fieldName);
        modification.setOldValue(JsonUtil.objectToJson(oldValue));
        modification.setNewValue(JsonUtil.objectToJson(newValue));

        if(isNull(oldValue) && !isNull(newValue)){
            modification.setModificationType(ModificationType.ADD);
        } else if (isNull(newValue) && !isNull(oldValue)){
            modification.setModificationType(ModificationType.DELETE);
        } else if(!isNull(oldValue) && !isNull(newValue) && !oldValue.equals(newValue)){
            modification.setModificationType(ModificationType.MODIFY);
        } else {
            return null;
        }

        return modification;
    }

    private boolean isNull(String value){
        return value == null || "null".equals(value);
    }
}
