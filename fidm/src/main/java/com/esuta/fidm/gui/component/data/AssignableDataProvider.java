package com.esuta.fidm.gui.component.data;

import com.esuta.fidm.repository.schema.core.*;
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
    public Iterator<T> iterator(long first, long count) {
        getData().clear();
        getCurrentPageData().clear();

        try {
            if(assignmentSource instanceof UserType){
                UserType user = (UserType)assignmentSource;

                if(getType().equals(RoleType.class)){
                    for(AssignmentType roleAssignment: user.getRoleAssignments()){
                        RoleType role = getModelService().readObject(RoleType.class, roleAssignment.getUid());
                        getData().add((T)role);
                    }
                } else if (getType().equals(OrgType.class)){
                    for(AssignmentType orgAssignment: user.getOrgUnitAssignments()){
                        OrgType org = getModelService().readObject(OrgType.class, orgAssignment.getUid());
                        getData().add((T)org);
                    }
                } else if (getType().equals(AccountType.class)){
                    for(AssignmentType accountAssignment: user.getAccounts()){
                        AccountType account = getModelService().readObject(AccountType.class, accountAssignment.getUid());
                        getData().add((T)account);
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

            if(getType().equals(RoleType.class)){
                return user.getRoleAssignments().size();
            } else if(getType().equals(OrgType.class)){
                return user.getOrgUnitAssignments().size();
            } else if(getType().equals(AccountType.class)){
                return user.getAccounts().size();
            }
        }

        return 0;
    }

    public S getAssignmentSource() {
        return assignmentSource;
    }
}
