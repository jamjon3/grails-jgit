package org.grails.plugins.jgit

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin

@TestMixin(GrailsUnitTestMixin)
class UserInfoHandlerTests {

    void setUp() {
        UserInfoHandler.metaClass.resolveEmail {->
            return 'sombody@mycompany.com'
        }
        UserInfoHandler.metaClass.resolveUsername {->
            return 'sombody'
        }
    }

    void testResolveEmail() {
        assert new UserInfoHandler().resolveEmail() == 'sombody@mycompany.com'
    }

    void testResolveUsername() {
        assert new UserInfoHandler().resolveUsername() == 'sombody'
    }
}
