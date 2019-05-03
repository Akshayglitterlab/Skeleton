package com.glitterlabs.home.skeleton1;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

class User {

    private String mName;
    private String mMobile;
    private String mAddress;
    private String mPicURL;
    private String mUserID;

    public User(){

    }

    public String getmName() {
        return mName;
    }

    public User(String mUserID, String mMobile, String mName, String mAddress, String mPicURL){

        this.mPicURL = mUserID;
        this.mMobile = mMobile;
        this.mName = mName;
        this.mAddress = mAddress;
        this.mPicURL = mPicURL;

    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmMobile() {
        return mMobile;
    }

    public void setmMobile(String mMobile) {
        this.mMobile = mMobile;
    }

    public String getmPicURL() {
        return mPicURL;
    }

    public void setmPicURL(String mPicURL) {
        this.mPicURL = mPicURL;
    }

    public String getmUserID() {
        return mUserID;
    }

    public void setmUserID(String mUserID) {
        this.mUserID = mUserID;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("mUserId", mUserID);
        result.put("mMobile", mMobile);
        result.put("mName", mName);
        result.put("mAddress", mAddress);
        result.put("mPicUrl", mPicURL);



        return result;
    }
}
