package com.esuta.fidm;

import com.esuta.fidm.gui.page.dashboard.PageDashboard;
import com.esuta.fidm.repository.api.RepositoryService;
import com.esuta.fidm.repository.schema.SystemConfigurationType;
import org.apache.wicket.Application;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;

/**
 *  @author shood
 * */
public class FederatedIDMApplication extends WebApplication
{
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
        systemConfig.setDbConnectionFile("F:\\FIIT\\Ing\\Diplo\\_repository\\_db\\repository.odb");

//        NB configuration
//        systemConfig.setDbConnectionFile("F:\\skola\\Ing\\Diplo\\_repository\\_DB\\repository.odb");

        //Initialization of RepositoryService
        RepositoryService.getInstance().initConnection(systemConfig);

        //TODO - add initial configuration here
	}
}
