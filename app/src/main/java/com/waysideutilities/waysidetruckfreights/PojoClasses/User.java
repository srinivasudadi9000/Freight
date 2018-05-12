package com.waysideutilities.waysidetruckfreights.PojoClasses;

/**
 * Created by Archana on 1/4/2017.
 */
public class User {
    public String userName;
    public String email = null;
    public String contact_number = null;
    public String emg_contact_number;
    public String city;
    public String password;
    public String who_u_r;

    //Truck Owner Details
    public String truckOwnerName;
    public String truckOwnerCity;
    public String truckOwnerNo;
    public String truckOwnerPanNo;
    public String truckOwnerAddress;

    //Truck Owner Bank details
    public String truckOwnerAccName;
    public String truckOwnerAccNo;
    public String truckOwnerBankIFSCNo;
    public String truckOwnerBankName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getWho_u_r() {
        return who_u_r;
    }

    public void setWho_u_r(String who_u_r) {
        this.who_u_r = who_u_r;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmg_contact_number() {
        return emg_contact_number;
    }

    public void setEmg_contact_number(String emg_contact_number) {
        this.emg_contact_number = emg_contact_number;
    }


    public String getTruckOwnerName() {
        return truckOwnerName;
    }

    public void setTruckOwnerName(String truckOwnerName) {
        this.truckOwnerName = truckOwnerName;
    }

    public String getTruckOwnerCity() {
        return truckOwnerCity;
    }

    public void setTruckOwnerCity(String truckOwnerCity) {
        this.truckOwnerCity = truckOwnerCity;
    }

    public String getTruckOwnerNo() {
        return truckOwnerNo;
    }

    public void setTruckOwnerNo(String truckOwnerNo) {
        this.truckOwnerNo = truckOwnerNo;
    }

    public String getTruckOwnerPanNo() {
        return truckOwnerPanNo;
    }

    public void setTruckOwnerPanNo(String truckOwnerPanNo) {
        this.truckOwnerPanNo = truckOwnerPanNo;
    }

    public String getTruckOwnerAddress() {
        return truckOwnerAddress;
    }

    public void setTruckOwnerAddress(String truckOwnerAddress) {
        this.truckOwnerAddress = truckOwnerAddress;
    }

    public String getTruckOwnerAccName() {
        return truckOwnerAccName;
    }

    public void setTruckOwnerAccName(String truckOwnerAccName) {
        this.truckOwnerAccName = truckOwnerAccName;
    }

    public String getTruckOwnerAccNo() {
        return truckOwnerAccNo;
    }

    public void setTruckOwnerAccNo(String truckOwnerAccNo) {
        this.truckOwnerAccNo = truckOwnerAccNo;
    }

    public String getTruckOwnerBankIFSCNo() {
        return truckOwnerBankIFSCNo;
    }

    public void setTruckOwnerBankIFSCNo(String truckOwnerBankIFSCNo) {
        this.truckOwnerBankIFSCNo = truckOwnerBankIFSCNo;
    }

    public String getTruckOwnerBankName() {
        return truckOwnerBankName;
    }

    public void setTruckOwnerBankName(String truckOwnerBankName) {
        this.truckOwnerBankName = truckOwnerBankName;
    }

}
