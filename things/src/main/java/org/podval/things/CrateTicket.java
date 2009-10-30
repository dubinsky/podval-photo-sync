package org.podval.things;


public final class CrateTicket {

    public CrateTicket(
        final String scheme,
        final String login,
        final String password,
        final String path)
    {
        this.scheme = scheme;
        this.login = login;
        this.password = password;
        this.path = path;
    }


    public final String scheme;


    public final String login;


    public final String password;


    public final String path;
}
