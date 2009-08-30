package org.podval.zenfolio;

import com.zenfolio.www.api._1_1.ZfApi;
import com.zenfolio.www.api._1_1.AuthChallenge;

import org.apache.axis2.client.Stub;
import org.apache.axis2.client.Options;
import org.apache.axis2.transport.http.HTTPConstants;

import org.apache.commons.httpclient.Header;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import java.rmi.RemoteException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.List;
import java.util.ArrayList;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


public final class Login {

    public static void login(final ZfApi zenfolio, final String loginName, final String password)
        throws RemoteException, UnsupportedEncodingException, NoSuchAlgorithmException, IOException
    {
        final AuthChallenge authChallenge = zenfolio.getChallenge(loginName);

        final byte[] challenge = readBytes(authChallenge.getChallenge());
        final byte[] passwordSalt = readBytes(authChallenge.getPasswordSalt());
        final byte[] passwordUtf8 = password.getBytes("UTF-8");
        final byte[] saltedPassword = concatenate(passwordSalt, passwordUtf8);
        final byte[] passwordHash = hash(saltedPassword);
        final byte[] proof = hash(concatenate(challenge, passwordHash));

        final DataHandler challengeHandler = wrapBytes(challenge);
        final DataHandler proofHandler = wrapBytes(proof);
        final String authToken = zenfolio.authenticate(challengeHandler, proofHandler);

        final Options options = ((Stub) zenfolio)._getServiceClient().getOptions();

//      options.setProperty(HTTPConstants.HEADER_COOKIE, "zf_token=" + authToken);

        final List headers = new ArrayList();
        headers.add(new Header("X-Zenfolio-Token", authToken));
        options.setProperty(HTTPConstants.HTTP_HEADERS, headers);
    }



    public static byte[] hash(final byte[] what) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        return digest.digest(what);
    }


    public static byte[] concatenate(final byte[] first, final byte[] second) {
        final byte[] result = new byte[first.length + second.length];

        for (int i = 0; i<first.length; i++) {
            result[i] = first[i];
        }

        for (int i = 0; i<second.length; i++) {
            result[first.length+i] = second[i];
        }

        return result;
    }


    private static byte[] readBytes(final DataHandler dataHandler) throws IOException {
        final ByteArrayInputStream is = (ByteArrayInputStream) dataHandler.getContent();
        final int length = is.available();
        final byte[] result = new byte[length];
        is.read(result);
        return result;
    }


    private static DataHandler wrapBytes(final byte[] bytes) {
        return new DataHandler(new DataSource() {

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(bytes);
            }


            @Override
            public OutputStream getOutputStream() throws IOException {
                throw new UnsupportedOperationException("Not supported yet.");
            }


            @Override
            public String getContentType() {
                return "application/octet-stream";
            }


            @Override
            public String getName() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
    }
}
