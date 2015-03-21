package com.esuta.fidm.gui.page.federation.component;

import com.esuta.fidm.repository.schema.core.ObjectReferenceType;
import com.esuta.fidm.repository.schema.core.OrgType;
import org.apache.log4j.Logger;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableTreeProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *  @author shood
 * */
public class FederationOrgTreeDataProvider extends SortableTreeProvider<OrgType, String> {

    private static final Logger LOGGER = Logger.getLogger(FederationOrgTreeDataProvider.class);

    private IModel<OrgType> rootModel;
    private OrgType root;
    private List<OrgType> allUnits;
    private String uniqueAttributeName;

    public FederationOrgTreeDataProvider(IModel<OrgType> rootModel, List<OrgType> allUnits, String uniqueAttributeName){

        this.rootModel = rootModel;
        this.allUnits = allUnits;
        this.uniqueAttributeName = uniqueAttributeName;
    }

    @Override
    public Iterator<? extends OrgType> getRoots() {
        List<OrgType> roots = new ArrayList<>();

        if(root == null){
            roots.add(rootModel.getObject());
        }

        return roots.iterator();
    }

    @Override
    public boolean hasChildren(OrgType node) {
        return true;
    }

    @Override
    public Iterator<? extends OrgType> getChildren(OrgType node) {
        List<OrgType> children = new ArrayList<>();

        try {

            for(OrgType org: allUnits){
                for(ObjectReferenceType parent: org.getParentOrgUnits()){
                    String uniqueAttributeValue = parent.getFederationIdentifier().getUniqueAttributeValue();

                    if(isChild(node, uniqueAttributeValue)){
                        children.add(org);
                    }
                }
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            LOGGER.error("Could not retrieve children of provided org. unit: '" + node.getName() + "'. Reason: ", e);
        }

        return children.iterator();
    }

    private boolean isChild(OrgType org, String uniqueAttributeValue) throws NoSuchFieldException, IllegalAccessException {
        Field uniqueAttribute = org.getClass().getDeclaredField(uniqueAttributeName);
        uniqueAttribute.setAccessible(true);
        String attributeValue = (String)uniqueAttribute.get(org);

        return uniqueAttributeValue.equals(attributeValue);
    }

    @Override
    public IModel<OrgType> model(OrgType object) {
        return new Model<>(object);
    }
}
