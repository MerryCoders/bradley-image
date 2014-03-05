package com.merrycoders.bradleyimage

import grails.plugin.lazylob.LazyBlob
import grails.transaction.Transactional
import org.imgscalr.Scalr

import javax.imageio.ImageIO
import javax.imageio.ImageReader
import javax.imageio.stream.ImageInputStream
import javax.imageio.stream.MemoryCacheImageInputStream
import java.awt.image.BufferedImage

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

    BradleyImage convertToPng(BradleyImage bradleyImage) {
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
        byte[] bytes = data?.getBytes(0L, data?.length() as Integer)
        bradleyImage.pHash = imagePHash.getHash(new ByteArrayInputStream(bytes))

        return bradleyImage

    }

    ScaledImage makeScaledCopy(BradleyImage image, BradleyImageSize imageSize) {

        if (["gif", "svg"].contains(image.extension)) {
            log.error "Image ${image?.id} can't be resized, use a png"
            return image.originalScaledImage
        }

        ScaledImage original = ScaledImage.findByBradleyImageAndOriginal(image, true)
        InputStream originalInputStream = new ByteArrayInputStream(original.data)
        BufferedImage imageBuffer = ImageIO.read(originalInputStream)

        if (imageSize.fill) {
            imageBuffer = fillAndScaleImage(original, imageSize, imageBuffer)
        } else if (imageSize.scale && imageSize.crop) {
            imageBuffer = scaleAndCropImage(original, imageSize, imageBuffer)
        } else if (imageSize.scale) {
            imageBuffer = scaleImage(original, imageSize, imageBuffer)
        } else if (imageSize.crop) {
            imageBuffer = cropImage(original, imageSize, imageBuffer)
        }

        ByteArrayOutputStream baos
        try {
            baos = new ByteArrayOutputStream()
            ImageIO.write(imageBuffer, image.extension, baos)
            byte[] data = baos.toByteArray()

            ScaledImage newImage = new ScaledImage(
                    bradleyImage: image,
                    size: data.size(),
                    imageSize: imageSize,
                    original: false,
                    data: data)

            correctHeightAndWidthOnScaledImage(newImage)

            if (!newImage.hasErrors() && newImage.save()) {
                return newImage
            }

        } catch (ex) {
            log.error "Error resizing image", ex
        } finally {
            baos.close()
        }

        log.error "Bradley image $image has errors"

        return null
    }

    private BufferedImage fillAndScaleImage(ScaledImage original, BradleyImageSize imageSize, BufferedImage imageBuffer) {

        Integer requestedHeight = imageSize.height ?: original.height
        Integer requestedWidth = imageSize.width ?: original.width

        //For the purpose of this, I represent aspect ratio as width/height
        float requestedAspectRatio = (float) requestedWidth / (float) requestedHeight
        float originalAspectRatio = (float) original.width / (float) original.height

        //This is how much smaller the image needs to be to fit the aspect ratio
        float heightShrinkRatio = (float) original.height / (float) requestedHeight
        float widthShrinkRatio = (float) original.width / (float) requestedWidth

        //new size
        Integer scaleHeight = 1
        Integer scaleWidth = 1
        Integer postFillWidth = 1
        Integer postFillHeight = 1

        if (heightShrinkRatio > widthShrinkRatio) {
            //maintain height, scale width, then fill to meet aspect ratio
            scaleHeight = Math.min(original.height, requestedHeight)
            scaleWidth = (float) scaleHeight * originalAspectRatio

            postFillHeight = (float) scaleHeight
            postFillWidth = (float) scaleHeight * requestedAspectRatio
        } else {
            //maintain width, scale height, then fill to meet aspect ration
            scaleWidth = Math.min(original.width, requestedWidth)
            scaleHeight = (float) scaleWidth / originalAspectRatio

            postFillWidth = scaleWidth
            postFillHeight = (float) scaleWidth / requestedAspectRatio
        }

        imageBuffer = Scalr.resize(imageBuffer, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.AUTOMATIC, scaleWidth, scaleHeight)
        imageBuffer = fillImage(imageBuffer, postFillWidth, postFillHeight)
        log.info("scale: w" + scaleWidth + " x h" + scaleHeight)
        log.info("fill: w" + postFillWidth + " x h" + postFillHeight)
        log.info("original Aspect Ratio: " + originalAspectRatio)
        log.info("requested Aspect Ratio: " + requestedAspectRatio)
        log.info("final Aspect Ratio : " + ((float) imageBuffer.width / (float) imageBuffer.height))

        return imageBuffer
    }

    private BufferedImage fillImage(BufferedImage imageBuffer, int width, int height) {
        BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        java.awt.Graphics g = combined.getGraphics()
        g.setColor(new java.awt.Color(255, 255, 255))
        g.fillRect(
                0,
                0,
                width,
                height)

        g.drawImage(
                imageBuffer,
                Math.floor(width / 2.0 - imageBuffer.width / 2.0) as int,
                Math.floor(height / 2.0 - imageBuffer.height / 2.0) as int,
                null)

        return combined
    }

    private BufferedImage scaleAndCropImage(ScaledImage original, BradleyImageSize imageSize, BufferedImage imageBuffer) {
        def (Integer requestedHeight, Integer requestedWidth) = getRequestedHeightAndWidth(imageSize, original)

        imageBuffer = resizeImageForCrop([originalHeight: original.height, originalWidth: original.width, requestedHeight: requestedHeight, requestedWidth: requestedWidth], imageBuffer)

        Integer offsetWidth = Math.max(0, ((imageBuffer.width / 2) as Integer) - ((requestedWidth / 2) as Integer))
        Integer offsetHeight = Math.max(0, ((imageBuffer.height / 2) as Integer) - ((requestedHeight / 2) as Integer))

        log.info("original w: " + original.width + " H: " + original.height)
        log.info("offset  w: " + offsetWidth + " H: " + offsetHeight)
        log.info("resized image w: ${imageBuffer.width}")
        log.info("resized image h: ${imageBuffer.height}")

        if ((offsetWidth + requestedWidth / 2 > imageBuffer.width) || (offsetHeight + requestedHeight / 2 > imageBuffer.height)) {
            throw new IllegalArgumentException("Invalid parameters for crop")
        }

        imageBuffer = imageBuffer.getSubimage(offsetWidth, offsetHeight, Math.min(requestedWidth, imageBuffer.width), Math.min(requestedHeight, imageBuffer.height))
        return imageBuffer
    }

    /**
     * Resizing an image for cropping is a little trickier because the requested crop size may be larger in one or two directions than the default resized image.
     * This method insures that the resized image can be cropped to the requested size without error
     * @param dimensions Map containing original and requested image height/widths
     * @param imageBuffer BufferedImage instance to resize
     * @return scaled BufferedImage instance
     */
    private BufferedImage resizeImageForCrop(Map dimensions, BufferedImage imageBuffer) {
        def originalHeight = dimensions.originalHeight
        def originalWidth = dimensions.originalWidth
        def requestedHeight = dimensions.requestedHeight
        def requestedWidth = dimensions.requestedWidth
        int width, height

        float heightShrinkRatio = originalHeight / requestedHeight
        float widthShrinkRatio = originalWidth / requestedWidth
        BufferedImage scaledImage = Scalr.resize(imageBuffer, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.AUTOMATIC, requestedWidth, requestedHeight)
        log.info("image   w: " + scaledImage.width + " H: " + scaledImage.height)

        if (scaledImage.height < requestedHeight) {
            height = requestedHeight
            width = Math.floor(originalWidth / heightShrinkRatio) as Integer
            scaledImage = Scalr.resize(imageBuffer, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.AUTOMATIC, width, height)
        } else if (scaledImage.width < requestedWidth) {
            width = requestedWidth
            height = Math.floor(originalHeight / widthShrinkRatio) as Integer
            scaledImage = Scalr.resize(imageBuffer, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.AUTOMATIC, width, height)
        }

        log.info("widthShrinkRatio: $widthShrinkRatio")
        log.info("heightShrinkRatio: $heightShrinkRatio")
        log.info("requested W: " + requestedWidth + " H: " + requestedHeight)

        return scaledImage
    }

    private BufferedImage scaleImage(ScaledImage original, BradleyImageSize imageSize, BufferedImage imageBuffer) {

        def (Integer requestedHeight, Integer requestedWidth) = getRequestedHeightAndWidth(imageSize, original)

        //scale so max is max size
        Integer height = null
        Integer width = null

        float heightShrinkRatio = original.height / requestedHeight
        float widthShrinkRatio = original.width / requestedWidth

        //never grow an image.
        if (widthShrinkRatio > 1 && widthShrinkRatio >= heightShrinkRatio) {
            width = imageSize.width
            height = Math.floor(original.height / widthShrinkRatio) as Integer

        } else if (heightShrinkRatio > 1f) {
            width = (Math.floor(original.width / heightShrinkRatio) as Integer)
            height = imageSize.height

        }
        if (height && width) {
            imageBuffer = Scalr.resize(imageBuffer, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.AUTOMATIC, width, height)
        }
        return imageBuffer
    }

    private ArrayList<Integer> getRequestedHeightAndWidth(BradleyImageSize imageSize, ScaledImage original) {
        Integer requestedHeight = Math.min(imageSize.height ?: original.height, original.height)
        Integer requestedWidth = Math.min(imageSize.width ?: original.width, original.width)
        return [requestedHeight, requestedWidth]
    }

    private BufferedImage cropImage(ScaledImage original, BradleyImageSize imageSize, BufferedImage imageBuffer) {

        def (Integer requestedHeight, Integer requestedWidth) = getRequestedHeightAndWidth(imageSize, original)

        Integer offsetWidth = Math.max(0, (Math.floor(original.width / 2) as Integer) - ((requestedWidth / 2) as Integer))
        Integer offsetHeight = Math.max(0, ((original.height / 2) as Integer) - ((requestedHeight / 2) as Integer))

        if ((offsetWidth + requestedWidth / 2 > original.width) || (offsetHeight + requestedHeight / 2 > original.height)) {
            throw new IllegalArgumentException("Invalid parameters for crop")
        }
        log.info("offsetWidth ${offsetWidth} offsetHeight ${offsetHeight} requestedWidth ${requestedWidth} requestedHeight ${requestedHeight}")
        imageBuffer = imageBuffer.getSubimage(offsetWidth, offsetHeight, requestedWidth, requestedHeight)
        return imageBuffer
    }

}
