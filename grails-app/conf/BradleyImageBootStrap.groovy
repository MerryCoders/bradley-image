import com.merrycoders.bradleyimage.BradleyImageSize

class BradleyImageBootStrap {

    def init = { servletContext ->

        initBradleyImageSizes()

    }

    def destroy = {}

    /**
     * By default, only image requests for predefined BradleyImageSizes will be rendered.  This provides an extra layer of security, but it
     * may be turned off in Config.groovy
     */
    void initBradleyImageSizes() {

        if (!BradleyImageSize.count()) {
            new BradleyImageSize(name: "original", width: null, height: null, crop: false, scale: false).save()
        }

    }
}
