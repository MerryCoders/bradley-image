package com.merrycoders.bradleyimage

class BradleyImageTagLib {

    static namespace = "bradleyImage"

    //static defaultEncodeAs = 'html'
    //static encodeAsForTags = [tagName: 'raw']

    /**
     * This tag is built upon the Ajax Uploader plugin.  The main purpose is to supply sensible defaults.
     *
     * @attr id (optional) - Id used for this instance of the uploader.  Defaults to "2'
     */
    def batchUploadButton = { attrs, body ->

        String id = attrs.id ?: "2"

        out << render(
                template: "/bradleyImageTag/batchUploadButton",
                model: [id: id]
        )


    }
}
