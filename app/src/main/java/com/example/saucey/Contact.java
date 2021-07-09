package com.example.saucey;

import android.net.Uri;
import android.widget.TextView;

public class Contact {

    public String email,phoneNumber,fullName,address,imageKey, retrievalCode;

    public Contact(){

    }

    public Contact(String email,String phoneNumber,String fullName,String address, String imageKey, String retrievalCode){
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.address = address;
        this.imageKey = imageKey;
        this.retrievalCode = retrievalCode;
    }
}
