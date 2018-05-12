package com.waysideutilities.waysidetruckfreights.PojoClasses;

/**
 * Created by Archana on 17/3/2017.
 */

public class Request {

    public String receiver_id;
    public String received_message;
    public String sent_message;
    public String sender_id;
    public String load_id;
    public String posttruck_id;
    public String rtruck_id;
    public String request_status;
    public String receiver_number;
    public String sender_number;

    public String getPaid_charges() {
        return paid_charges;
    }

    public void setPaid_charges(String paid_charges) {
        this.paid_charges = paid_charges;
    }

    public String getRemaining_charges() {
        return remaining_charges;
    }

    public void setRemaining_charges(String remaining_charges) {
        this.remaining_charges = remaining_charges;
    }

    public String getTotal_charges() {
        return total_charges;
    }

    public void setTotal_charges(String total_charges) {
        this.total_charges = total_charges;
    }

    public String paid_charges;
    public String remaining_charges;
    public String total_charges;


    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getRtruck_id() {
        return rtruck_id;
    }

    public void setRtruck_id(String rtruck_id) {
        this.rtruck_id = rtruck_id;
    }

    public String getPosttruck_id() {
        return posttruck_id;
    }

    public void setPosttruck_id(String posttruck_id) {
        this.posttruck_id = posttruck_id;
    }

    public String getRequest_status() {
        return request_status;
    }

    public void setRequest_status(String request_status) {
        this.request_status = request_status;
    }

    public String getLoad_id() {
        return load_id;
    }

    public void setLoad_id(String load_id) {
        this.load_id = load_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getReceived_message() {
        return received_message;
    }

    public void setReceived_message(String received_message) {
        this.received_message = received_message;
    }

    public String getSent_message() {
        return sent_message;
    }

    public void setSent_message(String sent_message) {
        this.sent_message = sent_message;
    }
    public String getReceiver_number() {
        return receiver_number;
    }

    public void setReceiver_number(String receiver_number) {
        this.receiver_number = receiver_number;
    }

    public String getSender_number() {
        return sender_number;
    }

    public void setSender_number(String sender_number) {
        this.sender_number = sender_number;
    }

}
