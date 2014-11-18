package com.esuta.fidm.gui.component;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.List;

/**
 *  @author shood
 * */
public class CustomFeedbackPanel extends FeedbackPanel{

    private static final String ID_FEEDBACK = "feedback";
    private static final String ID_MESSAGES = "messages";
    private static final String ID_MESSAGE = "message";

    public CustomFeedbackPanel(final String id){
        this(id, null);
    }

    public CustomFeedbackPanel(final String id, IFeedbackMessageFilter filter){
        super(id);

        get("feedbackul:messages").add(new AttributeModifier("class", ""));
    }

    @Override
    protected String getCSSClass(FeedbackMessage message) {
        String messageType = message.getLevelAsString();

        if("ERROR".equals(messageType) || "FATAL".equals(messageType)){
            return "alert alert-danger alert-dismissable";
        } else if("INFO".equals(messageType) || "DEBUG".endsWith(messageType)){
            return "alert alert-info alert-dismissable";
        }else if("WARNING".equals(messageType)){
            return "alert alert-warning alert-dismissable";
        } else if("SUCCESS".equals(messageType)){
            return "alert alert-success alert-dismissable";
        }

        return "alert alert-info alert-dismissable";
    }
}
