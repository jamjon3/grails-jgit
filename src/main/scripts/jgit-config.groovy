import java.security.MessageDigest

description("Adds settings in Config.groovy for the JGit plugin", "grails jgit-config")

def appDir = "$baseDir/grails-app"
def configFile = new File(appDir, 'conf/application.groovy')
def configYml = false
if (!configFile.exists()) {
    configFile = new File(appDir, 'conf/application.yml')
    configYml = true
}
//def appName = ant.project.properties.'base.name'


MessageDigest md5 = MessageDigest.getInstance("MD5")
String var_dt = System.currentTimeMillis() / 1000
md5.update(var_dt.getBytes())
BigInteger hash = new BigInteger(1, md5.digest())
String uniqKey = hash.toString(16)

if (configFile.exists()) {
    if (configYml) {
        configFile.withWriterAppend {
        it.writeLine '''
# Added by the JGit plugin:
jgit:
    userInfoHandler: org.grails.plugins.jgit.UserInfoHandler
    fallbackMap: {}
    fallbackEmailDefault: jdoe@foo.net
    fallbackUsername: jdoe
    branch: master
    gitRemoteURL: https://github.com/someuser/SomeApp.git
    injectInto:
        - Controller
        - Service
        - Domain
    http:
        gitRemotelogin: jdoe
        gitRemotePassword: mygitpassword
'''
        }
        println """
    *************************************************************
    * Your grails-app/conf/application.yml has been updated.    *
    *                                                           *
    * Please verify that the values are correct.                *
    *************************************************************
    """
    }
    else {
        configFile.withWriterAppend {
            it.writeLine '''
// Added by the JGit plugin:
jgit.userInfoHandler = 'org.grails.plugins.jgit.UserInfoHandler'
jgit.fallbackMap = [:]
jgit.fallbackEmailDefault = 'jdoe@foo.net'
jgit.fallbackUsername = 'jdoe'
jgit.branch = 'master'
jgit.gitRemoteURL = 'https://github.com/someuser/SomeApp.git'
jgit.http.gitRemotelogin = 'jdoe'
jgit.http.gitRemotePassword = 'mygitpassword'
jgit.injectInto = ['Controller', 'Service','Domain']
'''
        }
        println """
    *************************************************************
    * Your grails-app/conf/application.groovy has been updated. *
    *                                                           *
    * Please verify that the values are correct.                *
    *************************************************************
    """
    }
}

