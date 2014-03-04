package com.merrycoders.bradleyimage

class BradleyImageSize {
    //NAME_WIDTH_HEIGHT_SC
    String name
    Integer width
    Integer height
    Boolean scale = false
    Boolean crop = false
    Boolean fill = false

    static constraints = {
        name(unique: true)
        width(nullable: true)
        height(nullable: true)
    }

    boolean equals(other) {
        if (!(other instanceof BradleyImageSize)) {
            return false
        }

        other?.id == id
    }

    String toString() {
        return name
    }

}

