package com.esuta.fidm.gui.component.data.column;

import com.esuta.fidm.gui.component.behavior.VisibleEnableBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 *  @author shood
 *
 *  based on implementation by lazyman,
 *  (https://github.com/Evolveum/midpoint/blob/a6c023945dbea34db69a8ff17c9a61b7184c42cc/gui/admin-gui/src/main/java/com/evolveum/midpoint/web/component/data/column/LinkPanel.java)
 * */
public class LinkPanel<T extends Serializable> extends Panel {

    private static final String ID_LINK = "link";
    private static final String ID_LABEL = "label";

    public LinkPanel(String id, IModel<String> label) {
        super(id);

        AjaxLink link = new AjaxLink(ID_LINK) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                LinkPanel.this.onClick(target);
            }

            @Override
            public String getBeforeDisabledLink() {
                return null;
            }

            @Override
            public String getAfterDisabledLink() {
                return null;
            }
        };
        link.add(new Label(ID_LABEL, label));
        link.add(new VisibleEnableBehavior() {

            @Override
            public boolean isEnabled() {
                return LinkPanel.this.isEnabled();
            }
        });
        add(link);
    }

    public boolean isEnabled() {
        return true;
    }

    /**
     *  Override to provide an action to be done when link is clicked
     * */
    public void onClick(AjaxRequestTarget target) {}
}
