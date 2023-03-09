package com.example.Maatran;

import android.os.Parcel;
import android.os.Parcelable;

//Class used for storing user details in the form of a parcel
//Complex objects cannot be passed as Bundles between different intents ('Activity' classes)
//Thus they must be converted to 'Parcelable' objects
//More documentation can be found here: https://developer.android.com/reference/android/os/Parcelable
//https://developer.android.com/guide/components/activities/parcelables-and-bundles#java

public class User implements Parcelable {
    private String name, gender, mobile, address, emergency, locality;
    private String family_id;
    private String admin_id="null";
    private long age;
    private String android_id;

    public User()
    {

    }

    //parameterized constructor for constructing a user object
    public User(String name, long age, String gender, String mobile, String address, String emergency, String locality) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.mobile = mobile;
        this.address = address;
        this.emergency = emergency;
        this.locality = locality;
    }

    //constructor for creating a User.class object from type Parcel
    protected User(Parcel in) {
        name = in.readString();
        gender = in.readString();
        mobile = in.readString();
        address = in.readString();
        emergency = in.readString();
        locality = in.readString();
        age = in.readLong();
    }

    //non-null static field used for implementing the Parcelable interface
    public static final Creator<User> CREATOR = new Creator<User>() {

        //Takes a parcel object as input and creates a User.class object from it
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        //Creates an array of User.class objects
        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };


    //getter methods for accessing the fields of the User.class object
    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getMobile() {
        return mobile;
    }

    public String getAddress() {
        return address;
    }

    public String getEmergency() {
        return emergency;
    }

    public long getAge() {
        return age;
    }

    //setter methods for setting the fields of the User.class object
    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmergency(String emergency) {
        this.emergency = emergency;
    }

    public void setAge(long age) {
        this.age = age;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //writeToParcel method for writing the fields of the User.class object to a parcel
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(gender);
        parcel.writeString(mobile);
        parcel.writeString(address);
        parcel.writeString(emergency);
        parcel.writeString(locality);
        parcel.writeLong(age);
    }

    public String getAndroid_id() {
        return android_id;
    }

    public void setAndroid_id(String android_id) {
        this.android_id = android_id;
    }

    public String getFamily_id() {
        return family_id;
    }

    public void setFamily_id(String family_id) {
        this.family_id = family_id;
    }

    public String getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(String admin_id) {
        this.admin_id = admin_id;
    }
}
