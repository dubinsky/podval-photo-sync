package org.podval.sync;

import org.podval.things.CrateTicket;

import org.apache.commons.cli.ParseException;

import org.junit.Assert;
import org.junit.Test;


public class UriParserTest {

    @Test
    public void schemeHostPath() throws ParseException {
        final CrateTicket result = UriParser.fromUri("scheme://host/path", null);
        Assert.assertEquals("scheme", result.scheme);
        Assert.assertEquals("host", result.host);
        Assert.assertEquals("/path", result.path);
    }


    @Test
    public void loginPassword() throws ParseException {
        loginPassword("http://a@b.c/d/e", "a", null);
        loginPassword("http://a:b@b.c/d/e", "a", "b");
        loginPassword("http://b.c/d/e", null, null);
        loginPassword("http://a@/d/e", "a", null);
        loginPassword("http://a:p@/d/e", "a", "p");
    }


    private void loginPassword(final String uri, final String login, final String password)
        throws ParseException
    {
        final CrateTicket result = UriParser.fromUri(uri, null);
        Assert.assertEquals(login, result.login);
        Assert.assertEquals(password, result.password);
    }
}
