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
        });
    }

    public void editPerformed(AjaxRequestTarget target, IModel<T> rowModel){

    }

    public void removePerformed(AjaxRequestTarget target, IModel<T> rowModel){

    }
}
