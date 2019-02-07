package org.grails.plugins.disqus.disqus


import grails.boot.config.GrailsAutoConfiguration

@PluginSource
class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }
}