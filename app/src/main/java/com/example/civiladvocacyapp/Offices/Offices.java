package com.example.civiladvocacyapp.Offices;

import java.io.Serializable;

public class Offices implements Serializable {

    private String loc;
    private String off;
    private String name;
    private String party;
    private String address;
    private String phone;
    private String email;
    private String website;
    private String fb;
    private String tw;
    private String yt;
    private String image;

    private static int ctr = 1;

//    public Offices() {
//        this.off = "Offices Name " + ctr;
//        this.name = "Name " + ctr;
//        ctr++;
//    }

    public Offices(String loc, String off, String name, String party, String address, String phone, String email,
                   String website, String fb, String tw, String yt, String image) {
        this.loc = loc;
        this.off = off;
        this.name = name;
        this.party = party;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.website = website;
        this.fb = fb;
        this.tw = tw;
        this.yt = yt;
        this.image = image;
    }

    public String getLoc() {
        return loc;
    }

    public String getOff() {
        return off;
    }

    public String getName() {
        return name;
    }

    public String getParty() {
        return party;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getWebsite() {
        return website;
    }

    public String getFb() {
        return fb;
    }

    public String getTw() {
        return tw;
    }

    public String getYt() {
        return yt;
    }

    public String getImage() {
        return image;
    }

    public static int getCtr() {
        return ctr;
    }

    @Override
    public String toString() {
        return "Offices{" +
                "loc='" + loc + '\'' +
                ", off='" + off + '\'' +
                ", name='" + name + '\'' +
                ", party='" + party + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", website='" + website + '\'' +
                ", fb='" + fb + '\'' +
                ", tw='" + tw + '\'' +
                ", yt='" + yt + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
