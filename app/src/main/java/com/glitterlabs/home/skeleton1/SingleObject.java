package com.glitterlabs.home.skeleton1;

public class SingleObject {

    private static SingleObject idInstance= new SingleObject();

    private SingleObject(){}

    public static SingleObject getIdInstance(){
        return getIdInstance();
    }


}
