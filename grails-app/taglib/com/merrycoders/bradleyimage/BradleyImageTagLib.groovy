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

    /**
     * Render an HTML image tag for a BradleyImageInstance
     *
     * @attr bradleyImageInstance
     * @attr title (optional)
     * @attr altText (optional)
     * @attr noSize (optional) - Don't print the size onto the image tag.  Defaults to false.
     */
    def bradleyImg = { attrs, body ->

        BradleyImage bradleyImageInstance = attrs.bradleyImage
        Long id = attrs.id
        String title = ""
        String altText = ""

        if (bradleyImageInstance) {

            title = (params.title ?: bradleyImageInstance.titleText ?: "").trim()
            altText = (params.altText ?: bradleyImageInstance?.altText ?: "").trim()

        } else if (id) {

            bradleyImageInstance = BradleyImage.get(id)
            title = (params.title ?: bradleyImageInstance.titleText ?: "").trim()
            altText = (params.altText ?: bradleyImageInstance?.altText ?: "").trim()

        }

        String imageTag = ""

        if (bradleyImageInstance) {
            String imageLink = buildImageLink(attrs)

            imageTag = "<img src=\"${imageLink}\""
            attrs.each { k, v ->
                if (k != "productImage" &&
                        k != "id" &&
                        k != "bradleyImage" &&
                        k != "noSize" &&
                        (!attrs.noSize || (k != 'width' && k != 'height'))) {
                    imageTag += " ${k}='${v}' "
                }
            }

            if (title.size()) {
                imageTag += " title='${title}'"
            }

            if (altText.size()) {
                imageTag += " alt='${altText}'"
            }

            imageTag += " />"
        }

        out << imageTag
    }

    /**
     *
     * @param attrs
     * @return String representation of the BradleyImage url
     */
    private String buildImageLink(Map attrs) {

        BradleyImage bradleyImageInstance = null

        if (attrs.bradleyImage && attrs.bradleyImage instanceof BradleyImage) {

            bradleyImageInstance = attrs.bradleyImage

        } else if (attrs.id) {

            bradleyImageInstance = BradleyImage.get(attrs.id as Long)

        }

        String imageLink = ""
        if (bradleyImageInstance) {

            Integer forcedHeight = attrs.height as Integer ?: null
            Integer forcedWidth = attrs.width as Integer ?: null
            def resizeType = attrs.resize ?: "s"
            def size = null

            if (forcedHeight && forcedWidth) {

                size = "w${forcedWidth}_h${forcedHeight}_${resizeType}"

            } else if (forcedWidth) {

                size = "w${forcedWidth}_${resizeType}"

            } else if (forcedHeight) {

                size = "h${forcedHeight}_${resizeType}"

            }

            //This preserves aspect ratio in the browser when scaling
            if (resizeType == "s") {

                scaleImage(bradleyImageInstance, forcedHeight, forcedWidth, attrs)

            }

            imageLink = g.createLink(controller: "bradleyImage", action: "display") + '/' + bradleyImageInstance?.id + '/' + bradleyImageInstance?.name?.replace(' ', '-') + '.' + bradleyImageInstance?.extension
            if (size) {

                imageLink += "?size=$size"

            }

        }

        return imageLink

    }

    /**
     *
     * @param bradleyImageInstance
     * @param forcedHeight
     * @param forcedWidth
     * @param attrs
     */
    private void scaleImage(BradleyImage bradleyImageInstance, Integer forcedHeight, Integer forcedWidth, Map attrs) {
        def heightShrinkRatio = null
        def widthShrinkRatio = null
        ScaledImage original = bradleyImageInstance.getOriginalScaledImage()

        if (forcedHeight && forcedWidth && original) {

            heightShrinkRatio = original.height / forcedHeight
            widthShrinkRatio = original.width / forcedWidth

        }

        if (forcedHeight) {

            if (forcedWidth && widthShrinkRatio > heightShrinkRatio && original) {

                Integer newHeight = Math.floor(original.height / widthShrinkRatio) as Integer
                attrs.put("height", newHeight)

            } else {
                attrs.put("height", forcedHeight)
            }
        }
        if (forcedWidth) {
            if (forcedHeight && heightShrinkRatio > widthShrinkRatio && original) {

                Integer newWidth = Math.floor(original.width / heightShrinkRatio) as Integer
                attrs.put("width", newWidth)

            } else {

                attrs.put("width", forcedWidth)

            }

        }

    }

}
