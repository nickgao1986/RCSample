package com.example.nickgao.service.response;

/**
 * Created by steve.chen on 7/7/14.
 */
public class RestPageResponse<T> extends RestListResponse<T> {

    private PageInfo paging;
    private PageNavigation navigation;


    public PageInfo getPaging() {
        return paging;
    }

    public void setPaging(PageInfo paging) {
        this.paging = paging;
    }

    public PageNavigation getNavigation() {
        return navigation;
    }

    public void setNavigation(PageNavigation navigation) {
        this.navigation = navigation;
    }
}
