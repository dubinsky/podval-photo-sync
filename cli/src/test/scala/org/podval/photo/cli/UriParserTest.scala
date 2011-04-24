package org.podval.photo.cli

import org.kohsuke.args4j.CmdLineException

import org.junit.{Assert, Test}


class UriParserTest {

    @Test @throws(classOf[CmdLineException])
    def schemeHostPath() {
        val result = UriParser.fromUri("scheme://host/path", null)
        Assert.assertEquals("scheme", result.scheme)
        Assert.assertEquals("host", result.host)
        Assert.assertEquals("/path", result.path)
    }


    @Test @throws(classOf[CmdLineException])
    def loginPassword() {
        loginPassword("http://a@b.c/d/e", Some("a"), None)
        loginPassword("http://a:b@b.c/d/e", Some("a"), Some("b"))
        loginPassword("http://b.c/d/e", None, None)
        loginPassword("http://a@/d/e", Some("a"), None)
        loginPassword("http://a:p@/d/e", Some("a"), Some("p"))
    }


    @throws(classOf[CmdLineException])
    private def loginPassword(uri: String, login: Option[String], password: Option[String]) {
        val result = UriParser.fromUri(uri, null)
        Assert.assertEquals(login, result.login)
        Assert.assertEquals(password, result.password)
    }
}
