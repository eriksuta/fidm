package com.esuta.fidm.model.federation.client;

import com.esuta.fidm.model.auth.AuthResult;

/***
 *  @author shood
 */
public class AuthRestResponse extends SimpleRestResponse{

    private AuthResult result;

    public AuthRestResponse() {}

    public AuthRestResponse(int status, String message) {
        super(status, message);
    }

    public AuthRestResponse(int status, AuthResult result){
        setStatus(status);
        this.result = result;
    }

    public AuthResult getResult() {
        return result;
    }

    public void setResult(AuthResult result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AuthRestResponse that = (AuthRestResponse) o;

        return result == that.result;

    }

    @Override
    public int hashCode() {
        int result1 = super.hashCode();
        result1 = 31 * result1 + (result != null ? result.hashCode() : 0);
        return result1;
    }
}
