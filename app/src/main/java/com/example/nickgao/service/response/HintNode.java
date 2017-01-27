package com.example.nickgao.service.response;


/**
 * Created by nick.gao on 2014/7/16.
 */
public class HintNode extends AbstractModel {
    boolean actionRequired;

    public boolean isActionRequired() {
        return actionRequired;
    }

    public void setActionRequired(boolean actionRequired) {
        this.actionRequired = actionRequired;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    long expiresIn;

}
