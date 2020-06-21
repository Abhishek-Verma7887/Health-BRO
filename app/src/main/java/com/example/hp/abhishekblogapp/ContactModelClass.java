package com.example.hp.abhishekblogapp;

public class ContactModelClass {
    private String D_name,Address,D_contact,D_imageURL,Speciality;
    ContactModelClass(){

    }
    ContactModelClass(String d_name,String address,String d_contact,String d_imageURL,String speciality){
        D_name=d_name;
        D_contact=d_contact;
        D_imageURL=d_imageURL;
        Address=address;
        Speciality=speciality;
    }
    public String getAddress(){return Address;
    }
    public void setAddress(String address){Address =address;}

    public String getD_name(){return D_name;
    }
    public void setD_name(String d_name){D_name=d_name;}

    public String getD_contact(){return D_contact;
    }
    public void setD_contact(String d_contact){D_contact=d_contact;}

    public String getD_imageURL(){return D_imageURL;
    }
    public void setD_imageURL(String d_imageURL){D_imageURL=d_imageURL;}

    public String getSpeciality(){return Speciality;
    }
    public void setSpeciality(String d_imageURL){Speciality=d_imageURL;}

}
