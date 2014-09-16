//
// This script is executed by Grails during application upgrade ('grails upgrade'
// command). This script is a Gant script so you can use all special variables
// provided by Gant (such as 'baseDir' which points on project base dir). You can
// use 'ant' to access a global instance of AntBuilder
//
// For example you can create directory under project tree:
//
//    ant.mkdir(dir:"${basedir}/grails-app/jobs")
//

// ensure that there are 'git' directory under application 'web-app'
ant.mkdir(dir:"${basedir}/web-app/git")
def gitignoreFile = "${basedir}/.gitignore"
if(!(gitignoreFile as File).exists()?true:((gitignoreFile as File).readLines().findAll { it.contains("web-app/git") }.size() < 1)) {
    (gitignoreFile as File).withWriterAppend {
        it.writeLine "/web-app/git/"
    }
    ant.echo "Added /web-app/git/ to ${gitignoreFile}"
}