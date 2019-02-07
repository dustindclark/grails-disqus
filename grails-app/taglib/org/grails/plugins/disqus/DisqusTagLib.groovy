package org.grails.plugins.disqus

import grails.config.Config
import grails.core.GrailsApplication
import grails.core.support.GrailsConfigurationAware
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.grails.web.gsp.GroovyPagesTemplateRenderer

@CompileStatic
class DisqusTagLib implements GrailsConfigurationAware {
    static namespace = "disqus"
    private static boolean isEnabled
    private static boolean showPoweredByText
    private static String defaultShortName
    private static String noScript
    private static Closure identifierClosure
    private static Closure urlClosure

    GrailsApplication grailsApplication
    GroovyPagesTemplateRenderer groovyPagesTemplateRenderer


    /**
     * Sets a variable in the pageContext or the specified scope.
     * The value can be specified directly or can be a bean retrieved from the applicationContext.
     *
     * @attr shortname disqus shortname; if not specified value from the config will be used
     * @attr identifier disqus page identifier
     * @attr url disqus page url; if not specified current request url will be used
     * @attr bean OPTIONAL; if identifier is not specified the id property or hascode of the bean will be used
     */
    Closure comments = { Map attrs ->
        if (!isEnabled) {
            return
        }

        String shortname = attrs['shortname'] ? attrs['shortname'].toString() : defaultShortName
        String title = attrs['title']
        String category = attrs['category']

        if (!shortname) {
            throwTagError "Disqus can't be used because shortname is not configured."
        }

        String identifier = null
        if (attrs.identifier) {
            identifier = attrs.identifier
        } else if (identifierClosure) {
            identifier = identifierClosure(attrs.bean)
        } else if (attrs.bean) {
            Object bean = attrs.bean
            String name = bean.class.name
            identifier = "${name}#${getBeanId(bean)}"
        }

        String url
        if (attrs.url) {
            url = attrs.url
        } else if (urlClosure) {
            url = urlClosure()
        } else {
            url = request.getRequestURL()
        }

        Map<String, Object> renderAttrs = [
                template: "/templates/disqus/disqus",
                model   : (Object) [
                        shortname : shortname,
                        identifier: identifier,
                        url       : url,
                        title     : title,
                        category  : category,
                        noscript  : noScript,
                        powered   : showPoweredByText
                ]]
        groovyPagesTemplateRenderer.render(getWebRequest(), getPageScope(), renderAttrs, null, getOut())
    }

    @CompileDynamic
    String getBeanId(Object bean) {
        return bean in Disqussable ? bean.disqusId
                : bean.metaClass.properties.find { it.name == "id" }
                ? bean.id
                : bean.hashCode()
    }

    @Override
    void setConfiguration(Config co) {
        isEnabled = co.get("grails.plugin.disqus.enabled", true)
        defaultShortName = co.get("grails.plugin.disqus.shortname")
        noScript = co.get("grails.plugin.disqus.noscript")
        showPoweredByText = co.get("grails.plugin.disqus.powered", false)
        identifierClosure = co.get("grails.plugin.disqus.identifier") ? (Closure) co.get("grails.plugin.disqus.identifier") : null
        urlClosure = co.get("grails.plugin.disqus.url") ? (Closure) co.get("grails.plugin.disqus.url") : null
    }
}
