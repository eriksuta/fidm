package com.esuta.fidm.gui.page.login.dto;

import java.io.Serializable;

/**
 *  @author shood
 * */
public class LoginDto implements Serializable{

    public static final String F_NAME = "name";
    public static final String F_PASSWORD = "password";
    public static final String F_LOGIN_TO_RESOURCE = "loginToResource";
    public static final String F_RESOURCE_NAME = "resourceName";

    private String name;
    private String password;
    private boolean loginToResource;
    private String resourceName;

    public LoginDto() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoginToResource() {
        return loginToResource;
    }

    public void setLoginToResource(boolean loginToResource) {
        this.loginToResource = loginToResource;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoginDto)) return false;

        LoginDto loginDto = (LoginDto) o;

        if (loginToResource != loginDto.loginToResource) return false;
        if (name != null ? !name.equals(loginDto.name) : loginDto.name != null) return false;
        if (password != null ? !password.equals(loginDto.password) : loginDto.password != null) return false;
        return !(resourceName != null ? !resourceName.equals(loginDto.resourceName) : loginDto.resourceName != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (loginToResource ? 1 : 0);
        result = 31 * result + (resourceName != null ? resourceName.hashCode() : 0);
        return result;
    }
}
