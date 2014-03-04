package com.merrycoders.bradleyimage

import grails.plugin.lazylob.LazyBlob
import grails.transaction.Transactional

import javax.imageio.ImageIO
import javax.imageio.ImageReader
import javax.imageio.stream.ImageInputStream
import javax.imageio.stream.MemoryCacheImageInputStream

@Transactional
class BradleyImageService {

    /**
     * Calculates the height and width of the ScaledImage instance and sets the fields
     * @param scaledImage
     * @return The modified ScaledImage instance
     */
    def correctHeightAndWidthOnScaledImage(ScaledImage scaledImage) {
        Iterator<ImageReader> readers = ImageIO.getImageReadersBySuffix(scaledImage.bradleyImage?.extension)
        if (readers.hasNext() && scaledImage?.data) {
            ImageReader reader = readers.next()
            ImageInputStream is = new MemoryCacheImageInputStream(new ByteArrayInputStream(scaledImage.data.getBytes(0L, scaledImage.data?.length() as Integer)))
            reader.setInput(is)

            scaledImage.width = reader.getWidth(reader.getMinIndex())
            scaledImage.height = reader.getHeight(reader.getMinIndex())

        } else {
            scaledImage.width = 1
            scaledImage.height = 1
        }

        return scaledImage
    }

    def convertToPng(BradleyImage bradleyImage) {
        log.info "Converting ${bradleyImage?.name} to PNG"
        ScaledImage originalScaledImage = bradleyImage.getOriginalScaledImage()

        //load original bytes in, convert to another type
        ImageInputStream inputStream = new MemoryCacheImageInputStream(new ByteArrayInputStream(originalScaledImage.data))
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ImageIO.write(ImageIO.read(inputStream), "png", baos)
        originalScaledImage.data = baos.toByteArray()

        //change metadata on image
        bradleyImage.extension = "png"
        originalScaledImage.size = originalScaledImage.data.size()
        correctHeightAndWidthOnScaledImage(originalScaledImage)

        //bradleyImage.mimeType =

        //save back to database
        if (bradleyImage.hasErrors() || !bradleyImage.save()) {
            log.error bradleyImage.errors
        }
        if (originalScaledImage.hasErrors() || !originalScaledImage.save()) {
            log.error originalScaledImage.errors
        }

        //destroy all old scaled copies of the image that are not the new original
        ScaledImage.findAllByBradleyImageAndOriginal(bradleyImage, false).each { it.delete() }
        return bradleyImage
    }

    BradleyImage updateImageHash(BradleyImage bradleyImage) {

        BradleyImagePerceptualHash imagePHash = new BradleyImagePerceptualHash(32, 8)
        LazyBlob data = bradleyImage.originalScaledImage?.data
        byte [] bytes = data?.getBytes(0L, data?.length() as Integer)
        bradleyImage.pHash = imagePHash.getHash(new ByteArrayInputStream(bytes))

        return bradleyImage

    }

}
