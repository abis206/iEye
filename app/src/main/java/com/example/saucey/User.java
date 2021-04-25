package com.example.saucey;

import android.widget.ImageView;

public class User {
    public String fullname, email, bio, username,location,fire;
    public ImageView profilePic;

    public User(){

    }

    public User(String fullname, String email, String bio, String username, String location, String fire, ImageView profilePic){
        this.fullname = fullname;
        this.email = email;
        this.bio = bio;
        this.username = username;
        this.location = location;
        this.fire = fire;
        this.profilePic = profilePic;
    }

}
