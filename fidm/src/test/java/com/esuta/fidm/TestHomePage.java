package com.esuta.fidm;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;

/**
 * Simple test using the WicketTester
 */
public class TestHomePage
{
	private WicketTester tester;

	@Before
	public void setUp(){
		tester = new WicketTester(new FederatedIDMApplication());
	}
}
