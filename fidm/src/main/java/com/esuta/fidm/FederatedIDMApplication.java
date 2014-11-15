package com.esuta.fidm;

import com.esuta.fidm.gui.page.dashboard.PageDashboard;
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

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init(){
		super.init();

        //TODO - add initial configuration here
	}
}
