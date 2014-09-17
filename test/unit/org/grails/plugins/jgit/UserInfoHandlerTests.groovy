package org.grails.plugins.jgit

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class UserInfoHandlerTests {

    void setUp() {
        // Setup logic here
        UserInfoHandler.metaClass.resolveEmail {->
            return 'sombody@mycompany.com'
        }
        UserInfoHandler.metaClass.resolveUsername {->
            return 'sombody'
        }
    }

    void tearDown() {
        // Tear down logic here
    }

    void testResolveEmail() {
        def userInfoHandler = new UserInfoHandler()
        assert userInfoHandler.resolveEmail() == 'sombody@mycompany.com'
    }

    void testResolveUsername() {
        def userInfoHandler = new UserInfoHandler()
        assert userInfoHandler.resolveUsername() == 'sombody'
    }
}
