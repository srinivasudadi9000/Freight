package com.waysideutilities.waysidetruckfreights.PojoClasses;

/**
 * Created by Archana on 1/4/2017.
 */
public class Truck {
    //Driver Details
    // public String driverImage = null;
    public String driverName;
    public String driverNumber;
    public String driverLicenceImage;


    public String driver_Name;
    public String driver_contact_number;
    public String driver_licence_number;
    public String driver_licence_city;

    //Truck documents
    public String truckImage = null;
    //public String truckNumber;
    public String truckRegNumber;
    public String truckLoadPassing;
    public String truckInsProviderImage;
    public String truckRegistrationImage;
    public String truckCity;

    //Posr Truck details
    public String id;
    public String post_truck_id;
    public String date;
    public String from;
    public String to;
    public String load_Category;
    public String load_Capacity;
    public String contactNumber;
    public String ftl_ltl;
    public String charges;
    public String type_of_truck;
    public String load_description;
    public String book_status;
    public String trip_status;
    public String booked_by_id;

    public String truck_owner_id;
    public String userName;
    public String comment;
    public String rating;

    //Getter setter
    public String getPost_truck_id() {
        return post_truck_id;
    }

    public void setPost_truck_id(String post_truck_id) {
        this.post_truck_id = post_truck_id;
    }

    public String getDriver_Name() {
        return driver_Name;
    }

    public void setDriver_Name(String driver_Name) {
        this.driver_Name = driver_Name;
    }

    public String getDriver_contact_number() {
        return driver_contact_number;
    }

    public void setDriver_contact_number(String driver_contact_number) {
        this.driver_contact_number = driver_contact_number;
    }

    public String getDriver_licence_number() {
        return driver_licence_number;
    }

    public void setDriver_licence_number(String driver_licence_number) {
        this.driver_licence_number = driver_licence_number;
    }

    public String getDriver_licence_city() {
        return driver_licence_city;
    }

    public void setDriver_licence_city(String driver_licence_city) {
        this.driver_licence_city = driver_licence_city;
    }

    public String getBook_status() {
        return book_status;
    }

    public void setBook_status(String book_status) {
        this.book_status = book_status;
    }

    public String getTrip_status() {
        return trip_status;
    }

    public void setTrip_status(String trip_status) {
        this.trip_status = trip_status;
    }

    public String getBooked_by_id() {
        return booked_by_id;
    }

    public void setBooked_by_id(String booked_by_id) {
        this.booked_by_id = booked_by_id;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    /*public String getDriverImage() {
        return driverImage;
    }

    public void setDriverImage(String driverImage) {
        this.driverImage = driverImage;
    }*/

    public String getCharges() {
        return charges;
    }

    public void setCharges(String charges) {
        this.charges = charges;
    }

    public String getLoad_Capacity() {
        return load_Capacity;
    }

    public void setLoad_Capacity(String load_Capacity) {
        this.load_Capacity = load_Capacity;
    }


    public String getTruckImage() {
        return truckImage;
    }

    public void setTruckImage(String truckImage) {
        this.truckImage = truckImage;
    }

    public String getTruckInsProviderImage() {
        return truckInsProviderImage;
    }

    public void setTruckInsProviderImage(String truckInsProviderImage) {
        this.truckInsProviderImage = truckInsProviderImage;
    }

    public String getTruckRegistrationImage() {
        return truckRegistrationImage;
    }

    public void setTruckRegistrationImage(String truckRegistrationImage) {
        this.truckRegistrationImage = truckRegistrationImage;
    }

    public String getDriverLicenceImage() {
        return driverLicenceImage;
    }

    public void setDriverLicenceImage(String driverLicenceImage) {
        this.driverLicenceImage = driverLicenceImage;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverNumber() {
        return driverNumber;
    }

    public void setDriverNumber(String driverNumber) {
        this.driverNumber = driverNumber;
    }


   /* public String getTruckNumber() {
        return truckNumber;
    }

    public void setTruckNumber(String truckNumber) {
        this.truckNumber = truckNumber;
    }*/

    public String getTruckRegNumber() {
        return truckRegNumber;
    }

    public void setTruckRegNumber(String truckRegNumber) {
        this.truckRegNumber = truckRegNumber;
    }

    public String getTruckLoadPassing() {
        return truckLoadPassing;
    }

    public void setTruckLoadPassing(String truckLoadPassing) {
        this.truckLoadPassing = truckLoadPassing;
    }

    public String getTruckCity() {
        return truckCity;
    }

    public void setTruckCity(String truckCity) {
        this.truckCity = truckCity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getLoad_Category() {
        return load_Category;
    }

    public void setLoad_Category(String load_Category) {
        this.load_Category = load_Category;
    }

    public String getFtl_ltl() {
        return ftl_ltl;
    }

    public void setFtl_ltl(String ftl_ltl) {
        this.ftl_ltl = ftl_ltl;
    }

    public String getType_of_truck() {
        return type_of_truck;
    }

    public void setType_of_truck(String type_of_truck) {
        this.type_of_truck = type_of_truck;
    }

    public String getLoad_description() {
        return load_description;
    }

    public void setLoad_description(String load_description) {
        this.load_description = load_description;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTruck_owner_id() {
        return truck_owner_id;
    }

    public void setTruck_owner_id(String truck_owner_id) {
        this.truck_owner_id = truck_owner_id;
    }

    @Override
    public String toString() {
        return "Truck{" +
                "driverName='" + driverName + '\'' +
                ", driverNumber='" + driverNumber + '\'' +
               // ", truckNumber='" + truckNumber + '\'' +
                ", truckRegNumber='" + truckRegNumber + '\'' +
                ", truckLoadPassing='" + truckLoadPassing + '\'' +
                ", id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", load_Category='" + load_Category + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", ftl_ltl='" + ftl_ltl + '\'' +
                ", type_of_truck='" + type_of_truck + '\'' +
                ", load_description='" + load_description + '\'' +
                '}';
    }
}
