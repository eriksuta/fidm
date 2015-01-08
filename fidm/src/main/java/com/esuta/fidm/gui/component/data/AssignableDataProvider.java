package com.esuta.fidm.gui.component.data;

import com.esuta.fidm.repository.schema.ObjectType;
import com.esuta.fidm.repository.schema.RoleType;
import com.esuta.fidm.repository.schema.UserType;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;

import java.util.Iterator;

/**
 *  @author shood
 * */
public class AssignableDataProvider<T extends ObjectType, S extends ObjectType> extends ObjectDataProvider<T>{

    private transient Logger LOGGER = Logger.getLogger(AssignableDataProvider.class);

    private S assignmentSource;

    public AssignableDataProvider(Component component, Class<T> type, S object){
        super(component, type);

        if(object != null){
            assignmentSource = object;
        }
    }

    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        getData().clear();
        getCurrentPageData().clear();

        try {
            if(assignmentSource instanceof UserType){
                UserType user = (UserType)assignmentSource;

                if(getType().equals(RoleType.class)){
                    for(String uid: user.getRoleAssignments()){
                        RoleType role = getModelService().readObject(RoleType.class, uid);
                        getData().add((T)role);
                    }
                }
            }

        } catch (Exception e){
            LOGGER.error("Could not create an iterator object for data of type: '" + getType().getSimpleName() + "'. Could not read objects from model.");
        }

        return getData().iterator();
    }

    @Override
    public long size() {
        if(assignmentSource instanceof UserType){
            UserType user = (UserType) assignmentSource;

            return user.getRoleAssignments().size();
        }

        return 0;
    }


}
