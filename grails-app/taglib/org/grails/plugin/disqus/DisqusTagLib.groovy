package org.grails.plugins.disqus

import grails.config.Config
import grails.core.GrailsApplication
import grails.core.support.GrailsConfigurationAware
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.grails.plugin.disqus.Disqussable
import org.grails.web.gsp.GroovyPagesTemplateRenderer

@CompileStatic
class DisqusTagLib implements GrailsConfigurationAware {
    static namespace = "disqus"
    private static final String DISQUS_COMMENT_JS_REQUEST_KEY = "DISQUS_JS_REQ_KEY"

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
                        identifier: getIdentifier(attrs),
                        url       : url,
                        title     : title,
                        category  : category,
                        noscript  : noScript,
                        powered   : showPoweredByText
                ]]
        groovyPagesTemplateRenderer.render(getWebRequest(), getPageScope(), renderAttrs, null, getOut())
    }

    Closure commentsLink = { Map attrs, body ->
        String id = getIdentifier(attrs)
        attrs.remove('bean')
        attrs.put('data-disqus-identifier', id)
        attrs.put('fragment', '#disqus_thread')
        renderLink(attrs, body)
        renderCommentJsIfNecessary(attrs)
    }

    Closure commentsCount = { Map attrs, body ->
        String id = getIdentifier(attrs)
        out.write("<span class='disqus-comment-count' data-disqus-identifier='${id}'>${body}</span>".toString())
        renderCommentJsIfNecessary(attrs)
    }

    Closure commentsJs = { Map attrs ->
        String shortname = attrs['shortname'] ? attrs['shortname'].toString() : defaultShortName
        Map<String, Object> renderAttrs = [
                template: "/templates/disqus/discussCommentJs",
                model   : (Object) [
                        shortname: shortname
                ]]
        groovyPagesTemplateRenderer.render(getWebRequest(), getPageScope(), renderAttrs, null, getOut())
    }

    @CompileDynamic
    private void renderLink(Map attrs, def body) {
        link(attrs, body)
    }

    private void renderCommentJsIfNecessary(Map attrs) {
        if (!request.getAttribute(DISQUS_COMMENT_JS_REQUEST_KEY)) {
            request.setAttribute(DISQUS_COMMENT_JS_REQUEST_KEY, true)
            commentsJs.call(attrs)
        }
    }

    private static String getIdentifier(Map attrs) {
        if (attrs.identifier) {
            return attrs.identifier
        }
        if (identifierClosure) {
            return identifierClosure(attrs.bean)
        }
        String name = attrs.bean.class.name
        return "${name}#${getBeanId(attrs.bean)}"
    }

    @CompileDynamic
    private static String getBeanId(Object bean) {
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
