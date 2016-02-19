/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.grails.plugins.jgit

import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.util.FS
import org.eclipse.jgit.transport.OpenSshConfig

/**
 *
 * @author james
 */
class FlexibleSshSessionFactory extends JschConfigSessionFactory {
    private config

    void setConfig(conf) {
        config = conf
    }
    
    @Override
    public void configure(OpenSshConfig.Host hc, Session session) {
        session.setConfig("StrictHostKeyChecking", (config.strictHostKeyChecking)?config.strictHostKeyChecking:"yes");
    }
    
    @Override
    protected JSch getJSch(final OpenSshConfig.Host hc, FS fs) throws JSchException {
        JSch jsch = super.getJSch(hc, fs);
        jsch.removeAllIdentity();
        //Where getSshKey returns content of the private key file
        if (config.sshPrivKey) {            
            jsch.addIdentity("identityName", config.sshPrivKey.toString().getBytes(), config?.sshPubKey?.toString()?.getBytes(), (byte[]) config?.sshPassphrase?.toString()?.getBytes());
        } else if(config.sshPrivKeyPath) {
            if(config.sshPubKeyPath) {
                if(config.sshPassphrase) {
                    jsch.addIdentity(config.sshPrivKeyPath,config.sshPubKeyPath,config.sshPassphrase.getBytes());
                }                
            } else {
                if(config.sshPassphrase) {
                    jsch.addIdentity(config.sshPrivKeyPath,config.sshPassphrase.getBytes());
                } else {
                    jsch.addIdentity(config.sshPrivKeyPath);
                }
            }
        }           
        return jsch;
    }
}

