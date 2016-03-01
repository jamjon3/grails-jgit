package org.grails.plugins.jgit

import grails.util.Holders

trait JGitTrait {

    Object withJGit(Closure closure) {
        if (!closure) {
            return
        }
        def jgit = Holders.applicationContext.getBean(JGit)

        closure.delegate = jgit
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure(jgit.rootFolder)
    }
}