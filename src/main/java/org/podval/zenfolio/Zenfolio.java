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

import org.podval.things.Folder;
import org.podval.things.Crate;
import org.podval.things.ThingsException;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;


public final class Zenfolio extends Crate<Photo> {

    public Zenfolio(final String login, final String password) throws ThingsException {
        this.login = login;
        this.password = password;
        try {
            this.connection = new ZfApiStub();
        } catch (final RemoteException e) {
            throw new ThingsException(e);
        }
    }


    @Override
    public void open() throws ThingsException {
        if (password != null) {
            login();
        }
    }


    private void login() throws ThingsException {
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
            throw new ThingsException(e);
        } catch (final IOException e) {
            throw new ThingsException(e);
        } catch (final NoSuchAlgorithmException e) {
            throw new ThingsException(e);
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
    public Folder<Photo> getRootFolder() throws ThingsException {
        try {
            return new Group(this, connection.loadGroupHierarchy(login));
        } catch (final RemoteException e) {
            throw new ThingsException(e);
        }
    }


    @Override
    public File toFile(final Photo thing) {
        return null;
    }


    /* package */ ZfApi getConnection() {
        return connection;
    }


    private final String  login;


    private final String password;


    private final ZfApi connection;


    private String authToken;
}
