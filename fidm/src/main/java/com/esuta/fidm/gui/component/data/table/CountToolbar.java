package com.esuta.fidm.gui.component.data.table;

import com.esuta.fidm.gui.component.model.LoadableModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

/**
 *  @author shood
 *
 *  loosely based on implementation by lazyman,
 *  https://github.com/Evolveum/midpoint/blob/a6c023945dbea34db69a8ff17c9a61b7184c42cc/gui/admin-gui/src/main/java/com/evolveum/midpoint/web/component/data/CountToolbar.java
 * */
public class CountToolbar extends AbstractToolbar{

    private static final String ID_COUNT_COLUMN = "countColumn";
    private static final String ID_COUNT = "count";

    public CountToolbar(DataTable<?,?> table){
        super(table);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        WebMarkupContainer countContainer = new WebMarkupContainer(ID_COUNT_COLUMN);
        countContainer.add(AttributeModifier.replace("colspan", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return String.valueOf(getTable().getColumns().size());
            }
        }));
        add(countContainer);

        Label count = new Label(ID_COUNT, createCountModel());
        count.setRenderBodyOnly(true);
        countContainer.add(count);
    }

    private IModel<String> createCountModel() {

        return new LoadableModel<String>() {

            @Override
            protected String load() {
                long from = 0;
                long to = 0;
                long count = 0;

                IPageable table = getTable();

                if (table instanceof DataTable) {
                    DataTable dataTable = (DataTable) table;

                    from = table.getCurrentPage() * dataTable.getItemsPerPage() + 1;
                    to = from + dataTable.getItemsPerPage() - 1;
                    long itemCount = dataTable.getItemCount();
                    if (to > itemCount) {
                        to = itemCount;
                    }
                    count = itemCount;
                }

                if (count > 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Displaying ");
                    sb.append(from);
                    sb.append(" to ");
                    sb.append(to);
                    sb.append(" of ");
                    sb.append(count);
                    sb.append("matching results.");

                    return sb.toString();
                }

                return "No Matching Result Found";
            }
        };
    }
}
