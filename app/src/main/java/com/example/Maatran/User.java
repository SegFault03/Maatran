package com.example.Maatran;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    String name, gender, mobile, address, emergency;
    long age;

    public User()
    {

    }

    public User(String name, long age, String gender, String mobile, String address, String emergency) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.mobile = mobile;
        this.address = address;
        this.emergency = emergency;
    }

    protected User(Parcel in) {
        name = in.readString();
        gender = in.readString();
        mobile = in.readString();
        address = in.readString();
        emergency = in.readString();
        age = in.readLong();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    public long getAge() {
        return age;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(gender);
        parcel.writeString(mobile);
        parcel.writeString(address);
        parcel.writeString(emergency);
        parcel.writeLong(age);
    }
}
