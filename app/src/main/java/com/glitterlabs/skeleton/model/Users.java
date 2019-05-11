package com.glitterlabs.skeleton.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Users {

    private String mName;
    private String mMobile;
    private String mAddress;
    private String mPicUrl;
    private String mUserID;

    public Users(){

    }

    public String getmName() {
        return mName;
    }

    public Users(String mUserID, String mName, String mAddress, String mPicUrl){

        this.mUserID = mUserID;
        this.mName = mName;
        this.mAddress = mAddress;
        this.mPicUrl = mPicUrl;

    }

    public Users(String mUserID, String mMobile, String mName, String mAddress, String mPicUrl){

        this.mUserID = mUserID;
        this.mMobile = mMobile;
        this.mName = mName;
        this.mAddress = mAddress;
        this.mPicUrl = mPicUrl;

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

    public String getmPicUrl() {
        return mPicUrl;
    }

    public void setmPicUrl(String mPicUrl) {
        this.mPicUrl = mPicUrl;
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
        result.put("mPicUrl", mPicUrl);



        return result;
    }
}
