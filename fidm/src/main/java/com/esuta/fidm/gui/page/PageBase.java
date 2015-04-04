package com.esuta.fidm.gui.page;

import com.esuta.fidm.gui.component.CustomFeedbackPanel;
import com.esuta.fidm.gui.component.nav.RightNavigationMenu;
import com.esuta.fidm.gui.page.config.PageDebugList;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.model.ObjectChangeProcessor;
import com.esuta.fidm.model.federation.client.RestFederationServiceClient;
import com.esuta.fidm.repository.schema.core.FederationMemberType;
import com.esuta.fidm.repository.schema.core.SystemConfigurationType;
import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.List;

/**
 *  @author shood
 * */
public abstract class PageBase extends WebPage{

    private static final Logger LOGGER = Logger.getLogger(PageBase.class);

    /**
     *  Every page is able to communicate with model through this ModelService instance
     * */
    private transient ModelService modelService;

    /**
     *  Every page is able to communicate with other federation members using this federation service rest client instance
     * */
    private transient RestFederationServiceClient federationServiceClient;

    /**
     *  A component that processes changes in objects and provides a unified format for object modifications
     * */
    private transient ObjectChangeProcessor objectChangeProcessor;

    //Constant used to identify page parameter name (uid) - used when editing objects
    public static final String UID_PAGE_PARAMETER_NAME = "uid";

    //Other page parameter constants
    public static final String PAGE_ACCOUNT_RESOURCE_UID = "pageAccResourceUid";

    private static final String ID_TITLE = "title";
    private static final String ID_SUBTITLE = "subtitle";
    private static final String ID_RIGHT_MENU_PANEL = "rightMenuPanel";
    private static final String ID_FEEDBACK_CONTAINER = "feedbackContainer";
    private static final String ID_FEEDBACK = "feedback";

    public static final String SYSTEM_CONFIG_UID = "00000000-0000-0000-0000-000000000001";

    public PageBase(){
        this(null);
    }

    public PageBase(PageParameters parameters){
        super(parameters);

        initLayout();
    }

    private void initLayout(){
        Label title = new Label(ID_TITLE, createPageTitleModel());
        add(title);

        Label subtitle = new Label(ID_SUBTITLE, createPageSubtitleModel());
        add(subtitle);

        RightNavigationMenu rightMenu = new RightNavigationMenu(ID_RIGHT_MENU_PANEL, this.getClass());
        add(rightMenu);

        WebMarkupContainer feedbackContainer = new WebMarkupContainer(ID_FEEDBACK_CONTAINER);
        feedbackContainer.setOutputMarkupId(true);
        add(feedbackContainer);

        final FeedbackPanel feedback = new CustomFeedbackPanel(ID_FEEDBACK);
        feedback.setOutputMarkupId(true);
        feedbackContainer.add(feedback);
    }

    protected IModel<String> createPageTitleModel(){
        return createStringResource("page.title");
    }

    protected IModel<String> createPageSubtitleModel(){
        return createStringResource("page.subtitle");
    }

    public StringResourceModel createStringResource(String resourceKey, Object... objects){
        return new StringResourceModel(resourceKey, this, new Model<String>(), resourceKey, objects);
    }

    public ModelService getModelService(){
        if(modelService == null){
            modelService = ModelService.getInstance();
        }

        return modelService;
    }

    public RestFederationServiceClient getFederationServiceClient() {
        if(federationServiceClient == null){
            federationServiceClient = RestFederationServiceClient.getInstance();
        }

        return federationServiceClient;
    }

    public ObjectChangeProcessor getObjectChangeProcessor() {
        if(objectChangeProcessor == null){
            objectChangeProcessor = ObjectChangeProcessor.getInstance();
        }

        return objectChangeProcessor;
    }

    public WebMarkupContainer getFeedbackPanel(){
        return (WebMarkupContainer) get(ID_FEEDBACK_CONTAINER);
    }

    public SystemConfigurationType loadSystemConfiguration(){
        SystemConfigurationType systemConfiguration;

        try {
            systemConfiguration = getModelService().readObject(SystemConfigurationType.class, SYSTEM_CONFIG_UID);
        } catch (DatabaseCommunicationException exc){
            error("Couldn't retrieve system configuration object, uid: '" + SYSTEM_CONFIG_UID + "' from the repository. Reason: " + exc.getExceptionMessage());
            LOGGER.error("Couldn't retrieve system configuration object, uid: '" + SYSTEM_CONFIG_UID + "' from the repository. Reason: ", exc);
            throw new RestartResponseException(PageDebugList.class);
        }

        return systemConfiguration;
    }

    public FederationMemberType getFederationMemberByName(String federationMemberName){
        FederationMemberType memberToRetrieve = null;

        try {
            List<FederationMemberType> allMembers = getModelService().getAllObjectsOfType(FederationMemberType.class);

            for(FederationMemberType member: allMembers){
                if(member.getFederationMemberName().equals(federationMemberName)){
                    memberToRetrieve = member;
                }
            }
        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Could not retrieve all federation member from the repository.", e);
            error("Could not retrieve all federation member from the repository. Reason: " + e);
        }

        return memberToRetrieve;
    }
}
