package org.podval.sync;

import org.podval.things.CrateTicket;
import org.podval.directory.FileFactory;

import org.apache.commons.cli.ParseException;

import java.net.URI;
import java.net.URISyntaxException;


public final class UriParser {

    private UriParser() {
    }


    public static CrateTicket fromUri(final String uriStr, final String suffix)
        throws ParseException
    {
        final URI uri;
        try {
            uri = new URI(uriStr);
        } catch (final URISyntaxException e) {
            throw new ParseException(e.getMessage());
        }

        final String userInfo = getUserInfo(uri);

        final String login;
        final String password;

        if (userInfo == null) {
            login = null;
            password = null;
        } else {
            final int colon = userInfo.indexOf(':');
            if (colon == -1) {
                login = userInfo;
                password = null;
            } else {
                login = userInfo.substring(0, colon);
                password = userInfo.substring(colon+1);
            }
        }

        return new CrateTicket(
            defaultScheme(uri.getScheme()),
            login,
            password,
            uri.getHost(),
            addSuffix(uri.getPath(), suffix));
    }


    private static String getUserInfo(final URI uri) {
        final String result;

        final String userInfo = uri.getUserInfo();
        final String host = uri.getHost();
        final String authority = uri.getAuthority();
        if ((host == null) && (authority != null)) {
            result = (authority.endsWith("@")) ?
                authority.substring(0, authority.length()-1) :
                authority;
        } else {
            result = userInfo;
        }

        return result;
    }


    private static String defaultScheme(final String scheme) {
        return (scheme == null) ? FileFactory.SCHEME : scheme;
    }


    private static String addSuffix(final String what, final String suffix) {
        return ((what == null) || (suffix == null)) ? what : what + "/" + suffix;
    }
}
