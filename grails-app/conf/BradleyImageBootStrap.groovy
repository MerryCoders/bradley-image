import com.merrycoders.bradleyimage.bootstrap.BradleyImageSizeBootStrap
import com.merrycoders.bradleyimage.bootstrap.MimeTypeBootStrap

class BradleyImageBootStrap {

    def init = { servletContext ->


        BradleyImageSizeBootStrap.initDevData()
        MimeTypeBootStrap.initDevData()

    }

    def destroy = {}
}
