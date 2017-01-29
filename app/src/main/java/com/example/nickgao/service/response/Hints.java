package com.example.nickgao.service.response;


/**
 * Created by nick.gao on 2014/7/16.
 */
public class Hints {
    private HintNode appVersionUpgrade;

    public HintNode getAppVersionUpgrade() {
        return appVersionUpgrade;
    }

    public void setAppVersionUpgrade(HintNode appVersionUpgrade) {
        this.appVersionUpgrade = appVersionUpgrade;
    }

    private HintNode trialState;

    public HintNode getTrialState() {
        return trialState;
    }

    public void setTrialState(HintNode trialState) {
        this.trialState = trialState;
    }
}
