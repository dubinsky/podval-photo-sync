package org.podval.sync;

import org.podval.photo.ConnectionDescriptor;
import org.kohsuke.args4j.CmdLineException;

import org.junit.Assert;
import org.junit.Test;


public class UriParserTest {

    @Test
    public void schemeHostPath() throws CmdLineException {
        final ConnectionDescriptor result = UriParser.fromUri("scheme://host/path", null);
        Assert.assertEquals("scheme", result.getScheme());
        Assert.assertEquals("host", result.getHost());
        Assert.assertEquals("/path", result.getPath());
    }


    @Test
    public void loginPassword() throws CmdLineException {
        loginPassword("http://a@b.c/d/e", "a", null);
        loginPassword("http://a:b@b.c/d/e", "a", "b");
        loginPassword("http://b.c/d/e", null, null);
        loginPassword("http://a@/d/e", "a", null);
        loginPassword("http://a:p@/d/e", "a", "p");
    }


    private void loginPassword(final String uri, final String login, final String password)
        throws CmdLineException
    {
        final ConnectionDescriptor result = UriParser.fromUri(uri, null);
        Assert.assertEquals(login, result.getLogin());
        Assert.assertEquals(password, result.getPassword());
    }
}
