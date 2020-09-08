package com.desarrollosmoyan.movilyaDriver.Models;

public class ListcheclResponse {
    private boolean error;
    private listcheck listcheck;

    public boolean isError() {
        return error;
    }

    public com.desarrollosmoyan.movilyaDriver.Models.listcheck getListcheck() {
        return listcheck;
    }

    public ListcheclResponse(boolean error, com.desarrollosmoyan.movilyaDriver.Models.listcheck listcheck) {
        this.error = error;
        this.listcheck = listcheck;
    }
}
