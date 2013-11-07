/* Inspired by https://github.com/grails-plugins/grails-spring-security-core/blob/master/scripts/CreateS2TestApps.groovy
 */
includeTargets << grailsScript("_GrailsInit")
includeTargets << new File("$bradleyImagePluginDir/scripts/_BradleyImageCommon.groovy")

functionalTestPluginVersion = '1.2.7'
projectfiles = new File(basedir, 'webtest/projectFiles')
grailsHome = null
dotGrails = null
grailsVersion = null
projectDir = null
appName = null
pluginVersion = null
pluginZip = null
testprojectRoot = null
deleteAll = false
dependencies = null
plugins = null

target(createPlatformUiTestApp: 'Creates test apps for manual testing') {

    def configFile = new File(basedir, 'testapps.config.groovy')
    if (!configFile.exists()) {
        error "$configFile.path not found"
    }

    new ConfigSlurper().parse(configFile.text).each { name, config ->
        printMessage "\nCreating app based on configuration $name: ${config.flatten()}\n"
        init name, config
        createApp()
        installPlugins()
        copyTests()
    }
}

private void init(String name, config) {

    pluginVersion = config.pluginVersion
    if (!pluginVersion) {
        error "pluginVersion wasn't specified for config '$name'"
    }

    pluginZip = new File(basedir, "platform-ui-${pluginVersion}.zip")
    if (!pluginZip.exists()) {
        error "plugin $pluginZip.absolutePath not found"
    }

    grailsHome = config.grailsHome
    if (!new File(grailsHome).exists()) {
        error "Grails home $grailsHome not found"
    }

    projectDir = config.projectDir
    appName = 'platform-ui-test-' + name
    testprojectRoot = "$projectDir/$appName"

    grailsVersion = config.grailsVersion
    dotGrails = config.dotGrails + '/' + grailsVersion

    dependencies = config.dependencies
    plugins = config.plugins

}

private void createApp() {

    ant.mkdir dir: projectDir

    deleteDir testprojectRoot
    deleteDir "$dotGrails/projects/$appName"

    callGrails(grailsHome, projectDir, 'dev', 'create-app') {
        ant.arg value: appName
    }
}

private void installPlugins() {

    File buildConfig = new File(testprojectRoot, 'grails-app/conf/BuildConfig.groovy')
    String contents = buildConfig.text

    contents = contents.replace('grails.project.class.dir = "target/classes"', "grails.project.work.dir = 'target'")
    contents = contents.replace('grails.project.test.class.dir = "target/test-classes"', '')
    contents = contents.replace('grails.project.test.reports.dir = "target/test-reports"', '')
    contents = contents.replace("//mavenLocal()", "mavenLocal()")
    contents = contents.replace('plugins {', 'plugins {\n        compile ":platform-ui:' + pluginVersion + '"\n        compile ":bootstrap-ui:1.0.RC4"\n' + plugins)
    contents = contents.replace('dependencies {', 'dependencies { ' + dependencies)
    buildConfig.withWriter { it.writeLine contents }

//	callGrails(grailsHome, testprojectRoot, 'dev', 'install-plugin') {
//		ant.arg value: pluginZip.absolutePath
//	}
}

private void copyTests() {

    def targetTestDir = "${projectDir}/${appName}/test"
    printMessage "Copying tests to $targetTestDir"

    ant.copy(todir: "$targetTestDir") {

        fileset(dir: "test", includes: "**")

    }

}

private void callGrails(String grailsHome, String dir, String env, String action, extraArgs = null) {

    ant.exec(executable: "$grailsHome/bin/grails", dir: dir, failonerror: 'true') {
        ant.env key: 'GRAILS_HOME', value: grailsHome
        ant.arg value: env
        ant.arg value: action
        extraArgs?.call()
    }

}

private void deleteDir(String path) {

    if (new File(path).exists() && !deleteAll) {
        String code = "confirm.delete.$path"
        ant.input message: "$path exists, ok to delete?", addproperty: code, validargs: 'y,n,a'
        def result = ant.antProject.properties[code]
        if ('a'.equalsIgnoreCase(result)) {
            deleteAll = true
        } else if (!'y'.equalsIgnoreCase(result)) {
            printMessage "\nNot deleting $path"
            exit 1
        }
    }

    ant.delete dir: path

}

private void error(String message) {

    errorMessage "\nERROR: $message"
    exit 1

}

setDefaultTarget 'createPlatformUiTestApp'
