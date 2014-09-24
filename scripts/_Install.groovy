// ensure that there are 'git' directory under application 'web-app'
ant.mkdir(dir:"${basedir}/web-app/git")
def gitignoreFile = new File(basedir, '.gitignore')
if (!gitignoreFile.exists() ? true : gitignoreFile.readLines().findAll { it.contains("web-app/git") }.isEmpty() ) {
    gitignoreFile.withWriterAppend {
        it.writeLine "/web-app/git/"
    }
    println "Added /web-app/git/ to ${gitignoreFile}"
}
println '''
**************************************************************
* You've installed the JGit plugin.                          *
*                                                            *
* Next run the "jgit-config" script to add the default       *
* values to your configuration.                              *
*                                                            *
**************************************************************
'''