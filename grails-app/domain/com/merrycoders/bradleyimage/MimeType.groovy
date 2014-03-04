package com.merrycoders.bradleyimage

class MimeType {

    String name
    Date lastUpdated
    Date dateCreated

    static constraints = {}

    def getImages() {
        BradleyImage.findByMimeType(this, [sort: "name"])
    }

    boolean equals(other) {
        if (!(other instanceof MimeType)) {
            return false
        }
        other?.id == id
    }

    String toString() {
        name
    }

}
