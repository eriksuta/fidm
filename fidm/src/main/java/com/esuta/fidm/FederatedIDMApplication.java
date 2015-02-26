package com.esuta.fidm;

import com.esuta.fidm.gui.page.dashboard.PageDashboard;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.infra.exception.ObjectAlreadyExistsException;
import com.esuta.fidm.model.federation.RestFederationService;
import com.esuta.fidm.repository.api.RepositoryService;
import com.esuta.fidm.repository.schema.core.SystemConfigurationType;
import org.apache.log4j.Logger;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;

/**
 *  @author shood
 * */
public class FederatedIDMApplication extends WebApplication{

    Logger LOGGER = Logger.getLogger(FederatedIDMApplication.class);

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage(){
		return PageDashboard.class;
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

        //Initialization of system configuration object
        SystemConfigurationType systemConfig = new SystemConfigurationType();

//        PC configuration
        systemConfig.setUid("00000000-0000-0000-0000-000000000001");
        systemConfig.setDbConnectionFile("F:\\FIIT\\Ing\\Diplo\\_repository\\_db\\repository.odb");
        systemConfig.setIdentityProviderIdentifier("Local Identity Provider");
        systemConfig.setName("System Configuration - Initial");

//        NB configuration
//        systemConfig.setUid("00000000-0000-0000-0000-000000000001");
//        systemConfig.setDbConnectionFile("F:\\skola\\Ing\\Diplo\\_repository\\_DB\\repository.odb");
//        systemConfig.setIdentityProviderIdentifier("Local Identity Provider");
//        systemConfig.setName("System Configuration - Initial");

        //Initialization of RepositoryService
        RepositoryService.getInstance().initConnection(systemConfig);

        //save system configuration
        RepositoryService repositoryService = RepositoryService.getInstance();

        try {
            if(repositoryService.readObjectByName(SystemConfigurationType.class, systemConfig.getName()) == null){
                repositoryService.createObject(systemConfig);
                LOGGER.info("Creating initial system configuration object");
            }

        } catch (DatabaseCommunicationException | ObjectAlreadyExistsException e) {
            LOGGER.error("Could not read or save initial system configuration object", e);
        }

        //Initialization of RestFederationService
        RestFederationService.getInstance();

        //TODO - add initial configuration here
	}

    @Override
    protected void onDestroy() {
        //Clearing the resources (database connection) when wicket servlet is destroyed
        RepositoryService.getInstance().closeConnection();
    }
}
