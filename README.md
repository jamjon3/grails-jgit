grails-jgit
==============================

Grails JGit plugin creates a wrapper around the JGit library. 

## Table of Contents

1. [Introduction](#introduction)
2. [Usage](#usage)
3. [Installation](#installation)
4. [Configuration](#configuration)

## Introduction

The JGit library is very useful for handling Git repositories in Java applications. This implementation intends on making it more "groovy".

## Usage

Adds a dynamic method for JGit. The closure includes the root folder of the repository (aka: 'rf')

```Groovy
withJGit() { rf ->
  // do stuff on the jgit class using its methods
}
```

Example:

```Groovy
withJGit() { rf ->
    pull().call()
    def f = new File(rf,"test.txt")
    f.createNewFile()
    // Relative path
    add().addFilepattern(f.name).call()
    if(!status().call().isClean()) {
        commit().setMessage("some comment").call()
    }
    push().call()
    pull().call()
}
```

### Installation

Add to your BuildConfig.groovy:
```Gradle
plugins {
   compile ':jgit:2.0.0.BUILD-SNAPSHOT'
}
```

## Configuration

After you have installed the plugin, run this command to add the necessary configuration options:

```
grails jgit-config
```

The following lines will be added to your `application.groovy` (or `application.yml` if no `application.groovy` exists):

```Groovy
jgit.userInfoHandler = 'org.grails.plugins.jgit.UserInfoHandler'
jgit.fallbackMap = [:]
jgit.fallbackEmailDefault = 'jdoe@foo.net'
jgit.fallbackUsername = 'jdoe'
jgit.branch = 'master'
jgit.gitRemoteURL = 'https://github.com/someuser/SomeApp.git'
jgit.http.gitRemotelogin = 'jdoe'
jgit.http.gitRemotePassword = 'mygitpassword'
jgit.injectInto = ['Controller', 'Service','Domain']
```

> **Note:** All of these property overrides must be specified in `grails-app/conf/application.groovy` using the `jgit` prefix, for example:
> 
>```
>jgit.userInfoHandler =
>     'org.grails.plugins.jgit.UserInfoHandler'
>```


| Name	                        | Default	                 | Meaning                                                                                                                                                                                               |
| ----------------------------- | ------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
|<sub>userInfoHandler</sub> | <sub>`org.grails.plugins.jgit.UserInfoHandler`</sub>	 | <sub>User Info Handler class</sub>                                                                                                                                                                            |
|<sub>fallbackMap</sub>	        | <sub>`[:]`</sub>  | <sub>A map of usernames to email addresses</sub>                                                                                                                                                                 |
|<sub>fallbackEmailDefault</sub>	        | <sub>`jdoe@foo.net`</sub>  | <sub>A default email address used when resolution fails</sub>                                                                                                                                                                 |
|<sub>fallbackUsername</sub>	        | <sub>`jdoe`</sub>  | <sub>A default username when resolution fails</sub>                                                                                                                                                                 |
|<sub>branch</sub>	                | <sub>`master`</sub>	                 | <sub>The "branch" being used to sync remotely</sub>                                                                                                                                      |
|<sub>gitRemoteURL</sub>	                | <sub>`https://github.com/someuser/SomeApp.git`</sub>	                 | <sub>The remote git repo for sync</sub>                                                                                                                                      |
|<sub>http.gitRemotelogin</sub>	| <sub>`jdoe`</sub>	| <sub>the remote user id</sub> |
|<sub>http.gitRemotePassword</sub>	                | <sub>`mygitpassword`</sub>	                 | <sub>the remote user password</sub>                                                                                                                                      |
|<sub>ssh.sshPassphrase</sub>	| <sub>`some passphrase`</sub>	| <sub>the passphrase associated with your private key</sub> |
|<sub>ssh.sshPrivKey</sub>	| <sub>Text of the .ssh/id_rsa file</sub>	| <sub>the full text of your private key</sub> |
|<sub>ssh.sshPubKey</sub>	| <sub>Text of the .ssh/id_rsa.pub file</sub>	| <sub>the full text of your public key</sub> |
|<sub>ssh.sshPrivKeyPath</sub>	| <sub>`/home/jdoe/.ssh/id_rsa`</sub>	| <sub>the path of your private key</sub> |
|<sub>ssh.sshPubKeyPath</sub>	| <sub>`/home/jdoe/.ssh/id_rsa.pub`</sub>	| <sub>the path of your public key</sub> |
|<sub>ssh.strictHostKeyChecking</sub>	                | <sub>`yes`</sub>	                 | <sub>yes/no option to toggle strict host key checking on and off</sub>                                                                                                                                      |
|<sub>injectInto</sub>	                | <sub>`['Controller', 'Service','Domain']`</sub>	                 | <sub>The class types to be injected </sub>                                                                                                                                      |

### Using a remote ssh repository

By default, the plugin connects over http with a username and password. However, when you need to connect to a ssh style URL (ex: `git@somehost.com:MyProject.git`), you'll
need to specify ssh config options for key authentication. If the remote git host has your public key as an authorized key, you should be able to simply specify
your ssh passphrase and private key in your config file something like:

```
jgit.ssh.sshPassphrase = 'MySecretPassphrase'
jgit.ssh.sshPrivKey = """
-----BEGIN RSA PRIVATE KEY-----
Proc-Type: 4,ENCRYPTED
DEK-Info: AES-128-CBC,
....all the key data here....
-----END RSA PRIVATE KEY-----
"""
```

Of course, you can override the `sshSessionFactory` with your own ssh session factory if you require special handling
not provided in the `FlexibleSshSessionFactory` class.

### Using a Custom User Info Handler

There are many reasons why you might want to have a custom `UserInfoHandler` rather than hardcoding exclusively inside your config. 
You can extend the `UserInfoHandler` as a Groovy class and update the config option `jgit.userInfoHandler` to reference that new class.

However, perhaps you are using Spring Security and you can derive username and email attributes from there. Maybe something else. 
Regardless, simply define your own methods similar to those in `org.grails.plugins.jgit.UserInfoHandler` and override the `jgitUserInfo` bean in `resources.groovy`.

For example with using Spring Security, you may want to just create a service that contains the `resolveEmail()` and `resolveUsername()` methods something like this:

```Groovy
class SpringSecurityUserInfoHandlerService {
    def springSecurityService
    def grailsApplication
    /**
     * Resolves the Git email address and uses a fallback if necessary
     *
     * @param    username     The username used to resolve an email address
     * @return                The email address associated or fallback email address if not found
     */
    def resolveEmail() {
        def username = resolveUsername()
        return grailsApplication.config.fallbackMap[username] ?: grailsApplication.config.fallbackEmailDefault
    }

    /**
     * Resolves the Git username from Spring Security
     *
     * @return                The username provided by Spring Security
     */
    def resolveUsername() {
        def principal = springSecurityService.principal
        return principal.username
    }
}
```

You can define a bean for using it in `resources.groovy` like:

```Groovy
beans = {
    jgitUserInfo { bean ->
        bean.parent = ref('springSecurityUserInfoHandlerService')
    } 
}
```
