package com.esuta.fidm.gui.component.data.column;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 *  @author shood
 * */
public class EditDeleteButtonColumn<T extends Serializable> extends AbstractColumn<T, String>{

    public EditDeleteButtonColumn(IModel<String> displayModel) {
        this(displayModel, null);
    }

    public EditDeleteButtonColumn(IModel<String> displayModel, String sortProperty) {
        super(displayModel, sortProperty);
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, final IModel<T> rowModel) {

        cellItem.add(new EditDeleteButtonPanel(componentId, rowModel){

            @Override
            protected void editPerformed(AjaxRequestTarget target) {
                EditDeleteButtonColumn.this.editPerformed(target, rowModel);
            }

            @Override
            protected void removePerformed(AjaxRequestTarget target) {
                EditDeleteButtonColumn.this.removePerformed(target, rowModel);
            }

            @Override
            public boolean isEditVisible() {
                return EditDeleteButtonColumn.this.getEditVisible();
            }

            @Override
            public boolean isRemoveVisible() {
                return EditDeleteButtonColumn.this.getRemoveVisible();
            }

            @Override
            public boolean isEditEnabled() {
                return EditDeleteButtonColumn.this.isEditEnabled();
            }

            @Override
            public boolean isRemoveEnabled() {
                return EditDeleteButtonColumn.this.isRemoveEnabled();
            }
        });
    }

    public boolean getEditVisible(){
        return true;
    }

    public boolean getRemoveVisible(){
        return true;
    }

    public boolean isEditEnabled(){
        return true;
    }

    public boolean isRemoveEnabled(){
        return true;
    }

    public void editPerformed(AjaxRequestTarget target, IModel<T> rowModel){}

    public void removePerformed(AjaxRequestTarget target, IModel<T> rowModel){}
}
