package com.merrycoders.bradleyimage

import grails.plugin.lazylob.LazyBlob
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.apache.commons.io.FilenameUtils

import javax.sql.rowset.serial.SerialBlob
import java.sql.Blob

@TestFor(BradleyImageTagLib)
@Mock([BradleyImage, BradleyImageSize, ScaledImage])
class BradleyImageTagLibSpec extends SpecificationDataCore {

    def setup() {}

    def cleanup() {}


    def "imageLink"() {
        given:
        def bradleyImageSizeInstance = new BradleyImageSize(width: null, height: null, scale: true, name: "original").save()
        BradleyImage bradleyImageInstance = new BradleyImage(name: FilenameUtils.getBaseName(fileName), extension: FilenameUtils.getExtension(fileName)).save()
        ScaledImage scaledImageInstance = new ScaledImage(height: 1, width: 1, bradleyImage: bradleyImageInstance, original: true, imageSize: bradleyImageSizeInstance)
        File fileInstance = new File(fileName)
        Blob blob = new SerialBlob(fileInstance.bytes)
        scaledImageInstance.data = new LazyBlob(blob, scaledImageInstance)
        scaledImageInstance.save()

        when:
        def results = tagLib.imageLink([bradleyImage: bradleyImageInstance])

        then:
        results == imageTag

        where:
        fileName                   | imageTag
        "test/data/32_16_flag.jpg" | '<img src="/bradleyImage/display/1/32_16_flag.jpg" />'
    }
}
