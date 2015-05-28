package com.esuta.fidm;

import com.esuta.fidm.gui.page.dashboard.PageDashboard;
import com.esuta.fidm.gui.page.login.PageLogin;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.ObjectAlreadyExistsException;
import com.esuta.fidm.model.federation.service.RestFederationService;
import com.esuta.fidm.repository.api.RepositoryService;
import com.esuta.fidm.repository.schema.core.SystemConfigurationType;
import com.esuta.fidm.repository.schema.core.UserType;
import org.apache.log4j.Logger;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 *  @author shood
 * */
public class FederatedIDMApplication extends WebApplication{

    private static final Logger LOGGER = Logger.getLogger(FederatedIDMApplication.class);

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage(){
		return PageLogin.class;
	}

    @Override
    public RuntimeConfigurationType getConfigurationType() {
        return RuntimeConfigurationType.DEVELOPMENT;
    }

    /**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init(){
		super.init();
        System.setProperty("objectdb.conf", "D:\\skola\\Diplo\\_repository\\_db\\objectdb-2.4.0\\objectdb.conf");

//        Initialization of system configuration object
        SystemConfigurationType systemConfig = new SystemConfigurationType();

//        PC configuration
        systemConfig.setUid("00000000-0000-0000-0000-000000000001");

//        TODO - this is just a temporary hack, so we can run multiple application instances to test federation functions
        if(getLocalPort() == 8080){
            systemConfig.setDbConnectionFile("D:\\skola\\Diplo\\_repository\\_db\\repository.odb");
            systemConfig.setIdentityProviderIdentifier("Local-Identity-Provider-1");
        } else {
            systemConfig.setDbConnectionFile("D:\\skola\\Diplo\\_repository\\_db\\repository2.odb");
            systemConfig.setIdentityProviderIdentifier("Local-Identity-Provider-2");
        }
        systemConfig.setName("System Configuration - Initial");
        systemConfig.setPort(getLocalPort());
        systemConfig.setLocalAddress("localhost");

//        NB configuration
//        systemConfig.setUid("00000000-0000-0000-0000-000000000001");
//        if(getLocalPort() == 8080){
//            systemConfig.setDbConnectionFile("F:\\FIIT\\Ing\\Diplo\\_repository\\_db\\repository.odb");
//        } else {
//            systemConfig.setDbConnectionFile("F:\\FIIT\\Ing\\Diplo\\_repository\\_db\\repository2.odb");
//        }
//        systemConfig.setIdentityProviderIdentifier("Local Identity Provider");
//        systemConfig.setName("System Configuration - Initial");
//        systemConfig.setPort(getLocalPort());
//        systemConfig.setLocalAddress("localhost")

//        Initialization of RepositoryService
        RepositoryService.getInstance().initConnection(systemConfig);

//        save system configuration
        RepositoryService repositoryService = RepositoryService.getInstance();

        try {
            if(repositoryService.readObjectByName(SystemConfigurationType.class, systemConfig.getName()) == null){
                repositoryService.createObject(systemConfig);
                LOGGER.info("Creating initial system configuration object");
            }

            addAdministrator(repositoryService);

        } catch (DatabaseCommunicationException | ObjectAlreadyExistsException e) {
            LOGGER.error("Could not read or save initial system configuration object", e);
        }

//        Initialization of RestFederationService
        RestFederationService.getInstance();
	}

    private void addAdministrator(RepositoryService service) throws ObjectAlreadyExistsException, DatabaseCommunicationException {
        if(service.readObjectByName(UserType.class, "Administrator") != null){
            return;
        }

        UserType user = new UserType();
        user.setUid("00000000-0000-0000-0000-000000000002");
        user.setName("Administrator");
        user.setPassword("123456");
        service.createObject(user);
    }

    @Override
    protected void onDestroy() {
        //Clearing the resources (database connection) when wicket servlet is destroyed
        RepositoryService.getInstance().closeConnection();
    }

    private int getLocalPort(){
        WebAppContext.Context context = (WebAppContext.Context) getServletContext();
        return context.getContextHandler().getServer().getConnectors()[0].getPort();
    }
}
