package org.podval.sync;

import org.podval.things.CrateTicket;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;


public class UriParserTest {

    @Test
    public void schemeHostPath() throws URISyntaxException {
        final CrateTicket result = UriParser.fromUri("scheme://host/path");
        Assert.assertEquals("scheme", result.scheme);
        Assert.assertEquals("host", result.host);
        Assert.assertEquals("/path", result.path);
    }


    @Test
    public void loginPassword() throws URISyntaxException {
        loginPassword("http://a@b.c/d/e", "a", null);
        loginPassword("http://a:b@b.c/d/e", "a", "b");
        loginPassword("http://b.c/d/e", null, null);
        loginPassword("http://a@/d/e", "a", null);
        loginPassword("http://a:p@/d/e", "a", "p");
    }


    private void loginPassword(final String uri, final String login, final String password)
        throws URISyntaxException
    {
        final CrateTicket result = UriParser.fromUri(uri);
        Assert.assertEquals(login, result.login);
        Assert.assertEquals(password, result.password);
    }
}
