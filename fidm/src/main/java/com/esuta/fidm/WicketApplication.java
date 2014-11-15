package com.esuta.fidm;

import com.esuta.fidm.gui.page.PageBase;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;

/**
 *  @author shood
 * */
public class WicketApplication extends WebApplication
{
	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage(){
		return PageBase.class;
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
