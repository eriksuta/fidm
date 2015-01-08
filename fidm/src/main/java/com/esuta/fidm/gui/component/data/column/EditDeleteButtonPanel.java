package com.esuta.fidm.gui.component.data.column;

import com.esuta.fidm.gui.component.behavior.VisibleEnableBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *  @author shood
 * */
public class EditDeleteButtonPanel extends Panel{

    private static final String ID_EDIT = "edit";
    private static final String ID_REMOVE = "remove";

    private IModel<Boolean> editVisible = new Model<>(Boolean.TRUE);
    private IModel<Boolean> removeVisible = new Model<>(Boolean.TRUE);

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
        edit.add(new VisibleEnableBehavior(){

            @Override
            public boolean isVisible() {
                return getEditVisible();
            }
        });
        add(edit);

        AjaxLink remove = new AjaxLink(ID_REMOVE) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                removePerformed(target);
            }
        };
        remove.add(new VisibleEnableBehavior(){

            @Override
            public boolean isVisible() {
                return getRemoveVisible();
            }
        });
        add(remove);
    }

    public boolean getEditVisible(){
        return editVisible.getObject();
    }

    public boolean getRemoveVisible(){
        return removeVisible.getObject();
    }

    protected void editPerformed(AjaxRequestTarget target){

    }

    protected void removePerformed(AjaxRequestTarget target){

    }
}
