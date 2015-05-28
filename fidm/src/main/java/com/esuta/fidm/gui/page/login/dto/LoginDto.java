package com.esuta.fidm.gui.page.login.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *  @author shood
 * */
public class LoginDto implements Serializable{

    public static final String F_NAME = "name";
    public static final String F_PASSWORD = "password";
    public static final String F_LOGIN_TO_RESOURCE = "loginToResource";
    public static final String F_LOGIN_TO_REMOTE_RESOURCE = "loginToRemoteResource";
    public static final String F_RESOURCE_NAME = "resourceName";
    public static final String F_FEDERATION_MEMBER_NAME = "federationMemberName";
    public static final String F_REMOTE_RESOURCE_NAME = "remoteResourceName";

    private String name;
    private String password;
    private boolean loginToResource;
    private boolean loginToRemoteResource;
    private String resourceName;
    private String federationMemberName;
    private String remoteResourceName;
    private List<String> remoteResourceNameList;

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

    public boolean isLoginToRemoteResource() {
        return loginToRemoteResource;
    }

    public void setLoginToRemoteResource(boolean loginToRemoteResource) {
        this.loginToRemoteResource = loginToRemoteResource;
    }

    public String getFederationMemberName() {
        return federationMemberName;
    }

    public void setFederationMemberName(String federationmemberName) {
        this.federationMemberName = federationmemberName;
    }

    public String getRemoteResourceName() {
        return remoteResourceName;
    }

    public void setRemoteResourceName(String remoteResourceName) {
        this.remoteResourceName = remoteResourceName;
    }

    public List<String> getRemoteResourceNameList() {
        if(remoteResourceNameList == null){
            remoteResourceNameList = new ArrayList<>();
        }

        return remoteResourceNameList;
    }

    public void setRemoteResourceNameList(List<String> remoteResourceNameList) {
        this.remoteResourceNameList = remoteResourceNameList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoginDto loginDto = (LoginDto) o;

        if (loginToResource != loginDto.loginToResource) return false;
        if (loginToRemoteResource != loginDto.loginToRemoteResource) return false;
        if (name != null ? !name.equals(loginDto.name) : loginDto.name != null) return false;
        if (password != null ? !password.equals(loginDto.password) : loginDto.password != null) return false;
        if (resourceName != null ? !resourceName.equals(loginDto.resourceName) : loginDto.resourceName != null)
            return false;
        if (federationMemberName != null ? !federationMemberName.equals(loginDto.federationMemberName) : loginDto.federationMemberName != null)
            return false;
        if (remoteResourceName != null ? !remoteResourceName.equals(loginDto.remoteResourceName) : loginDto.remoteResourceName != null)
            return false;
        return !(remoteResourceNameList != null ? !remoteResourceNameList.equals(loginDto.remoteResourceNameList) : loginDto.remoteResourceNameList != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (loginToResource ? 1 : 0);
        result = 31 * result + (loginToRemoteResource ? 1 : 0);
        result = 31 * result + (resourceName != null ? resourceName.hashCode() : 0);
        result = 31 * result + (federationMemberName != null ? federationMemberName.hashCode() : 0);
        result = 31 * result + (remoteResourceName != null ? remoteResourceName.hashCode() : 0);
        result = 31 * result + (remoteResourceNameList != null ? remoteResourceNameList.hashCode() : 0);
        return result;
    }
}
