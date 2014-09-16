import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.InitCommand
import org.eclipse.jgit.api.CloneCommand
import org.eclipse.jgit.api.FetchCommand
import org.eclipse.jgit.api.LsRemoteCommand
import org.eclipse.jgit.api.PullCommand
import org.eclipse.jgit.api.PushCommand
import org.eclipse.jgit.api.SubmoduleSyncCommand
import org.eclipse.jgit.api.RmCommand
import org.eclipse.jgit.api.CheckoutCommand
import org.eclipse.jgit.api.StatusCommand
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.eclipse.jgit.api.errors.GitAPIException
import grails.util.Holders

class JgitGrailsPlugin {
    def version = "0.1"
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
        // TODO Implement runtime spring config (optional)
        
    }

    def doWithDynamicMethods = { ctx -> processArtifacts(application) }
    
    // Defined within doWithApplicationContext 
    private Git git
    private def credentialsProvider
    
    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
        def localGitFolder = applicationContext.getResource("web-app/git").file
        if(localGitFolder.exists()) {
            localRepoFolder.deleteDir()
        }
        FileRepositoryBuilder builder = new FileRepositoryBuilder()
        Repository repository = builder.setGitDir(localGitFolder)
            .readEnvironment().findGitDir().setup().build()
        def jgitConfig = Holders.config.jgit
        // Load the default or custom specified user info handler
        def userInfoHandler = this.getClass().classLoader.loadClass(jgitConfig.userInfoHandler).newInstance()
        // Setup the Clone
        CloneCommand clone = Git.cloneRepository()
        // Setup the branch on the clone
        clone.setBare(false).setBranch(jgitConfig.branch)
        // Specify the remote uri. Ex: git@192.168.2.43:test.git OR https://github.com/someuser/SomeProject.git
        clone.setDirectory(localRepoFolder).setURI(jgitConfig.gitRemoteURL)
        // Specifiy username/password (only supported method for now)
        credentialsProvider = new UsernamePasswordCredentialsProvider(jgitConfig.gitRemotelogin, jgitConfig.gitRemotePassword)
        clone.setCredentialsProvider(credentialsProvider)
        
        try {
            git = clone.call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        
    }

    
    def onChange = { event -> processArtifacts(application) }

    def onConfigChange = { event -> processArtifacts(application) }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
    
    private void processArtifacts(application) {
        def config = application.config
        def types = config.jgit?.injectInto ?: ["Controller", "Service", "Domain"]
        types.each { type ->
            application.getArtefacts(type).each { klass -> addDynamicMethods(klass, application) }
        }
    }
    
    private void addDynamicMethods(klass, application) {
        klass.metaClass.withJGit = withJGit.curry(git,credentialsProvider)
    }
    
    private withJGit = { Git git, def credentialsProvider, Closure closure ->
        PushCommand pushcm = git.push()
        pushcm.setCredentialsProvider(credentialsProvider)
        pushcm.setRemote(Holders.config.jgit.gitRemoteURL)
        PullCommand pullcm = git.pull()
        pullcm.setCredentialsProvider(credentialsProvider)
        // pull.setRemote(Holders.config.jgit.gitRemoteURL)
        RmCommand rmcm = git.rm()        
        SubmoduleSyncCommand synccm = git.submoduleSync()
        
        if (closure) {
            closure.delegate = git
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure()
        }
    }
}
