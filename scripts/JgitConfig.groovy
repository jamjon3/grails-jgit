import java.security.MessageDigest

includeTargets << grailsScript("_GrailsInit")

target(jgitConfig: "Adds settings in Config.groovy for the JGit plugin") {
    def appDir = "$basedir/grails-app"
    def configFile = new File(appDir, 'conf/Config.groovy')
    def appName = ant.project.properties.'base.name'


    MessageDigest md5 = MessageDigest.getInstance("MD5")
    String var_dt = System.currentTimeMillis() / 1000
    md5.update(var_dt.getBytes())
    BigInteger hash = new BigInteger(1, md5.digest())
    String uniqKey = hash.toString(16)

    if (configFile.exists()) {
        configFile.withWriterAppend {
            it.writeLine '''
// Added by the JGit plugin:
jgit.userInfoHandler = 'org.grails.plugins.jgit.UserInfoHandler'
jgit.fallbackMap = [:]
jgit.fallbackEmailDefault = 'jdoe@foo.net'
jgit.fallbackUsername = 'jdoe'
jgit.branch = 'master'
jgit.gitRemoteURL = 'https://github.com/someuser/SomeApp.git'
jgit.gitRemotelogin = 'jdoe'
jgit.gitRemotePassword = 'mygitpassword'
jgit.injectInto = ['Controller', 'Service','Domain']
'''
        }
    }

    println """
    ********************************************************
    * Your grails-app/conf/Config.groovy has been updated. *
    *                                                      *
    * Please verify that the values are correct.           *
    ********************************************************
    """
}

setDefaultTarget(jgitConfig)