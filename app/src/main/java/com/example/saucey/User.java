package com.example.saucey;

import android.widget.ImageView;

import java.util.ArrayList;

public class User {
    public String fullname, email, gender,location,age;
    public ArrayList<String> contactList;

    public User(){

    }

    public User(String fullname, String email, String gender, String location, String age, ArrayList<String> contactList){
        this.fullname = fullname;
        this.email = email;
        this.gender = gender;
        this.location = location;
        this.age = age;
        this.contactList = contactList;
    }

}
