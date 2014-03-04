package com.merrycoders.bradleyimage

import grails.converters.JSON
import org.apache.commons.io.FilenameUtils
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

import javax.imageio.IIOException
import javax.imageio.ImageIO
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

    public def upload() {

        try {
            BradleyImage image = new BradleyImage(params)
            image.name = FilenameUtils.getBaseName(params.qqfile)
            image.extension = FilenameUtils.getExtension(params.qqfile)

            byte[] data = selectInputStream(request).getBytes()
            InputStream originalInputStream = new ByteArrayInputStream(data)
            ImageIO.read(originalInputStream)

            if (image.hasErrors() || !image.save()) {
                log.error image.errors
                return render(text: [success: false] as JSON, contentType: 'text/json')
            }

            ScaledImage scaledImage = new ScaledImage()
//                    data: data,
//                    original: true,
//                    imageSize: BradleyImageSize.findByName("original"),
//                    bradleyImage: image
//            )

            scaledImage.data = data
            scaledImage.size = scaledImage.data.length
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
            log.error("Failed to upload file.", e)
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

}
