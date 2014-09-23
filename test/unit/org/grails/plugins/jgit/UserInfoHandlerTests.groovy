package org.grails.plugins.jgit

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin

@TestMixin(GrailsUnitTestMixin)
class UserInfoHandlerTests {

    void testResolveEmail() {
        def userInfoHandler = new UserInfoHandler()
        userInfoHandler.setConfig([
            fallbackEmailDefault: 'sombody@mycompany.com',
            fallbackUsername: 'sombody',
            fallbackMap: [:]
        ])
        assert userInfoHandler.resolveEmail() == 'sombody@mycompany.com'
        userInfoHandler.setConfig([
            fallbackEmailDefault: 'sombody@mycompany.com',
            fallbackUsername: 'sombody',
            fallbackMap: [sombody: 'sombody@anothercompany.com']
        ])
        assert userInfoHandler.resolveEmail() == 'sombody@anothercompany.com'
    }

    void testResolveUsername() {
        def userInfoHandler = new UserInfoHandler()
        userInfoHandler.setConfig([
            fallbackUsername: 'sombody'
        ])
        assert userInfoHandler.resolveUsername() == 'sombody'
    }
}
