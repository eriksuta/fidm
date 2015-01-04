package com.esuta.fidm.gui.component.data.table;

import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.parser.XmlTag;
import org.apache.wicket.model.Model;

/**
 *  @author shood
 *
 *  based on implementation by lazyman,
 *  https://github.com/Evolveum/midpoint/blob/a6c023945dbea34db69a8ff17c9a61b7184c42cc/gui/admin-gui/src/main/java/com/evolveum/midpoint/web/component/data/paging/NavigatorPageLink.java
 * */
public abstract class PaginationPageLink extends AjaxLink<String>{

    private final long pageNumber;

    public PaginationPageLink(String id, long pageNumber) {
        super(id, new Model<>(Long.toString(pageNumber + 1)));
        this.pageNumber = pageNumber;
    }

    @Override
    public void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {
        replaceComponentTagBody(markupStream, openTag, getDefaultModelObjectAsString());
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        if (tag.isOpenClose()) {
            tag.setType(XmlTag.TagType.OPEN);
        }
    }

    public long getPageNumber() {
        return pageNumber;
    }
}
