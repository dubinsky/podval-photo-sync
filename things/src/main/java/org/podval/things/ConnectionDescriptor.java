package org.podval.things;


public final class ConnectionDescriptor {

    public ConnectionDescriptor(
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


    public String getScheme() {
        return scheme;
    }


    public String getLogin() {
        return login;
    }


    public String getPassword() {
        return password;
    }


    public String getHost() {
        return host;
    }


    public String getPath() {
        return path;
    }


    private final String scheme;


    private final String login;


    private final String password;


    private final String path;


    private final String host;
}
