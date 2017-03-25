grails.project.work.dir = 'target'

grails.project.dependency.resolution = {

    inherits 'global'
    log 'warn'

    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
        // Fix failed dependencies http://stackoverflow.com/questions/28836824/grails-2-2-x-unresolved-dependencies-without-any-changes-to-previously-working-b
        mavenRepo "http://repo.grails.org/grails/repo/"
        // Fix failed dependencies http://stackoverflow.com/questions/28692748/error-failed-to-resolve-dependencies-grails
        mavenRepo "http://repo.grails.org/grails/core"
        mavenRepo "http://repo.grails.org/grails/plugins"
    }

    dependencies {
        compile "org.eclipse.jgit:org.eclipse.jgit:3.4.1.201406201815-r"
    }

    plugins {
        build ':release:2.2.4', ':rest-client-builder:1.0.3', {
            export = false
        }
    }
}
