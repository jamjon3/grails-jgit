import java.security.MessageDigest

includeTargets << grailsScript("Init")

target(main: "Creates artifacts for the JGit plugin") {
    def appDir = "$basedir/grails-app"
    def configFile = new File(appDir, 'conf/Config.groovy')
    def appName = Ant.project.properties.'base.name'


    MessageDigest md5 = MessageDigest.getInstance("MD5");
    String var_dt = new Date().getTime() / 1000
    md5.update(var_dt.getBytes());
    BigInteger hash = new BigInteger(1, md5.digest());
    String uniqKey = hash.toString(16);

    if (configFile.exists()) {
        configFile.withWriterAppend {
            it.writeLine '\n// Added by the JGit plugin:'
            it.writeLine "jgit.userInfoHandler = 'org.grails.plugins.jgit.UserInfoHandler'"
            it.writeLine "jgit.fallbackMap = [:]"
            it.writeLine "jgit.fallbackEmailDefault = 'jdoe@foo.net'"
            it.writeLine "jgit.fallbackUsername = 'jdoe'"
            it.writeLine "jgit.branch = 'master'"
            it.writeLine "jgit.gitRemoteURL = 'https://github.com/someuser/SomeApp.git'"
            it.writeLine "jgit.gitRemotelogin = 'jdoe'"
            it.writeLine "jgit.gitRemotePassword = 'mygitpassword'"
            it.writeLine "jgit.injectInto = ['Controller', 'Service','Domain']"
        }
    }

    ant.echo """
    ********************************************************
    * Your grails-app/conf/Config.groovy has been updated. *
    *                                                      *
    * Please verify that the values are correct.           *
    ********************************************************
    """
}

setDefaultTarget(main)