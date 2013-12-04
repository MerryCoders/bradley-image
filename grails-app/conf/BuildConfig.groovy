grails.project.work.dir = 'target'

grails.project.dependency.resolution = {

    inherits("global") {}
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    legacyResolve true

    repositories {
        grailsCentral()
        //mavenLocal()
    }

    dependencies {
        // runtime 'mysql:mysql-connector-java:5.1.21'
    }

    plugins {

        build ':release:2.2.1', ':rest-client-builder:2.0.0', {
            export = false
        }

        compile ":ajax-uploader:1.1"
        compile(":hibernate:$grailsVersion") {
            export = false
        }
        compile ':platform-core:1.0.RC6'
        compile ":lazylob:0.1"

        test(":spock:0.7") {
            exclude "spock-grails-support"
        }
    }
}
