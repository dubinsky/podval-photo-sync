package org.podval.sync;

import org.podval.photo.ConnectionDescriptor;
import org.podval.directory.FileConnection;

import org.kohsuke.args4j.CmdLineException;

import java.net.URI;
import java.net.URISyntaxException;


public final class UriParser {

    private UriParser() {
    }


    public static ConnectionDescriptor fromUri(final String uriStr, final String suffix)
        throws CmdLineException
    {
        final URI uri;
        try {
            uri = new URI(uriStr);
        } catch (final URISyntaxException e) {
            throw new CmdLineException(e.getMessage());
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

        return new ConnectionDescriptor(
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
        return (scheme == null) ? FileConnection.SCHEME : scheme;
    }


    private static String addSuffix(final String what, final String suffix) {
        return ((what == null) || (suffix == null)) ? what : what + "/" + suffix;
    }
}
