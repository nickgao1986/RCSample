package com.example.nickgao.service.response;

/**
 * Created by steve.chen on 7/7/14.
 */
public class PageNavigation {

    private PageUri firstPage;
    private PageUri lastPage;
    private PageUri nextPage;
    private PageUri previousPage;

    public PageUri getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(PageUri firstPage) {
        this.firstPage = firstPage;
    }

    public PageUri getLastPage() {
        return lastPage;
    }

    public void setLastPage(PageUri lastPage) {
        this.lastPage = lastPage;
    }

    public PageUri getNextPage() {
        return nextPage;
    }

    public void setNextPage(PageUri nextPage) {
        this.nextPage = nextPage;
    }

    public PageUri getPreviousPage() {
        return previousPage;
    }

    public void setPreviousPage(PageUri previousPage) {
        this.previousPage = previousPage;
    }

    public static class PageUri {
        String uri;

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }
    }
}
