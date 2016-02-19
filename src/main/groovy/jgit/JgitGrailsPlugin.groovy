/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jgit

import grails.plugins.Plugin
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.grails.plugins.jgit.JGit
import org.grails.plugins.jgit.FlexibleSshSessionFactory

class JgitGrailsPlugin extends Plugin {

    def grailsVersion = "3.0.0.BUILD-SNAPSHOT > *"

    def title = "JGit Plugin"
    def author = "James Jones"
    def authorEmail = "james@gmail.com"
    def description = 'Creates a wrapper around the JGit library.'
    def observe = ['controllers', 'services', 'domains']
    def documentation = "http://grails.org/plugin/jgit"
    def license = "GPL3"
    def organization = [ name: "University of South Florida", url: "http://www.usf.edu/" ]
    def issueManagement = [ system: "github", url: "https://github.com/jamjon3/grails-jgit/issues" ]
    def scm = [ url: "https://github.com/jamjon3/grails-jgit/" ]

    Closure doWithSpring() {

        { ->
            def jgitConfig = grailsApplication.config?.jgit
            if (!jgitConfig.userInfoHandler) return

            // Load the user info handler class name string into an actual class
            def userInfoHandlerClass = getClass().classLoader.loadClass(jgitConfig.userInfoHandler)
            jgitUserInfo(userInfoHandlerClass) {
                config = grailsApplication.config.jgit
            }

            if (jgitConfig.http) {
                credentialsProvider(UsernamePasswordCredentialsProvider, jgitConfig.http.gitRemotelogin, jgitConfig.http.gitRemotePassword)
            }
            if (jgitConfig.ssh) {
                sshSessionFactory(FlexibleSshSessionFactory) {
                    config = grailsApplication.config.jgit.ssh
                }
            }
            jGit(JGit) { bean ->
                rootFolder = new File("git") //grailsApplication.parentContext.getResource("git").file
                remoteURL  = jgitConfig.gitRemoteURL
                branch = jgitConfig.branch
                userInfo = ref('jgitUserInfo')
                if (jgitConfig.http) {
                    credentialsProvider = ref('credentialsProvider')
                }
                if (jgitConfig.ssh) {
                    sshSessionFactory = ref('sshSessionFactory')
                }
            }
        }
    }

    void doWithDynamicMethods() {
        processArtifacts(grailsApplication)
    }

    void onChange(Map<String, Object> event) {
        processArtifacts(grailsApplication)
    }

    void onConfigChange(Map<String, Object> event) {
        processArtifacts(grailsApplication)
    }

    private void processArtifacts(application) {
        def config = grailsApplication.config
        if (!grailsApplication.mainContext.beanDefinitionNames.find { it == 'jGit' }) return

        def jgit = grailsApplication.mainContext.getBean('jGit')
        def types = config.jgit?.injectInto ?: ["Controller", "Service", "Domain"]
        if (!jgit) return

        types.each { type ->
            grailsApplication.getArtefacts(type).each { klass -> addDynamicMethods(klass, jgit) }
        }
    }

    private void addDynamicMethods(klass, jgit) {
        klass.metaClass.withJGit = withJGit.curry(jgit)
    }

    private withJGit = { jgit, Closure closure ->
        if (!closure) return

        closure.delegate = jgit
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure(jgit.rootFolder)
    }
}
