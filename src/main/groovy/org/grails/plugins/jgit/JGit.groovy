package org.grails.plugins.jgit

import groovy.util.logging.Slf4j
import org.eclipse.jgit.api.*
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.transport.SshSessionFactory

import javax.annotation.PostConstruct

/**
 * @author james
 */
@Slf4j
class JGit {
    def rootFolder
    def userInfo
    def credentialsProvider
    def remoteURL
    def branch
    def sshSessionFactory
    private Git git
    private PullCommand pull
    private PushCommand push
    private Repository repository

    @PostConstruct
    void afterPropertiesSet() {
        // do you stuff here.
        if (rootFolder.exists()) {
            rootFolder.deleteDir()
        }
        if (sshSessionFactory) {
            SshSessionFactory.setInstance(sshSessionFactory)
        }
        FileRepositoryBuilder builder = new FileRepositoryBuilder()
        repository = builder.setGitDir(rootFolder).readEnvironment().findGitDir().setup().build()
        // Setup the Clone
        CloneCommand clone = Git.cloneRepository()
        // Setup the branch on the clone
        clone.setBare(false).setBranch(branch)
        // Specify the remote uri. Ex: git@192.168.2.43:test.git OR https://github.com/someuser/SomeProject.git
        clone.setDirectory(rootFolder).setURI(remoteURL)
        // Add the provided credentialsProvider
        if (credentialsProvider) {
            clone.setCredentialsProvider(credentialsProvider)
        }
        try {
            git = clone.call()
            pull = git.pull()
            if (credentialsProvider) {
                pull.setCredentialsProvider(credentialsProvider)
            }
            push = git.push()
            if (credentialsProvider) {
                push.setCredentialsProvider(credentialsProvider)
            }
            push.setRemote(remoteURL)
        }
        catch(GitAPIException e) {
            log.error(e.message, e)
        }
    }
    /**
     * Returns a command object to execute a {@code Pull} command
     * with credentialProvider and remote already set
     *
     * @return a {@link PullCommand}
     */
    PullCommand pull() {
        return pull
    }
    /**
     * Returns a command object to execute a {@code Push} command
     * with credentialProvider and remote already set
     *
     * @see <a
     *      href="http://www.kernel.org/pub/software/scm/git/docs/git-push.html"
     *      >Git documentation about Push</a>
     * @return a {@link PushCommand} used to collect all optional parameters and
     *         to finally execute the {@code Push} command
     */
    PushCommand push() {
        return push
    }
    /**
     * Returns a command object to execute a {@code Commit} command
     * with the author preset using the userInfo class methods
     * 'resolveUsername' and 'resolveEmail'
     *
     * @see <a
     *      href="http://www.kernel.org/pub/software/scm/git/docs/git-commit.html"
     *      >Git documentation about Commit</a>
     * @return a {@link CommitCommand} used to collect all optional parameters
     *         and to finally execute the {@code Commit} command
     */
    CommitCommand commit() {
        return git.commit().setAuthor(userInfo.resolveUsername(),userInfo.resolveEmail())
    }
    /**
     * Returns a command object to execute a {@code Log} command
     *
     * @see <a
     *      href="http://www.kernel.org/pub/software/scm/git/docs/git-log.html"
     *      >Git documentation about Log</a>
     * @return a {@link LogCommand} used to collect all optional parameters and
     *         to finally execute the {@code Log} command
     */
    LogCommand log() {
        return git.log()
    }
    /**
     * Returns a command object to execute a {@code Merge} command
     *
     * @see <a
     *      href="http://www.kernel.org/pub/software/scm/git/docs/git-merge.html"
     *      >Git documentation about Merge</a>
     * @return a {@link MergeCommand} used to collect all optional parameters
     *         and to finally execute the {@code Merge} command
     */
    MergeCommand merge() {
        return git.merge()
    }
    /**
     * Returns a command object used to create branches
     *
     * @return a {@link CreateBranchCommand}
     */
    CreateBranchCommand branchCreate() {
        return git.branchCreate()
    }
    /**
     * Returns a command object used to delete branches
     *
     * @return a {@link DeleteBranchCommand}
     */
    DeleteBranchCommand branchDelete() {
        return git.branchDelete()
    }
    /**
     * Returns a command object used to list branches
     *
     * @return a {@link ListBranchCommand}
     */
    ListBranchCommand branchList() {
        return git.branchList()
    }
    /**
     *
     * Returns a command object used to list tags
     *
     * @return a {@link ListTagCommand}
     */
    ListTagCommand tagList() {
        return git.tagList()
    }
    /**
     * Returns a command object used to rename branches
     *
     * @return a {@link RenameBranchCommand}
     */
    RenameBranchCommand branchRename() {
        return git.branchRename()
    }
    /**
     * Returns a command object to execute a {@code Add} command
     *
     * @see <a
     *      href="http://www.kernel.org/pub/software/scm/git/docs/git-add.html"
     *      >Git documentation about Add</a>
     * @return a {@link AddCommand} used to collect all optional parameters and
     *         to finally execute the {@code Add} command
     */
    AddCommand add() {
        return git.add()
    }
    /**
     * Returns a command object to execute a {@code Tag} command
     *
     * @see <a
     *      href="http://www.kernel.org/pub/software/scm/git/docs/git-tag.html"
     *      >Git documentation about Tag</a>
     * @return a {@link TagCommand} used to collect all optional parameters and
     *         to finally execute the {@code Tag} command
     */
    TagCommand tag() {
        return git.tag()
    }
    /**
     * Returns a command object to execute a {@code Fetch} command
     *
     * @see <a
     *      href="http://www.kernel.org/pub/software/scm/git/docs/git-fetch.html"
     *      >Git documentation about Fetch</a>
     * @return a {@link FetchCommand} used to collect all optional parameters
     *         and to finally execute the {@code Fetch} command
     */
    FetchCommand fetch() {
        return git.fetch()
    }
    /**
     * Returns a command object to execute a {@code cherry-pick} command
     *
     * @see <a
     *      href="http://www.kernel.org/pub/software/scm/git/docs/git-cherry-pick.html"
     *      >Git documentation about cherry-pick</a>
     * @return a {@link CherryPickCommand} used to collect all optional
     *         parameters and to finally execute the {@code cherry-pick} command
     */
    CherryPickCommand cherryPick() {
        return git.cherryPick()
    }
    /**
     * Returns a command object to execute a {@code revert} command
     *
     * @see <a
     *      href="http://www.kernel.org/pub/software/scm/git/docs/git-revert.html"
     *      >Git documentation about reverting changes</a>
     * @return a {@link RevertCommand} used to collect all optional parameters
     *         and to finally execute the {@code cherry-pick} command
     */
    RevertCommand revert() {
        return git.revert()
    }
    /**
     * Returns a command object to execute a {@code Rebase} command
     *
     * @see <a
     *      href="http://www.kernel.org/pub/software/scm/git/docs/git-rebase.html"
     *      >Git documentation about rebase</a>
     * @return a {@link RebaseCommand} used to collect all optional parameters
     *         and to finally execute the {@code rebase} command
     */
    RebaseCommand rebase() {
        return git.rebase()
    }
    /**
     * Returns a command object to execute a {@code rm} command
     *
     * @see <a
     *      href="http://www.kernel.org/pub/software/scm/git/docs/git-rm.html"
     *      >Git documentation about rm</a>
     * @return a {@link RmCommand} used to collect all optional parameters and
     *         to finally execute the {@code rm} command
     */
    RmCommand rm() {
        return git.rm()
    }
    /**
     * Returns a command object to execute a {@code checkout} command
     *
     * @see <a
     *      href="http://www.kernel.org/pub/software/scm/git/docs/git-checkout.html"
     *      >Git documentation about checkout</a>
     * @return a {@link CheckoutCommand} used to collect all optional parameters
     *         and to finally execute the {@code checkout} command
     */
    CheckoutCommand checkout() {
        return git.checkout()
    }
    /**
     * Returns a command object to execute a {@code reset} command
     *
     * @see <a
     *      href="http://www.kernel.org/pub/software/scm/git/docs/git-reset.html"
     *      >Git documentation about reset</a>
     * @return a {@link ResetCommand} used to collect all optional parameters
     *         and to finally execute the {@code reset} command
     */
    ResetCommand reset() {
        return git.reset()
    }
    /**
     * Returns a command object to execute a {@code status} command
     *
     * @see <a
     *      href="http://www.kernel.org/pub/software/scm/git/docs/git-status.html"
     *      >Git documentation about status</a>
     * @return a {@link StatusCommand} used to collect all optional parameters
     *         and to finally execute the {@code status} command
     */
    StatusCommand status() {
        return git.status()
    }
    /**
     * Returns a command to create an archive from a tree
     *
     * @return a {@link ArchiveCommand}
     * @since 3.1
     */
    ArchiveCommand archive() {
        return git.archive()
    }
    /**
     * Returns a command to add notes to an object
     *
     * @return a {@link AddNoteCommand}
     */
    AddNoteCommand notesAdd() {
        return git.notesAdd()
    }
    /**
     * Returns a command to remove notes on an object
     *
     * @return a {@link RemoveNoteCommand}
     */
    RemoveNoteCommand notesRemove() {
        return git.notesRemove()
    }
    /**
     * Returns a command to list all notes
     *
     * @return a {@link ListNotesCommand}
     */
    ListNotesCommand notesList() {
        return git.notesList()
    }
    /**
     * Returns a command to show notes on an object
     *
     * @return a {@link ShowNoteCommand}
     */
    ShowNoteCommand notesShow() {
        return git.notesShow()
    }
    /**
     * Returns a command object to execute a {@code ls-remote} command
     *
     * @see <a
     *      href="http://www.kernel.org/pub/software/scm/git/docs/git-ls-remote.html"
     *      >Git documentation about ls-remote</a>
     * @return a {@link LsRemoteCommand} used to collect all optional parameters
     *         and to finally execute the {@code status} command
     */
    LsRemoteCommand lsRemote() {
        return git.lsRemote()
    }
    /**
     * Returns a command object to execute a {@code clean} command
     *
     * @see <a
     *      href="http://www.kernel.org/pub/software/scm/git/docs/git-clean.html"
     *      >Git documentation about Clean</a>
     * @return a {@link CleanCommand} used to collect all optional parameters
     *         and to finally execute the {@code clean} command
     */
    CleanCommand clean() {
        return git.clean()
    }
    /**
     * Returns a command object to execute a {@code blame} command
     *
     * @see <a
     *      href="http://www.kernel.org/pub/software/scm/git/docs/git-blame.html"
     *      >Git documentation about Blame</a>
     * @return a {@link BlameCommand} used to collect all optional parameters
     *         and to finally execute the {@code blame} command
     */
    BlameCommand blame() {
        return git.blame()
    }
    /**
     * Returns a command object to execute a {@code reflog} command
     *
     * @see <a
     *      href="http://www.kernel.org/pub/software/scm/git/docs/git-reflog.html"
     *      >Git documentation about reflog</a>
     * @return a {@link ReflogCommand} used to collect all optional parameters
     *         and to finally execute the {@code reflog} command
     */
    ReflogCommand reflog() {
        return git.reflog()
    }
    /**
     * Returns a command object to execute a {@code diff} command
     *
     * @see <a
     *      href="http://www.kernel.org/pub/software/scm/git/docs/git-diff.html"
     *      >Git documentation about diff</a>
     * @return a {@link DiffCommand} used to collect all optional parameters and
     *         to finally execute the {@code diff} command
     */
    DiffCommand diff() {
        return git.diff()
    }
    /**
     * Returns a command object used to delete tags
     *
     * @return a {@link DeleteTagCommand}
     */
    DeleteTagCommand tagDelete() {
        return git.tagDelete()
    }
    /**
     * Returns a command object to execute a {@code submodule add} command
     *
     * @return a {@link SubmoduleAddCommand} used to add a new submodule to a
     *         parent repository
     */
    SubmoduleAddCommand submoduleAdd() {
        return git.submoduleAdd()
    }
    /**
     * Returns a command object to execute a {@code submodule init} command
     *
     * @return a {@link SubmoduleInitCommand} used to initialize the
     *         repository's config with settings from the .gitmodules file in
     *         the working tree
     */
    SubmoduleInitCommand submoduleInit() {
        return git.submoduleInit()
    }
    /**
     * Returns a command object to execute a {@code submodule status} command
     *
     * @return a {@link SubmoduleStatusCommand} used to report the status of a
     *         repository's configured submodules
     */
    SubmoduleStatusCommand submoduleStatus() {
        return git.submoduleStatus()
    }
    /**
     * Returns a command object to execute a {@code submodule sync} command
     *
     * @return a {@link SubmoduleSyncCommand} used to update the URL of a
     *         submodule from the parent repository's .gitmodules file
     */
    SubmoduleSyncCommand submoduleSync() {
        return git.submoduleSync()
    }
    /**
     * Returns a command object to execute a {@code submodule update} command
     *
     * @return a {@link SubmoduleUpdateCommand} used to update the submodules in
     *         a repository to the configured revision
     */
    SubmoduleUpdateCommand submoduleUpdate() {
        return git.submoduleUpdate()
    }
    /**
     * Returns a command object used to list stashed commits
     *
     * @return a {@link StashListCommand}
     */
    StashListCommand stashList() {
        return git.stashList()
    }
    /**
     * Returns a command object used to create a stashed commit
     *
     * @return a {@link StashCreateCommand}
     * @since 2.0
     */
    StashCreateCommand stashCreate() {
        return git.stashCreate()
    }
    /**
     * Returns a command object used to apply a stashed commit
     *
     * @return a {@link StashApplyCommand}
     * @since 2.0
     */
    StashApplyCommand stashApply() {
        return git.stashApply()
    }
    /**
     * Returns a command object used to drop a stashed commit
     *
     * @return a {@link StashDropCommand}
     * @since 2.0
     */
    StashDropCommand stashDrop() {
        return git.stashDrop()
    }
    /**
     * Returns a command object to execute a {@code apply} command
     *
     * @see <a
     *      href="http://www.kernel.org/pub/software/scm/git/docs/git-apply.html"
     *      >Git documentation about apply</a>
     *
     * @return a {@link ApplyCommand} used to collect all optional parameters
     *         and to finally execute the {@code apply} command
     * @since 2.0
     */
    ApplyCommand apply() {
        return git.apply()
    }
    /**
     * Returns a command object to execute a {@code gc} command
     *
     * @see <a
     *      href="http://www.kernel.org/pub/software/scm/git/docs/git-gc.html"
     *      >Git documentation about gc</a>
     *
     * @return a {@link GarbageCollectCommand} used to collect all optional
     *         parameters and to finally execute the {@code gc} command
     * @since 2.2
     */
    GarbageCollectCommand gc() {
        return git.gc()
    }
    /**
     * Returns a command object to find human-readable names of revisions.
     *
     * @return a {@link NameRevCommand}.
     * @since 3.0
     */
    NameRevCommand nameRev() {
        return git.nameRev()
    }
    /**
     * Returns a command object to come up with a short name that describes a
     * commit in terms of the nearest git tag.
     *
     * @return a {@link DescribeCommand}.
     * @since 3.2
     */
    DescribeCommand describe() {
        return git.describe()
    }
    /**
     * @return the git repository this class is interacting with
     */
    Repository getRepository() {
        return repository
    }
}
