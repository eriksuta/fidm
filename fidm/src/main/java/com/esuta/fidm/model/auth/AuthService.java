package com.esuta.fidm.model.auth;

import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.model.IModelService;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.model.ProvisioningService;
import com.esuta.fidm.repository.schema.core.AccountType;
import com.esuta.fidm.repository.schema.core.AssignmentType;
import com.esuta.fidm.repository.schema.core.ResourceType;
import com.esuta.fidm.repository.schema.core.UserType;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 * */
public class AuthService implements IAuthService{

    private static final Logger LOGGER = Logger.getLogger(AuthService.class);

    /**
     *  Single AuthService instance
     * */
    private static AuthService instance = null;

    /**
     *  An instance of model service - to check the user data
     * */
    private IModelService modelService;

    /**
     *  An instance of provisioning service - to perform a check, if
     *  user is trying to log to resource in federation.
     * */
    private ProvisioningService provisioningService;

    private AuthService(){
        modelService = ModelService.getInstance();
        provisioningService = ProvisioningService.getInstance();
    }

    public static AuthService getInstance(){
        if(instance == null){
            instance = new AuthService();
        }

        return instance;
    }

    @Override
    public AuthResult login(String name, String password) throws DatabaseCommunicationException {
        if(name == null || password == null ||
                StringUtils.isEmpty(name) || StringUtils.isEmpty(password)){
            return AuthResult.NO_ACCOUNT;
        }

        UserType user = modelService.readObjectByName(UserType.class, name);

        if(user == null){
            LOGGER.error("Account with name: '" + name + "' does not exits.");
            return AuthResult.NO_ACCOUNT;
        }

        if(!password.equals(user.getPassword())){
            LOGGER.error("Bad password for account with name: '" + name + "'.");
            return AuthResult.BAD_PASSWORD;
        }

        return AuthResult.SUCCESS;
    }

    @Override
    public AuthResult loginToResource(String name, String password, String resourceName) throws DatabaseCommunicationException {
        if(name == null || password == null || resourceName == null ||
                StringUtils.isEmpty(name) || StringUtils.isEmpty(password) || StringUtils.isEmpty(resourceName)){
            return AuthResult.NO_ACCOUNT;
        }

        UserType user = modelService.readObjectByName(UserType.class, name);

        if(user == null){
            LOGGER.error("Account with name: '" + name + "' does not exits.");
            return AuthResult.NO_ACCOUNT;
        }

        List<AssignmentType> assignments = user.getAccounts();

        if(assignments.isEmpty()){
            LOGGER.error("Account with name: '" + name + "' does not exit on resource: '" + resourceName + "'.");
            return AuthResult.NO_ACCOUNT_ON_RESOURCE;
        }

        List<AccountType> accounts = new ArrayList<>();
        for(AssignmentType assignment: assignments){
            accounts.add(modelService.readObject(AccountType.class, assignment.getUid()));
        }

        List<ResourceType> resources = new ArrayList<>();
        for(AccountType account: accounts){
            resources.add(modelService.readObject(ResourceType.class, account.getResource().getUid()));
        }

        ResourceType resourceToLogin = null;
        for(ResourceType resource: resources){
            if(resourceName.equals(resource.getName())){
                resourceToLogin = resource;
            }
        }

        if(resourceToLogin == null){
            LOGGER.error("Account with name: '" + name + "' does not exit on resource: '" + resourceName + "'.");
            return AuthResult.NO_ACCOUNT_ON_RESOURCE;
        }

        AccountType account = null;
        for(AccountType acc: accounts){
            if(resourceToLogin.getUid().equals(acc.getResource().getUid())){
                account = acc;
            }
        }

        if(account == null){
            LOGGER.error("Account with name: '" + name + "' does not exit on resource: '" + resourceName + "'.");
            return AuthResult.NO_ACCOUNT_ON_RESOURCE;
        }

        if(!password.equals(account.getPassword())){
            LOGGER.error("Bad password for account with name: '" + name + "'.");
            return AuthResult.BAD_PASSWORD;
        }

        return AuthResult.SUCCESS;
    }
}
