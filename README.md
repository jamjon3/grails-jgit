grails-jgit
==============================

Grails JGit plugin creates a wrapper around the JGit library. 

##Table of Contents

1. [Introduction](#introduction)
2. [Usage](#usage)
3. [Installation](#installation)
4. [Configuration](#configuration)

##Introduction

The JGit library is very useful for handling Git repositories in Java applications. This implementation intends on making it more "groovy".

##Usage

Adds a dynamic method for JGit. The closure includes the root folder of the repository (aka: 'rf')

```
withJGit() { rf ->
  # do stuff on the jgit class using its methods
}
```

Example:

```
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

###Installation

Download the latest version of the plugin and refer to it in BuildConfig.groovy by its path as:

```
grails.plugin.location.'jgit' = "../jgit"
```

##Configuration

After you have installed the plugin, run this command to add the necessary configuration options:

```
grails jgit-config
```

The following lines will be added to your Config.groovy:

```
jgit.userInfoHandler = 'org.grails.plugins.jgit.UserInfoHandler'
jgit.fallbackMap = [:]
jgit.fallbackEmailDefault = 'jdoe@foo.net'
jgit.fallbackUsername = 'jdoe'
jgit.branch = 'master'
jgit.gitRemoteURL = 'https://github.com/someuser/SomeApp.git'
jgit.gitRemotelogin = 'jdoe'
jgit.gitRemotePassword = 'mygitpassword'
jgit.injectInto = ['Controller', 'Service','Domain']
```

> **Note:** All of these property overrides must be specified in `grails-app/conf/Config.groovy` using the `jgit` prefix, for example:
> 
> ```
jgit.userInfoHandler =
     'org.grails.plugins.jgit.UserInfoHandler'
``` 
> 

| Name	                        | Default	                 | Meaning                                                                                                                                                                                               |
| ----------------------------- | ------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
|<sub>userInfoHandler</sub> | <sub>`org.grails.plugins.jgit.UserInfoHandler`</sub>	 | <sub>User Info Handler class</sub>                                                                                                                                                                            |
|<sub>fallbackMap</sub>	        | <sub>`[:]`</sub>  | <sub>A map of usernames to email addresses</sub>                                                                                                                                                                 |
|<sub>fallbackEmailDefault</sub>	        | <sub>`jdoe@foo.net`</sub>  | <sub>A default email address used when resolution fails</sub>                                                                                                                                                                 |
|<sub>fallbackUsername</sub>	        | <sub>`jdoe`</sub>  | <sub>A default username when resolution fails</sub>                                                                                                                                                                 |
|<sub>branch</sub>	                | <sub>`master`</sub>	                 | <sub>The "branch" being used to sync remotely</sub>                                                                                                                                      |
|<sub>gitRemoteURL</sub>	                | <sub>`https://github.com/someuser/SomeApp.git`</sub>	                 | <sub>The remote git repo for sync</sub>                                                                                                                                      |
|<sub>gitRemotelogin</sub>	| <sub>`jdoe`</sub>	| <sub>the remote user id</sub> |
|<sub>gitRemotePassword</sub>	                | <sub>`mygitpassword`</sub>	                 | <sub>the remote user password</sub>                                                                                                                                      |
|<sub>injectInto</sub>	                | <sub>`['Controller', 'Service','Domain']`</sub>	                 | <sub>The class types to be injected </sub>                                                                                                                                      |

###Using a Custom User Info Handler

There are many reasons why you might want to have a custom `UserInfoHandler` rather than hardcoding exclusively inside your config. 
You can extend the `UserInfoHandler` as a Groovy class and update the config option `jgit.userInfoHandler` to reference that new class.

However, perhaps you are using Spring Security and you can derive username and email attributes from there. Maybe something else. 
Regardless, simply define your own methods similar to those `org.grails.plugins.jgit.UserInfoHandler` and override the `jgitUserInfo` bean in `Resources.groovy`.

For example with using Spring Security, you may want to create a service that containing the `resolveEmail()` and `resolveUsername()` methods something like this:

```
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

You can define a bean for using it in `Resources.groovy` like:

```
beans = {
    jgitUserInfo { bean ->
        bean.parent = ref('springSecurityUserInfoHandlerService')
    } 
}
```
