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

}
