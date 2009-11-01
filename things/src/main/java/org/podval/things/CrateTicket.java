package org.podval.things;


public final class CrateTicket {

    public CrateTicket(
        final String scheme,
        final String login,
        final String password,
        final String host,
        final String path)
    {
        this.scheme = scheme;
        this.login = login;
        this.password = password;
        this.host= host;
        this.path = path;
    }


    public final String scheme;


    public final String login;


    public final String password;


    public final String path;


    public final String host;
}
