package com.esuta.fidm.model;

import com.esuta.fidm.gui.component.WebMiscUtil;
import com.esuta.fidm.model.util.JsonUtil;
import com.esuta.fidm.repository.schema.core.*;
import com.esuta.fidm.repository.schema.support.AttributeModificationType;
import com.esuta.fidm.repository.schema.support.ObjectModificationType;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 *  This is a simple object that takes two objects of some type,
 *  compares them and prepares a set of changes in JSON format
 *  that are further processed. Here, we also apply sharing
 *  policies and rules on detected changes and decide, which changes
 *  are going to be applied only locally and which are going to be
 *  distributed in identity federation. Another function of this component
 *  is the application of detected changes on some object -> the result
 *  is the new object with attribute changes according to modification
 *  objects.
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

    /**
     *  This method applies federation sharing policy for the org. unit on
     *  previously generated list of changes. Based on sharing policy, it
     *  will filter and return only choices, that are valid for local identity
     *  provider - these changes are saved and stored only locally and not
     *  distributed to other members of identity federation. However, this method
     *  will return changes that are valid ONLY for local identity provider - this means
     *  that changes that can be applied in federation (and thus in local identity provider)
     *  will not be returned by this method.
     * */
    public ObjectModificationType prepareLocalChanges(ObjectModificationType modificationObject, SharingPolicyType policy){
        List<AttributeModificationType> validModifications = new ArrayList<>();

        if(modificationObject == null || policy == null){
            return null;
        }

        if(modificationObject.getModificationList().isEmpty()){
            return modificationObject;
        }

        for(AttributeModificationType modification: modificationObject.getModificationList()){
            String attributeName = modification.getAttribute();
            SharingRuleType rule = getRuleByAttributeName(policy, attributeName);

            if(rule == null){
                //We need to apply default sharing policies, since there is no specific rule for current attribute
                if(isOrgAttributeSingleValue(attributeName)){
                    SingleValueTolerance tolerance = policy.getDefaultSingleValueTolerance();

                    if(SingleValueTolerance.ALLOW_OWN.equals(tolerance)){
                        validModifications.add(modification);
                    }
                } else if(isOrgAttributeMultiValue(attributeName)){
                    MultiValueTolerance tolerance = policy.getDefaultMultiValueTolerance();

                    if(MultiValueTolerance.ALLOW_ADD_OWN.equals(tolerance) &&
                            ModificationType.ADD.equals(modification.getModificationType())){
                        validModifications.add(modification);

                    } else if(MultiValueTolerance.ALLOW_MODIFY_OWN.equals(tolerance)){
                        validModifications.add(modification);
                    }
                }

            } else{
                if(isOrgAttributeSingleValue(attributeName)){
                    SingleValueTolerance tolerance = rule.getSingleValueTolerance();

                    if(SingleValueTolerance.ALLOW_OWN.equals(tolerance)){
                        validModifications.add(modification);
                    }

                } else if(isOrgAttributeMultiValue(attributeName)){
                    MultiValueTolerance tolerance = rule.getMultiValueTolerance();

                    if(MultiValueTolerance.ALLOW_ADD_OWN.equals(tolerance) &&
                            ModificationType.ADD.equals(modification.getModificationType())){
                        validModifications.add(modification);

                    } else if(MultiValueTolerance.ALLOW_MODIFY_OWN.equals(tolerance)){
                        validModifications.add(modification);
                    }
                }
            }
        }

        ObjectModificationType newModificationObject = new ObjectModificationType();
        newModificationObject.getModificationList().addAll(validModifications);
        return newModificationObject;
    }

    /**
     *  This method applies federation sharing policy for the org. unit on a
     *  previously generated list of changes. Based on sharing policy, it
     *  will filter and return those changes that are valid for both local copy
     *  of org. unit as well as for the origin of org. unit. All these changes
     *  are then distributed to all other members of identity federation with
     *  a copy of org. unit. However, this method returns changes that are valid
     *  for both local and origin distribution, so it is obvious, that only
     *  locally valid changes are not returned by this method - use prepareLocalChanges() instead.
     * */
    public ObjectModificationType prepareDistributedChanges(ObjectModificationType modificationObject, SharingPolicyType policy){
        List<AttributeModificationType> validModifications = new ArrayList<>();

        if(modificationObject == null || policy == null){
            return null;
        }

        if(modificationObject.getModificationList().isEmpty()){
            return modificationObject;
        }

        for(AttributeModificationType modification: modificationObject.getModificationList()){
            String attributeName = modification.getAttribute();
            SharingRuleType rule = getRuleByAttributeName(policy, attributeName);

            if(rule == null){
                //We need to apply default sharing policies, since there is no specific rule for current attribute
                if(isOrgAttributeSingleValue(attributeName)){
                    SingleValueTolerance tolerance = policy.getDefaultSingleValueTolerance();

                    if(SingleValueTolerance.ALLOW_MODIFY.equals(tolerance)){
                        validModifications.add(modification);
                    }
                } else if(isOrgAttributeMultiValue(attributeName)){
                    MultiValueTolerance tolerance = policy.getDefaultMultiValueTolerance();

                    if(MultiValueTolerance.ALLOW_ADD.equals(tolerance) &&
                            ModificationType.ADD.equals(modification.getModificationType())){
                        validModifications.add(modification);

                    } else if(MultiValueTolerance.ALLOW_MODIFY.equals(tolerance)){
                        validModifications.add(modification);
                    }
                }

            } else{
                if(isOrgAttributeSingleValue(attributeName)){
                    SingleValueTolerance tolerance = rule.getSingleValueTolerance();

                    if(SingleValueTolerance.ALLOW_MODIFY.equals(tolerance)){
                        validModifications.add(modification);
                    }

                } else if(isOrgAttributeMultiValue(attributeName)){
                    MultiValueTolerance tolerance = rule.getMultiValueTolerance();

                    if(MultiValueTolerance.ALLOW_ADD.equals(tolerance) &&
                            ModificationType.ADD.equals(modification.getModificationType())){
                        validModifications.add(modification);

                    } else if(MultiValueTolerance.ALLOW_MODIFY.equals(tolerance)){
                        validModifications.add(modification);
                    }
                }
            }
        }

        ObjectModificationType newModificationObject = new ObjectModificationType();
        newModificationObject.getModificationList().addAll(validModifications);
        return newModificationObject;
    }

    /**
     *  This method applies all the changes to provided org. unit
     */
    public OrgType applyModificationsOnOrg(OrgType org, ObjectModificationType modificationObject)
            throws NoSuchFieldException, IllegalAccessException {

        if(org == null){
            return null;
        }

        if(modificationObject == null){
            return org;
        }

        for(AttributeModificationType modification: modificationObject.getModificationList()){
            String attributeName = modification.getAttribute();
            Field attribute = org.getClass().getDeclaredField(attributeName);
            attribute.setAccessible(true);

            if(isOrgAttributeSingleValue(attributeName)){
                attribute.set(org, JsonUtil.jsonToObject(modification.getNewValue(), getAttributeType(attributeName)));
            } else if(isOrgAttributeMultiValue(attributeName)) {
                List values = (List)attribute.get(org);

                if(ModificationType.ADD.equals(modification.getModificationType())){
                    if(values == null){
                        values = new ArrayList();
                    }

                    values.add(JsonUtil.jsonToObject(modification.getNewValue(), getAttributeType(attributeName)));
                } else if(ModificationType.MODIFY.equals(modification.getModificationType())){
                    int index = 0;
                    Object o = JsonUtil.jsonToObject(modification.getOldValue(), getAttributeType(attributeName));

                    for(int i = 0; i < values.size(); i++){
                        if(o.equals(values.get(i))){
                            index = i;
                        }
                    }

                    values.remove(index);
                    values.add(index, JsonUtil.jsonToObject(modification.getNewValue(), getAttributeType(attributeName)));

                } else if(ModificationType.DELETE.equals(modification.getModificationType())){
                    values.remove(JsonUtil.jsonToObject(modification.getOldValue(), getAttributeType(attributeName)));
                }

                attribute.set(org, values);
            }
        }

        return org;
    }

    private Class getAttributeType(String attributeName){
        switch (attributeName){
            case "name":
                return String.class;
            case "displayName":
                return String.class;
            case "locality":
                return String.class;
            case "orgType":
                return String.class;
            case "parentOrgUnits":
                return ObjectReferenceType.class;
            case "governors":
                return ObjectReferenceType.class;
            case "resourceInducements":
                return InducementType.class;
            case "roleInducements":
                return InducementType.class;
            default:
                return null;
        }
    }

    private SharingRuleType getRuleByAttributeName(SharingPolicyType policy, String attributeName){
        if(policy == null || attributeName == null){
            return null;
        }

        for(SharingRuleType rule: policy.getRules()){
            if(attributeName.equals(rule.getAttributeName())){
                return rule;
            }
        }

        return null;
    }

    private boolean isOrgAttributeSingleValue(String attributeName){
        return attributeName.equals("name") ||
                attributeName.equals("displayName") ||
                attributeName.equals("locality");
    }

    private boolean isOrgAttributeMultiValue(String attributeName){
        return attributeName.equals("orgType") ||
                attributeName.equals("parentOrgUnits") ||
                attributeName.equals("governors") ||
                attributeName.equals("resourceInducements") ||
                attributeName.equals("roleInducements");
    }

    private AttributeModificationType prepareModification(String fieldName, String oldValue, String newValue){
        AttributeModificationType modification = new AttributeModificationType();
        modification.setAttribute(fieldName);
        modification.setOldValue(oldValue);
        modification.setNewValue(newValue);

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
