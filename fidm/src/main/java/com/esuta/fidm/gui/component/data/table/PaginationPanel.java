package com.esuta.fidm.gui.component.data.table;

import com.esuta.fidm.gui.component.behavior.VisibleEnableBehavior;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.AbstractRepeater;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

/**
 *  @author shood
 *
 *  based on NavigatorPanel implementation by lazyman,
 *  https://github.com/Evolveum/midpoint/blob/b7a0f587229342d3a91068d83c717748badf1fc8/gui/admin-gui/src/main/java/com/evolveum/midpoint/web/component/data/paging/NavigatorPanel.java
 *
 * */
public class PaginationPanel extends Panel {

    private int PAGING_SIZE = 5;

    private static final String ID_PREVIOUS = "previous";
    private static final String ID_PREVIOUS_LINK = "previousLink";
    private static final String ID_FIRST = "first";
    private static final String ID_FIRST_LINK = "firstLink";
    private static final String ID_DOTS = "dots";
    private static final String ID_NAVIGATION = "navigation";
    private static final String ID_PAGE_LINK = "pageLink";
    private static final String ID_NEXT = "next";
    private static final String ID_NEXT_LINK = "nextLink";

    private final IPageable table;
    private final IModel<Boolean> showPageListingModel;

    public PaginationPanel(String id, IPageable table, IModel<Boolean> showPageListingModel) {
        super(id);
        this.table = table;
        this.showPageListingModel = showPageListingModel;

        setOutputMarkupId(true);
        add(new VisibleEnableBehavior() {

            @Override
            public boolean isVisible() {
                return PaginationPanel.this.table.getPageCount() > 0;
            }
        });

        initLayout();
    }

    private void initLayout() {
        initPrevious();
        initFirst();
        initNavigation();
        initNext();
    }

    private void initPrevious() {
        WebMarkupContainer previous = new WebMarkupContainer(ID_PREVIOUS);
        previous.add(new AttributeModifier("class", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return isPreviousEnabled() ? "" : "disabled";
            }
        }));
        add(previous);
        AjaxLink previousLink = new AjaxLink(ID_PREVIOUS_LINK) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                previousPerformed(target);
            }
        };
        previousLink.add(new VisibleEnableBehavior() {

            @Override
            public boolean isEnabled() {
                return isPreviousEnabled();
            }
        });
        previous.add(previousLink);
    }

    private void initFirst() {
        WebMarkupContainer first = new WebMarkupContainer(ID_FIRST);
        first.add(new VisibleEnableBehavior() {

            @Override
            public boolean isVisible() {
                return BooleanUtils.isTrue(showPageListingModel.getObject()) && showFirstAndDots();
            }
        });
        add(first);
        AjaxLink firstLink = new AjaxLink(ID_FIRST_LINK) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                firstPerformed(target);
            }
        };
        first.add(firstLink);

        WebMarkupContainer dots = new WebMarkupContainer(ID_DOTS);
        dots.add(new VisibleEnableBehavior() {

            @Override
            public boolean isVisible() {
                return BooleanUtils.isTrue(showPageListingModel.getObject()) && showFirstAndDots();
            }
        });
        add(dots);
    }

    private void initNavigation() {
        IModel<Integer> model = new AbstractReadOnlyModel<Integer>() {

            @Override
            public Integer getObject() {
                int count = (int) table.getPageCount();
                if (count < PAGING_SIZE) {
                    return count;
                }

                return PAGING_SIZE;
            }
        };

        Loop navigation = new Loop(ID_NAVIGATION, model) {

            @Override
            protected void populateItem(final LoopItem item) {
                final PaginationPageLink pageLink = new PaginationPageLink(ID_PAGE_LINK,
                        computePageNumber(item.getIndex())) {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        pageLinkPerformed(target, getPageNumber());
                    }
                };
                item.add(pageLink);

                item.add(new AttributeModifier("class", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return table.getCurrentPage() == pageLink.getPageNumber() ? "active" : "";
                    }
                }));
            }
        };
        navigation.add(new VisibleEnableBehavior() {

            @Override
            public boolean isVisible() {
                return BooleanUtils.isTrue(showPageListingModel.getObject());
            }
        });
        add(navigation);
    }

    private void initNext() {
        WebMarkupContainer next = new WebMarkupContainer(ID_NEXT);
        next.add(new AttributeModifier("class", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return isNextEnabled() ? "" : "disabled";
            }
        }));
        add(next);

        AjaxLink nextLink = new AjaxLink(ID_NEXT_LINK) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                nextPerformed(target);
            }
        };
        nextLink.add(new VisibleEnableBehavior() {

            @Override
            public boolean isEnabled() {
                return isNextEnabled();
            }
        });
        next.add(nextLink);
    }

    private long computePageNumber(int loopIndex) {
        long current = table.getCurrentPage();
        long count = table.getPageCount();

        final long half = PAGING_SIZE / 2;

        long result;
        if (current - half <= 0) {
            result = loopIndex;
        } else if (current + half + 1 >= count) {
            result = count - PAGING_SIZE + loopIndex;
        } else {
            result = current - half + loopIndex;
        }

        if(count == 4 && current == 3){
            result++;
        }

        return result;
    }

    private boolean isPreviousEnabled() {
        return table.getCurrentPage() > 0;
    }

    private boolean showFirstAndDots() {
        return table.getCurrentPage() >= PAGING_SIZE - 1;
    }

    private void previousPerformed(AjaxRequestTarget target) {
        changeCurrentPage(target, table.getCurrentPage() - 1);
    }

    private void firstPerformed(AjaxRequestTarget target) {
        changeCurrentPage(target, 0);
    }

    private void nextPerformed(AjaxRequestTarget target) {
        changeCurrentPage(target, table.getCurrentPage() + 1);
    }

    private void pageLinkPerformed(AjaxRequestTarget target, long page) {
        changeCurrentPage(target, page);
    }

    private boolean isNextEnabled() {
        return table.getCurrentPage() + 1 < table.getPageCount();
    }

    private void changeCurrentPage(AjaxRequestTarget target, long page) {
        table.setCurrentPage(page);

        Component container = ((Component) table);

        while (container instanceof AbstractRepeater) {
            container = container.getParent();
        }

        target.add(container);
        target.add(this);
    }

}
