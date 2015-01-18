package com.esuta.fidm.gui.page.users;

import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.repository.schema.AccountType;
import org.apache.log4j.Logger;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *  @author shood
 *
 *  TODO
 * */
public class PageAccount extends PageBase{

    private transient Logger LOGGER = Logger.getLogger(PageAccount.class);

    private IModel<AccountType> model;

    public PageAccount(){
        this(null);
    }

    public PageAccount(PageParameters parameters){
        super(parameters);
    }
}
