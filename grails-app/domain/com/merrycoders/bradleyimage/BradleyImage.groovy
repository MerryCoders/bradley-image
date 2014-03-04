package com.merrycoders.bradleyimage

class BradleyImage {

    String name
    String extension
    // ImageType imageType
    // UrlStructure category
    // MimeType mimeType
    String altText
    String caption
    String titleText
    String pHash
    Date lastUpdated
    Date dateCreated

    static constraints = {
        //imageType(nullable: true)
        //category(nullable: true)

        //mimeType(nullable: true)

        altText(nullable: true)
        caption(nullable: true)
        titleText(nullable: true)
        pHash(nullable: true)
    }

    static mapping = {
        //category fetch: 'join'
    }

    /**
     *
     * @return List of all ScaleImage instances generated from the original, BradleyImage instance
     */
    List<ScaledImage> getScaledImages() {
        if (id) ScaledImage.findAllByBradleyImage(this)
    }

    /**
     *
     * @return The original ScaleImage instance
     */
    ScaledImage getOriginalScaledImage() {
        if (id) ScaledImage.findByBradleyImageAndOriginal(this, true)
    }

    String toString() {
        "${name}.${extension}"
    }

}
