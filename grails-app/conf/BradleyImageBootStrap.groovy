import com.merrycoders.bradleyimage.bootstrap.CoreBootStrap

class BradleyImageBootStrap {

    def init = { servletContext ->

        CoreBootStrap.init()

    }

    def destroy = {}
}
