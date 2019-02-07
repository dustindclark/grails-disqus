package org.grails.plugins.disqus

import groovy.transform.CompileStatic

@CompileStatic
trait Disqussable {
    abstract getDisqusId()
}