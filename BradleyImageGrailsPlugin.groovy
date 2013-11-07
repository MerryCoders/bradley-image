class BradleyImageGrailsPlugin {

    // the plugin version
    def version = "0.1.0"

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.2 > *"

    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "src/docs/**",
            "testapps.config.groovy"
    ]

    def title = "Bradley Image Plugin" // Headline display name of the plugin
    def author = "Dean Del Ponte"
    def authorEmail = "dean.delponte@gmail.com"
    def description = "Provides on the fly, secure image manipulation including crop, fill, resize and more."

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/bradley-image"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
    def organization = [ name: "Bradley Corporation", url: "http://www.bradleycorp.com/" ]

    // Any additional developers beyond the author specified above.
    def developers = [ [ name: "Adrian Moore", email: "adrian.moore@bradleycorp.com" ]]

    // Location of the plugin's issue tracker.
    // TODO Fill this in when it's official
    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://github.com/MerryCoders/bradley-image" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
