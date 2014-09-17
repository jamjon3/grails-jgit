import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import grails.util.Holders
import org.grails.plugins.jgit.JGit

class JgitGrailsPlugin {
    def version = "1.0.0"
    def grailsVersion = "2.2 > *"

    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    def title = "Jgit Plugin" 
    def author = "James Jones"
    def authorEmail = "james@gmail.com"
    def description = '''\
Creates a wrapper around the JGit library.
'''
    def observe = ['controllers', 'services', 'domains']
    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/jgit"

    def license = "GPL3"
    def organization = [ name: "University of South Florida", url: "http://www.usf.edu/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    def issueManagement = [ system: "github", url: "https://github.com/jamjon3/grails-jgit/issues" ]
    def scm = [ url: "https://github.com/jamjon3/grails-jgit/" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        def jgitConfig = application.config?.jgit
        if(jgitConfig.userInfoHandler) {
            credentialsProvider(org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider,jgitConfig.gitRemotelogin, jgitConfig.gitRemotePassword)
            jGit(org.grails.plugins.jgit.JGit) {
                rootFolder = application.parentContext.getResource("git").file
                remoteURL  = jgitConfig.gitRemoteURL
                branch = jgitConfig.branch
                userInfo = this.getClass().classLoader.loadClass(jgitConfig.userInfoHandler).newInstance()
                credentialsProvider = ref('credentialsProvider')
            }
        }
    }

    def doWithDynamicMethods = { ctx -> processArtifacts(application) }
        
    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    
    def onChange = { event -> processArtifacts(application) }

    def onConfigChange = { event -> processArtifacts(application) }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
    
    private void processArtifacts(application) {
        def config = application.config
        def jgit = application.mainContext.getBean('jGit')
        def types = config.jgit?.injectInto ?: ["Controller", "Service", "Domain"]
        if(jgit) {
            types.each { type ->
                application.getArtefacts(type).each { klass -> addDynamicMethods(klass, jgit) }
            }
        }
    }
    
    private void addDynamicMethods(klass, jgit) {
        klass.metaClass.withJGit = withJGit.curry(jgit)
    }
    
    private withJGit = { def jgit, Closure closure ->
        if (closure) {
            closure.delegate = jgit
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure(jgit.rootFolder)
        }
    }    
}
