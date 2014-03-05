package com.merrycoders.bradleyimage

import grails.plugin.lazylob.LazyBlob
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.apache.commons.io.FilenameUtils

import javax.sql.rowset.serial.SerialBlob
import java.sql.Blob

@TestFor(BradleyImageService)
@Mock([BradleyImage, BradleyImageSize, ScaledImage])
class BradleyImageServiceSpec extends SpecificationDataCore {

    def setup() {}

    def cleanup() {}

    def "Test correction of height and width on scaled images"() {
        given:
        BradleyImageSize bradleyImageSizeInstance = new BradleyImageSize(width: 16, height: 8, scale: true, name: "w16_h8_s").save()
        BradleyImage bradleyImageInstance = new BradleyImage(name: FilenameUtils.getName(fileName), extension: FilenameUtils.getExtension(fileName)).save()
        File fileInstance = new File(fileName)
        ScaledImage scaledImageInstance = new ScaledImage(height: 1, width: 1, bradleyImage: bradleyImageInstance, original: true, imageSize: bradleyImageSizeInstance)
        Blob blob = new SerialBlob(fileInstance.bytes)
        scaledImageInstance.data = new LazyBlob(blob, scaledImageInstance)
        scaledImageInstance.save()

        when:
        service.correctHeightAndWidthOnScaledImage(scaledImageInstance)

        then:
        scaledImageInstance.width == correctWidth
        scaledImageInstance.height == correctHeight

        where:
        fileName                   | correctWidth | correctHeight
        "test/data/32_16_flag.jpg" | 32           | 16
        "test/data/8_12_black.png" | 8            | 12

    }

    def "Test correction of height and width on invalid image"() {
        when:
        def results = service.correctHeightAndWidthOnScaledImage(null)

        then:
        results == null

    }

    def "test the scaling down of an image"() {
        given:
        BradleyImageSize originalBradleyImageSizeInstance = new BradleyImageSize(width: null, height: null, scale: true, name: "original").save()
        BradleyImageSize bradleyImageSizeInstance = new BradleyImageSize(width: 16, height: 8, scale: true, name: "w16_h8_s").save()
        BradleyImage bradleyImageInstance = new BradleyImage(name: FilenameUtils.getName(fileName), extension: FilenameUtils.getExtension(fileName)).save()

        File fileInstance = new File(fileName)
        ScaledImage scaledImageInstance = new ScaledImage(height: 1, width: 1, imageSize: originalBradleyImageSizeInstance, bradleyImage: bradleyImageInstance, original: true)
        Blob blob = new SerialBlob(fileInstance.bytes)
        scaledImageInstance.data = new LazyBlob(blob, scaledImageInstance)
        scaledImageInstance = service.correctHeightAndWidthOnScaledImage(scaledImageInstance)
        scaledImageInstance.save()

        when:
        def scaledCopy = service.makeScaledCopy(bradleyImageInstance, bradleyImageSizeInstance)

        then:
        scaledCopy.width == 16
        scaledCopy.height == 8

        where:
        fileName << ["test/data/32_16_flag.jpg"]
    }

    def "Test the scaling down of an image on invalid data"() {
        when:
        def results = service.makeScaledCopy(null, null)

        then:
        results == null

    }

    def "convertToPng"() {
        given:
        def bradleyImageSizeInstance = new BradleyImageSize(width: null, height: null, scale: true, name: "original").save()
        BradleyImage bradleyImageInstance = new BradleyImage(name: FilenameUtils.getName(fileName), extension: FilenameUtils.getExtension(fileName)).save()
        ScaledImage scaledImageInstance = new ScaledImage(height: 1, width: 1, bradleyImage: bradleyImageInstance, original: true, imageSize: bradleyImageSizeInstance)
        File fileInstance = new File(fileName)
        Blob blob = new SerialBlob(fileInstance.bytes)
        scaledImageInstance.data = new LazyBlob(blob, scaledImageInstance)
        scaledImageInstance.save()

        when:
        service.convertToPng(bradleyImageInstance)

        then:
        BradleyImage.findById(bradleyImageInstance.id).extension == "png"

        where:
        fileName << ["test/data/opaque_dots.gif", "test/data/transparent_dots.gif"]
    }

}
