package com.merrycoders.bradleyimage.bootstrap

abstract class BaseBootStrap {

    static public void initDevData() {}

    static public void initTestData() {
        initDevData()
    }

    static public void initProductionData() {
        initTestData()
    }
}
