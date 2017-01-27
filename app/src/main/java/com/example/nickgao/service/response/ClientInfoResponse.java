package com.example.nickgao.service.response;



/**
 * Created by nick.gao on 2014/7/16.
 */
public class ClientInfoResponse extends AbstractResponse {
    Client client;
    Provisioning provisioning;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Provisioning getProvisioning() {
        return provisioning;
    }

    public void setProvisioning(Provisioning provisioning) {
        this.provisioning = provisioning;
    }
}
