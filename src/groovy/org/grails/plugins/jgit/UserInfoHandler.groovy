/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.grails.plugins.jgit

import grails.util.Holders
/**
 *
 * @author james
 */
class UserInfoHandler {
    /**
     * Resolves the Git email address and uses a fallback if necessary
     * 
     * @param    username     The username used to resolve an email address
     * @return                The email address associated or fallback email address if not found
     */
    def resolveEmail() {
        def username = resolveUsername()
        if(Holders.config.jqit.fallbackMap[username]) {
            return Holders.config.jqit.fallbackMap[username]
        } else {
            // use the default
            return Holders.config.jgit.fallbackEmailDefault
        }
    }
    /**
     * Resolves the Git username and uses a fallback
     * 
     * @return                The username provided by fallback username
     */
    def resolveUsername() {
        return Holders.config.jgit.fallbackUsername
    }    
}

