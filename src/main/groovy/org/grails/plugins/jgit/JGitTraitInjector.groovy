package org.grails.plugins.jgit

import grails.compiler.traits.TraitInjector
import grails.config.Config
import grails.core.support.GrailsConfigurationAware

/**
 * Created by p277204 on 23-2-2016.
 */
class JGitTraitInjector implements TraitInjector, GrailsConfigurationAware {

    private Config config

    @Override
    void setConfiguration(Config co) {
        config = co
    }

    @Override
    Class getTrait() {
        return JGitTrait
    }

    @Override
    String[] getArtefactTypes() {
        return config?.jgit?.injectInto ?: ["Controller", "Service", "Domain"]
    }
}
