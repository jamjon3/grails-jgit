grails.project.work.dir = 'target'

grails.project.dependency.resolution = {

    inherits 'global'
    log 'warn'

    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
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
