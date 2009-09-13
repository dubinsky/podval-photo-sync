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

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.util.List;
import java.util.ArrayList;


public class Zenfolio {

    public Zenfolio(final String login, final String password) throws RemoteException {
        this.login = login;
        this.password = password;
        this.connection = new ZfApiStub();
    }


    public void connect() throws RemoteException, UnsupportedEncodingException, NoSuchAlgorithmException, IOException {
        if (password != null) {
            login();
        }
    }


    private void login() throws RemoteException, UnsupportedEncodingException, NoSuchAlgorithmException, IOException {
        final AuthChallenge authChallenge = connection.getChallenge(login);

        final byte[] challenge = Bytes.readBytes(authChallenge.getChallenge());
        final byte[] passwordSalt = Bytes.readBytes(authChallenge.getPasswordSalt());
        final byte[] passwordUtf8 = password.getBytes("UTF-8");
        final byte[] passwordHash = Bytes.hash(Bytes.concatenate(passwordSalt, passwordUtf8));
        final byte[] proof = Bytes.hash(Bytes.concatenate(challenge, passwordHash));

        authToken = connection.authenticate(
            Bytes.wrapBytes(challenge),
            Bytes.wrapBytes(proof));

        final Options options = ((Stub) connection)._getServiceClient().getOptions();

//      options.setProperty(HTTPConstants.HEADER_COOKIE, "zf_token=" + authToken);

        final List headers = new ArrayList();
        headers.add(getAuthTokenHeader());
        options.setProperty(HTTPConstants.HTTP_HEADERS, headers);
    }


    /* package */ Header getAuthTokenHeader() {
        return new Header("X-Zenfolio-Token", authToken);
    }


    public ZenfolioDirectory loadGroupHierarchy() throws RemoteException {
        return new Group(this, connection.loadGroupHierarchy(login));
    }


    /* package */ ZfApi getConnection() {
        return connection;
    }


    private final String  login;


    private final String password;


    private final ZfApi connection;


    private String authToken;
}
