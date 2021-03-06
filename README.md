[![Build Status](https://travis-ci.org/dustindclark/grails-disqus.png?branch=master)](https://travis-ci.org/dustindclark/grails-disqus)

# Grails Disqus plugin

It provides embeding [DISQUS](http://disqus.com) commenting system into your Grails application.

## Installation

Latest Version:[ ![Download](https://api.bintray.com/packages/captivatelabs/plugins/disqus/images/download.svg) ](https://bintray.com/captivatelabs/plugins/disqus/_latestVersion)

## Configuration

Plugin requires the site's shortname option. You can specify it in Config.groovy:

`grails.plugin.disqus.shortname = "mysiteshortname"`

You can also pass shortname attribute to `disqus:comments` tag. To get shortname visit http://disqus.com/

To embed comments use following tag:

`<disqus:comments bean="${article}" />`
or
`<disqus:comments identifier="page-1" />`

You must add either `bean` or `identifier` attribute. For bean attribute any object can be passed and it will generate
the following identifier, ex. "Article@1".

Optional attributes:

- `url`. Permlink to the page. If not specified then request url will be used.
- `shortname`. The shortname of the site generated in www.disqus.com during registration

### NOTICE
The Disqus will not work properly until the page will be deployed under the registered domain. This is normal behavior :)

## More configuration

To translate "noscript" message you can add config option:

`grails.plugin.disqus.noscript = "You don't have javascript therefor no comments for you :("`


To change poweredby message you can add config option or just use empty string to remove it:

`grails.plugin.disqus.powered = "Comments powered by Disqus"`


Identifier generation can by modified by creating identifier closure in Config.groovy:

`grails.plugin.disqus.identifier = { bean ->
    return bean.toString()
}`


Plugin can be disabled by setting enabled = false in config
`grails.plugin.disqus.enabled = false`



