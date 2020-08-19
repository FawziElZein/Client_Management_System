package com.clientsinfo.ui.purchases;

import com.clientsinfo.ui.clients.Client;
import com.clientsinfo.ui.purchases.categories.Category;

import java.io.Serializable;
import java.util.Date;

public class Purchase implements Serializable {

    private String id;
    private Client client;
    private Category category;
    private Date date;
    private double weight;
    private double cash;
    private double debt;
    private double check;
    private double outlay;
    private String note;

    public Purchase() {

    }


    public Purchase(String id, Client client, Category category, Date date, double weight, double cash, double debt, double check, double outlay, String note) {
        this.id = id;
        this.client = client;
        this.category = category;
        this.date = date;
        this.weight = weight;
        this.cash = cash;
        this.debt = debt;
        this.check = check;
        this.outlay = outlay;
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getOutlay() {
        return outlay;
    }

    public void setOutlay(double outlay) {
        this.outlay = outlay;
    }

    public double getCheck() {
        return check;
    }

    public void setCheck(double check) {
        this.check = check;
    }

    public double getCash() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash = cash;
    }

    public double getDebt() {
        return debt;
    }

    public void setDebt(double debt) {
        this.debt = debt;
    }


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
