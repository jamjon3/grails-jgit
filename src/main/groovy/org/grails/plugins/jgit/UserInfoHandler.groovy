package org.grails.plugins.jgit

import grails.util.Holders

/**
 * @author james
 */
class UserInfoHandler {

    private config

    void setConfig(conf) {
        config = conf
    }

    /**
     * Resolves the Git email address and uses a fallback if necessary
     *
     * @param    username     The username used to resolve an email address
     * @return                The email address associated or fallback email address if not found
     */
    def resolveEmail() {
        def username = resolveUsername()
        return config.fallbackMap[username] ?: config.fallbackEmailDefault
    }

    /**
     * Resolves the Git username and uses a fallback
     *
     * @return                The username provided by fallback username
     */
    def resolveUsername() {
        return config.fallbackUsername
    }
}
