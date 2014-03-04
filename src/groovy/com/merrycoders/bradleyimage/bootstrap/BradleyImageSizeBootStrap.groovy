package com.merrycoders.bradleyimage.bootstrap

import com.merrycoders.bradleyimage.BradleyImageSize


/**
 * By default, only image requests for predefined BradleyImageSizes will be rendered.  This provides an extra layer of security, but it
 * may be turned off in Config.groovy
 */
class BradleyImageSizeBootStrap extends BaseBootStrap {

    static public void initDevData() {

        if (!BradleyImageSize.count()) {
            new BradleyImageSize(name: "original", width: null, height: null, crop: false, scale: false).save()
        }

    }

}
