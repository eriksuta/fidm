package com.esuta.fidm.gui.page.users.dto;

import com.esuta.fidm.repository.schema.core.UserType;

import java.io.Serializable;

/**
 *  @author shood
 * */
public class UserTypeDto implements Serializable{

    public static final String F_USER = "user";
    public static final String F_PASSWORD = "password";
    public static final String F_PASSWORD_CONFIRM = "passwordConfirm";

    private UserType user;
    private String password;
    private String passwordConfirm;

    public UserTypeDto(){}

    public UserTypeDto(UserType user){
        this.user = user;
        this.password = user.getPassword();
        this.passwordConfirm = user.getPassword();
    }

    public UserType getUser() {
        return user;
    }

    public void setUser(UserType user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserTypeDto)) return false;

        UserTypeDto that = (UserTypeDto) o;

        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (passwordConfirm != null ? !passwordConfirm.equals(that.passwordConfirm) : that.passwordConfirm != null)
            return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (passwordConfirm != null ? passwordConfirm.hashCode() : 0);
        return result;
    }
}
