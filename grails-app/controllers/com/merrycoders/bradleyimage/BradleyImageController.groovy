package com.merrycoders.bradleyimage

import grails.converters.JSON
import grails.plugin.lazylob.LazyBlob
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang.StringUtils
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

import javax.imageio.IIOException
import javax.servlet.http.HttpServletRequest

class BradleyImageController {

    def bradleyImageService

    public def batchUpload() {

        def bradleyImageInstanceList = BradleyImage.list([max: 10, sort: "lastUpdated"])

        [
                imageInstanceList: bradleyImageInstanceList,
                imageType: params?.imageType
        ]

    }

    public def batchImageEdit() {

        def bradleyImageIdList = params.id?.tokenize(",")*.toLong()
        def bradleyImageInstanceList = BradleyImage.findAllByIdInList(bradleyImageIdList) ?: []

        [bradleyImageInstanceList: bradleyImageInstanceList]

    }

    public def upload() {

        try {

            BradleyImage image = new BradleyImage(params)
            image.name = FilenameUtils.getBaseName(params.qqfile)
            image.extension = FilenameUtils.getExtension(params.qqfile)

            if (image.hasErrors() || !image.save()) {
                log.error image.errors
                return render(text: [success: false] as JSON, contentType: 'text/json')
            }

            byte[] data = selectInputStream(request)?.bytes
            ScaledImage scaledImage = new ScaledImage(
                    original: true,
                    bradleyImage: image,
                    imageSize: BradleyImageSize.findByName("original")
            )

            scaledImage.data = new LazyBlob(data, scaledImage)
            scaledImage.size = scaledImage.data?.length()
            bradleyImageService.correctHeightAndWidthOnScaledImage(scaledImage)

            if (scaledImage.hasErrors() || !scaledImage.save()) {
                log.error scaledImage.errors
                return render(text: [success: false] as JSON, contentType: 'text/json')
            }


            if (image.extension.toLowerCase() == 'gif') {
                bradleyImageService.convertToPng(image)
            }

            image = bradleyImageService.updateImageHash(image)

            if (image.hasErrors() || !image.save()) {
                log.error image.errors
                return render(text: [success: false] as JSON, contentType: 'text/json')
            }


            return render(text: [success: true, imageId: image.id] as JSON, contentType: 'text/json')

        } catch (IIOException e) {
            log.error "Failed to upload file.", e
            return render(text: [success: false] as JSON, contentType: 'text/json')
        }

    }

    private InputStream selectInputStream(HttpServletRequest request) {
        if (request instanceof MultipartHttpServletRequest) {
            MultipartFile uploadedFile = ((MultipartHttpServletRequest) request).getFile('qqfile')
            return uploadedFile.inputStream
        }
        return request.inputStream
    }

    public def display() {
        Integer id = 0

        if (params.id) {

            id = params.id as Integer

        } else if (params.path) {

            try {
                id = StringUtils.substringBefore(params.path, '/') as Integer
            }
            catch (NumberFormatException e) {
                forward(controller: "errorPage", action: "notFound")
                return
            }

        }

        def bradleyImageInstance = BradleyImage.get(id)

        if (bradleyImageInstance) {

            def scaledImage

            //check for other params to get specific ScaledImage
            if (params.size && params.size != "original") {

                def imageSize = BradleyImageSize.findByName(params.size)

                if (imageSize) {

                    scaledImage = ScaledImage.findByBradleyImageAndImageSize(bradleyImageInstance, imageSize)

                    if (!scaledImage) {

                        scaledImage = bradleyImageService.makeScaledCopy(bradleyImageInstance, imageSize)

                    }

                }

            } else {
                scaledImage = ScaledImage.findByBradleyImageAndOriginal(bradleyImageInstance, true)
            }

            if (scaledImage) {

                // Set mime type?
                LazyBlob lazyBlobInstance = scaledImage.data
                response.contentLength = scaledImage.size
                response.outputStream << lazyBlobInstance.getBytes(1L, lazyBlobInstance.length() as Integer)
                response.outputStream.flush()

            } else {
                forward(controller: "errorPage", action: "notFound")
            }

        } else {

            forward(controller: "errorPage", action: "notFound")

        }

    }

}
