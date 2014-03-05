package com.merrycoders.bradleyimage

import com.merrycoders.bradleyimage.bootstrap.BradleyImageSizeBootStrap
import com.merrycoders.bradleyimage.bootstrap.MimeTypeBootStrap
import spock.lang.Specification

/**
 * This acts as a central test data repository for all Specification tests.  The goal is to reduce code duplication.
 */
class SpecificationDataCore extends Specification {

    def initAllData() {

        BradleyImageSizeBootStrap.init()
        MimeTypeBootStrap.init()

    }

}
