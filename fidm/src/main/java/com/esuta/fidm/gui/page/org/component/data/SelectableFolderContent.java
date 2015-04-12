package com.esuta.fidm.gui.page.org.component.data;

import com.esuta.fidm.repository.schema.core.OrgType;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

/**
 *  @author shood
 *
 *  implementation by lazyman, see
 *  (https://github.com/Evolveum/midpoint/blob/a6c023945dbea34db69a8ff17c9a61b7184c42cc/gui/admin-gui/src/main/java/com/evolveum/midpoint/web/page/admin/users/component/SelectableFolderContent.java)
 * */
public class SelectableFolderContent extends Folder<OrgType>{

    private AbstractTree tree;
    private IModel<OrgType> selected;

    public SelectableFolderContent(String id, AbstractTree<OrgType> tree, IModel<OrgType> model,
                                   IModel<OrgType> selected) {
        super(id, tree, model);

        this.tree = tree;
        this.selected = selected;
    }

    @Override
    protected IModel<?> newLabelModel(final IModel<OrgType> model) {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                StringBuilder sb = new StringBuilder();

                OrgType dto = model.getObject();
                sb.append(dto.getName());
                sb.append(" (");
                sb.append(dto.getFederationIdentifier() == null ? "Origin" : "Copy");
                sb.append(")");

                return sb.toString();
            }
        };
    }

    @Override
    protected void onClick(AjaxRequestTarget target) {
        if (selected.getObject() != null) {
            tree.updateNode(selected.getObject(), target);
        }

        OrgType dto = getModelObject();
        selected.setObject(dto);
        tree.updateNode(dto, target);
    }

    @Override
    protected boolean isClickable() {
        return true;
    }

    @Override
    protected boolean isSelected() {
        OrgType dto = getModelObject();
        return dto.equals(selected.getObject());
    }

    @Override
    protected String getSelectedStyleClass() {
        return null;
    }
}
