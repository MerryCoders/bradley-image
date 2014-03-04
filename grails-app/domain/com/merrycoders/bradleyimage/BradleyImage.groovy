package com.merrycoders.bradleyimage

class BradleyImage {

    def grailsMimeUtility

    String name
    String extension
    // ImageType imageType
    // UrlStructure category
    MimeType mimeType = calculateMimeType()
    String altText
    String caption
    String titleText
    String pHash
    Date lastUpdated
    Date dateCreated

    static constraints = {
        //imageType(nullable: true)
        //category(nullable: true)

        mimeType(nullable: true)
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

    private MimeType calculateMimeType() {

        if (extension) {
            def mimeType = grailsMimeUtility.getMimeTypeForExtension(extension)
            MimeType guessedMimeType = mimeType ? MimeType.findOrSaveByName(mimeType?.name) : MimeType.findByName("application/octet-stream")
            return guessedMimeType
        }

    }

    String toString() {
        "${name}.${extension}"
    }

}
