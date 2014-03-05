package com.merrycoders.bradleyimage.bootstrap

import com.merrycoders.bradleyimage.MimeType


/**
 * Supported image types are configurable by inserting/removing database entries
 */
class MimeTypeBootStrap {

    static public void init() {

        new MimeType(name: "image/gif").save()
        new MimeType(name: "image/jpeg").save()
        new MimeType(name: "image/pjpeg").save()
        new MimeType(name: "image/png").save()
        new MimeType(name: "image/svg+xml").save()
        new MimeType(name: "image/tiff").save()
        new MimeType(name: "image/vnd.microsoft.icon").save()

    }
}
