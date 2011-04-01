package org.podval.zenfolio;

import com.zenfolio.www.api._1_1.ZfApi;
import com.zenfolio.www.api._1_1.ZfApiStub;
import com.zenfolio.www.api._1_1.AuthChallenge;

import java.rmi.RemoteException;

import java.security.NoSuchAlgorithmException;

import org.apache.axis2.client.Stub;
import org.apache.axis2.client.Options;
import org.apache.axis2.transport.http.HTTPConstants;

import org.apache.commons.httpclient.Header;

import org.podval.photo.ConnectionDescriptor;
import org.podval.photo.Connection;
import org.podval.photo.Folder;
import org.podval.photo.PhotoException;

import java.io.IOException;

import java.util.List;
import java.util.ArrayList;


/* package */ final class Zenfolio extends Connection<ZenfolioPhoto> {

    public static final String SCHEME = "zenfolio";


    public Zenfolio(final ConnectionDescriptor descriptor) throws PhotoException {
        this.login = descriptor.getLogin();
        this.password = descriptor.getPassword();
        this.path = descriptor.getPath();

        try {
            this.connection = new ZfApiStub();
        } catch (final RemoteException e) {
            throw new PhotoException(e);
        }
    }


    @Override
    public String getScheme() {
        return SCHEME;
    }


    @Override
    public void enableLowLevelLogging() {
    }


    @Override
    public void open() throws PhotoException {
        if (password != null) {
            login();
        }
    }


    private void login() throws PhotoException {
        try {
            final AuthChallenge authChallenge = connection.getChallenge(login);

            final byte[] challenge = Bytes.readBytes(authChallenge.getChallenge());
            final byte[] passwordSalt = Bytes.readBytes(authChallenge.getPasswordSalt());
            final byte[] passwordUtf8 = password.getBytes("UTF-8");
            final byte[] passwordHash = Bytes.hash(Bytes.concatenate(passwordSalt, passwordUtf8));
            final byte[] proof = Bytes.hash(Bytes.concatenate(challenge, passwordHash));

            authToken = connection.authenticate(
                    Bytes.wrapBytes(challenge),
                    Bytes.wrapBytes(proof));

        } catch (final RemoteException e) {
            throw new PhotoException(e);
        } catch (final IOException e) {
            throw new PhotoException(e);
        } catch (final NoSuchAlgorithmException e) {
            throw new PhotoException(e);
        }

        final Options options = ((Stub) connection)._getServiceClient().getOptions();

//      options.setProperty(HTTPConstants.HEADER_COOKIE, "zf_token=" + authToken);

        final List headers = new ArrayList();
        headers.add(getAuthTokenHeader());
        options.setProperty(HTTPConstants.HTTP_HEADERS, headers);
    }


    /* package */ Header getAuthTokenHeader() {
        return new Header("X-Zenfolio-Token", authToken);
    }


    @Override
    public Folder<Zenfolio, ZenfolioPhoto> getRootFolder() throws PhotoException {
        return getSubFolderByPath(getRealRootFolder(), path);
    }


    private Folder<Zenfolio, ZenfolioPhoto> getRealRootFolder() throws PhotoException {
        try {
            return new Group(this, connection.loadGroupHierarchy(login));
        } catch (final RemoteException e) {
            throw new PhotoException(e);
        }
    }


    public ZfApi getConnection() {
        return connection;
    }


    private final String  login;


    private final String password;


    private final String path;


    private final ZfApi connection;


    private String authToken;
}
