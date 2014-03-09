package com.merrycoders.bradleyimage

import org.apache.commons.lang.StringUtils

class BradleyImageSizeService {

    /**
     * Returns a matching BradleyImageSize instance if it already exists in the database.
     * If it does not exist in the database, a new instance is created, saved and returned.
     * If the name parameter is of the wrong form, null will be returned
     *
     * @param name (required) Must be of the form "w100_h100_f"
     * @return Persisted BradleyImageSize instance
     */
    BradleyImageSize findOrSaveByString(String name) {

        if (!name) return null

        def bradleyImageSizeInstance = BradleyImageSize.findByName(name)
        def isValidNameMap = isValidName(name)

        if (!bradleyImageSizeInstance && isValidNameMap.isValid) {

            bradleyImageSizeInstance = new BradleyImageSize(isValidNameMap).save()

        }

        return bradleyImageSizeInstance
    }

    private Map isValidName(String name) {

        def isValidMap = [isValid: false]
        try {

            Integer width = StringUtils.substringBetween(name, "w", "_") as Integer ?: null
            Integer height = StringUtils.substringBetween(name, "h", "_") as Integer ?: null
            String renderType = StringUtils.substringAfterLast(name, "_")
            def isValid = (width || height) && renderType
            Boolean scale = renderType.contains("s")
            Boolean crop = renderType.contains("c")
            Boolean fill = renderType.contains("f")
            isValidMap = [isValid: isValid, name: name, width: width, height: height, scale: scale, crop: crop, fill: fill]

        } catch (ex) {
            log.error "Error validating BradleyImageSize name of $name", ex
        }

        return isValidMap

    }
}
