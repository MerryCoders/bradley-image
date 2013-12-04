package com.merrycoders.bradleyimage

class BradleyImageController {

    public def batchUpload() {

        def bradleyImageInstanceList = BradleyImage.list([max: 10, sort: "lastUpdated"])

        [
                imageInstanceList: bradleyImageInstanceList,
                imageType: params?.imageType
        ]

    }

}
