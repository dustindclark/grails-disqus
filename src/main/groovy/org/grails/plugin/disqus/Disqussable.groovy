package org.grails.plugin.disqus

import groovy.transform.CompileStatic

@CompileStatic
trait Disqussable {
    abstract getDisqusId()
}