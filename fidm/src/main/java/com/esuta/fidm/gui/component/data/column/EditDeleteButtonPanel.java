package com.esuta.fidm.gui.component.data.column;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 *  @author shood
 * */
public class EditDeleteButtonPanel extends Panel{

    private static final String ID_EDIT = "edit";
    private static final String ID_REMOVE = "remove";

    public EditDeleteButtonPanel(String id) {
        this(id, null);
    }

    public EditDeleteButtonPanel(String id, IModel<?> model) {
        super(id, model);

        initLayout();
    }

    private void initLayout(){
        AjaxLink edit = new AjaxLink(ID_EDIT) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                editPerformed(target);
            }
        };
        add(edit);

        AjaxLink remove = new AjaxLink(ID_REMOVE) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                removePerformed(target);
            }
        };
        add(remove);
    }

    protected void editPerformed(AjaxRequestTarget target){

    }

    protected void removePerformed(AjaxRequestTarget target){

    }
}
