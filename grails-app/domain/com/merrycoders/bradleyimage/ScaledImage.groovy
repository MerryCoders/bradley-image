package com.merrycoders.bradleyimage

import grails.plugin.lazylob.LazyBlob
import grails.plugin.lazylob.LazyBlobType

class ScaledImage {

    BradleyImage bradleyImage
    LazyBlob data
    BradleyImageSize imageSize
    Integer width
    Integer height
    Integer size = 0
    Boolean original = false

    static mapping = {
        imageSize(fetch: "join")
        data type: LazyBlobType, params: [propertyName: 'data']
    }

    static constraints = {
        width(min: 0)
        height(min: 0)
        size(min: 0)
        data(maxSize: 104857600)// 100 MB
    }

    static List<BradleyImage> getBradleyImages(BradleyImage bradleyImage) {
        def bradleyImageList = ScaledImage?.findAllByBradleyImage(bradleyImage)
        return bradleyImageList instanceof List ? bradleyImageList : [bradleyImageList]
    }

    boolean equals(other) {
        if (!(other instanceof ScaledImage)) {
            return false
        }
        other?.id == id
    }

    String toString() {
        "${bradleyImage?.name}.${bradleyImage?.extension}"
    }

}
