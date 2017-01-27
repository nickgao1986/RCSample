package com.example.nickgao.service.response;

/**
 * Created by nick.gao on 2014/7/16.
 */
public class Provisioning extends AbstractModel {
    WebUris webUris;
    Hints hints;

    public Hints getHints() {
        return hints;
    }

    public void setHints(Hints hints) {
        this.hints = hints;
    }

    public WebUris getWebUris() {
        return webUris;
    }

    public void setWebUris(WebUris webUris) {
        this.webUris = webUris;
    }
}
