package org.podval.sync;

import org.podval.things.CrateTicket;

import java.net.URI;
import java.net.URISyntaxException;


public final class UriParser {

    private UriParser() {
    }


    public static CrateTicket fromUri(final String uriStr) throws URISyntaxException {
        final URI uri = new URI(uriStr);

        final String userInfo = getUserInfo(uri);

        final String login;
        final String password;

        if (userInfo == null) {
            login = null;
            password = null;
        } else {
            final int colon = userInfo.indexOf(":");
            if (colon == -1) {
                login = userInfo;
                password = null;
            } else {
                login = userInfo.substring(0, colon);
                password = userInfo.substring(colon+1);
            }
        }

        return new CrateTicket(uri.getScheme(), login, password, uri.getHost(), uri.getPath());
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
}
