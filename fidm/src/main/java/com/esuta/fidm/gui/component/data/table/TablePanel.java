package com.esuta.fidm.gui.component.data.table;

import com.esuta.fidm.gui.component.behavior.VisibleEnableBehavior;
import org.apache.commons.lang3.Validate;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.List;

/**
 *  @author shood
 *
 *  implementation based on TablePanel implementation by lazyman,
 *  https://github.com/Evolveum/midpoint/blob/master/gui/admin-gui/src/main/java/com/evolveum/midpoint/web/component/data/TablePanel.java
 * */
public class TablePanel<T> extends Panel{

    private static final String ID_TABLE = "table";
    private static final String ID_PAGINATION = "pagination";

    private IModel<Boolean> showPaging = new Model<>(true);
    private IModel<Boolean> showCount = new Model<>(true);
    private IModel<Boolean> showHeader = new Model<>(true);

    public TablePanel(String id, ISortableDataProvider dataProvider, List<IColumn<T, String>> columns, int pageSize){
        super(id);

        Validate.notNull(dataProvider, "Data provider for table must not be null.");
        Validate.notNull(columns, "Columns of the table must not be null.");

        initLayout(columns, dataProvider, pageSize);
    }

    private void initLayout(List<IColumn<T, String>> columns, ISortableDataProvider dataProvider, int pageSize){
        DataTable<T, String> table = new DataTable<>(ID_TABLE, columns, dataProvider, pageSize);
        table.setOutputMarkupId(true);

        HeadersToolbar tableHeader = new HeadersToolbar(table, dataProvider);
        addVisibleBehaviour(tableHeader, showHeader);
        table.addTopToolbar(tableHeader);

        CountToolbar countToolbar = new CountToolbar(table);
        addVisibleBehaviour(countToolbar, showCount);
        table.addBottomToolbar(countToolbar);

        PaginationPanel pagination = new PaginationPanel(ID_PAGINATION, table, showPaging);
        addVisibleBehaviour(pagination, showPaging);
        add(pagination);

        add(table);
    }

    private void addVisibleBehaviour(Component comp, final IModel<Boolean> model) {
        comp.add(new VisibleEnableBehavior() {

            @Override
            public boolean isVisible() {
                return model.getObject();
            }
        });
    }

    public void setShowPaging(boolean showPaging) {
        this.showPaging.setObject(showPaging);
        this.showCount.setObject(showPaging);
    }

    public void setShowCount(boolean showCount) {
        this.showCount.setObject(showCount);
    }

    public void setShowHeader(boolean showHeader){
        this.showHeader.setObject(showHeader);
    }

    public DataTable getDataTable() {
        return (DataTable) get(ID_TABLE);
    }

    public void setCurrentPage(int currentPage) {
        if (currentPage < 0) {
            getDataTable().setCurrentPage(0);
        }

        getDataTable().setCurrentPage(currentPage);
    }
}
