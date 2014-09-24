import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.grails.plugins.jgit.JGit

class JgitGrailsPlugin {
    def version = "1.0.0"
    def grailsVersion = "2.2 > *"
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

    def doWithSpring = {
        def jgitConfig = application.config?.jgit
        if (!jgitConfig.userInfoHandler) return
        
        // Load the class name string into an actual class
        def userInfoHandlerClass = getClass().classLoader.loadClass(jgitConfig.userInfoHandler)
        jgitUserInfo(userInfoHandlerClass) {
            config = application.config.jgit
        }

        credentialsProvider(UsernamePasswordCredentialsProvider, jgitConfig.gitRemotelogin, jgitConfig.gitRemotePassword)

        jGit(JGit) {
            rootFolder = application.parentContext.getResource("git").file
            remoteURL  = jgitConfig.gitRemoteURL
            branch = jgitConfig.branch
            userInfo = ref('jgitUserInfo')
            credentialsProvider = ref('credentialsProvider')
        }
    }

    def doWithDynamicMethods = { ctx -> processArtifacts(application) }

    def onChange = { event -> processArtifacts(application) }

    def onConfigChange = { event -> processArtifacts(application) }

    private void processArtifacts(application) {
        def config = application.config
        if (!application.mainContext.beanDefinitionNames.find { it == 'jGit' }) return

        def jgit = application.mainContext.getBean('jGit')
        def types = config.jgit?.injectInto ?: ["Controller", "Service", "Domain"]
        if (!jgit) return

        types.each { type ->
            application.getArtefacts(type).each { klass -> addDynamicMethods(klass, jgit) }
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
