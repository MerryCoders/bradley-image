String version = 'Version #'
String grailsHomeRoot = '/path/to/your/grails/home'
String dotGrailsCommon = '/path/to/.grails/in/your/home/directory'
String projectDirCommon = '/path/to/target/directory/of/test/apps'

v22 {
    grailsVersion = '2.2.4'
    pluginVersion = version
    dotGrails = dotGrailsCommon
    projectDir = projectDirCommon
    grailsHome = grailsHomeRoot + '/' + grailsVersion
    plugins = """        test(":spock:0.7") {
                exclude "spock-grails-support"
            }"""
    dependencies = 'test "org.spockframework:spock-grails-support:0.7-groovy-2.0"'
}

v23 {
    grailsVersion = '2.3.1'
    pluginVersion = version
    dotGrails = dotGrailsCommon
    projectDir = projectDirCommon
    grailsHome = grailsHomeRoot + '/' + grailsVersion
}

