package com.clientsinfo.ui.clients;

import java.io.Serializable;

public class Client implements Serializable, Comparable<Client> {

    private String name;
    private String oldPhoneNumber;
    private String phoneNumber;
    private String address;

    public Client(String name, String phoneNumber, String address) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getOldPhoneNumber() {
        return oldPhoneNumber;
    }

    public void setOldPhoneNumber(String oldPhoneNumber) {
        this.oldPhoneNumber = oldPhoneNumber;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public int compareTo(Client client) {
        return this.name.compareTo(client.getName());
    }
}
