package org.podval.zenfolio;

import com.zenfolio.www.api._1_1.ZfApi;
import com.zenfolio.www.api._1_1.ZfApiStub;
import com.zenfolio.www.api._1_1.AuthChallenge;
import com.zenfolio.www.api._1_1.Group;
import com.zenfolio.www.api._1_1.GroupElement;
import com.zenfolio.www.api._1_1.GroupUpdater;
import com.zenfolio.www.api._1_1.PhotoSet;
import com.zenfolio.www.api._1_1.PhotoSetType;
import com.zenfolio.www.api._1_1.PhotoSetUpdater;
import com.zenfolio.www.api._1_1.ArrayOfChoice1;
import com.zenfolio.www.api._1_1.ArrayOfChoice1Choice;

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


    public Group findGroup(final String path) throws RemoteException {
        Group result = loadGroupHierarchy();

        if (path != null) {
            for (final String name : path.split("/")) {
                if (!name.isEmpty()) {
                    result = asGroup(find(result, name));
                }
            }
        }

        return result;
    }


    /* package */ ZfApi getConnection() {
        return connection;
    }


    public Group loadGroupHierarchy() throws RemoteException {
        return connection.loadGroupHierarchy(login);
    }


    public Group asGroup(final GroupElement element) {
        if (!(element instanceof Group)) {
            throw new IllegalArgumentException("Not a group: " + element);
        }

        return (Group) element;
    }


    public GroupElement find(final Group group, final String name) {
        GroupElement result = null;

        final ArrayOfChoice1Choice[] elements = getElements(group);

        if (elements != null) {
            for (final ArrayOfChoice1Choice element : elements) {
                GroupElement subElement = element.getGroup();
                if (subElement == null) {
                    subElement = element.getPhotoSet();
                }

                if (subElement.getTitle().equals(name)) {
                    result = subElement;
                    break;
                }
            }
        }

        return result;
    }


    public ArrayOfChoice1Choice[] getElements(final Group group) {
        final ArrayOfChoice1 array = group.getElements();

        return (array == null) ? new ArrayOfChoice1Choice[0] : array.getArrayOfChoice1Choice();
    }


    public GroupElement create(final Group group, final String name, final boolean shouldBeGroup, final boolean doIt)
        throws RemoteException
    {
        return (shouldBeGroup) ?
            createGroup(group, name, doIt) :
            createGallery(group, name, doIt);
    }


    public Group createGroup(final Group group, final String name, final boolean doIt) throws RemoteException {
        final Group result;

        if (doIt) {
            final GroupUpdater updater = new GroupUpdater();
            updater.setTitle(name);
            result = connection.createGroup(group.getId(), updater);
        } else {
            result = new Group();
            result.setTitle(name);
        }

        return result;
    }


    public PhotoSet createGallery(final Group group, final String name, final boolean doIt) throws RemoteException {
        final PhotoSet result;

        if (doIt) {
            final PhotoSetUpdater updater = new PhotoSetUpdater();
            updater.setTitle(name);
            result = connection.createPhotoSet(group.getId(), PhotoSetType.Gallery, updater);
        } else {
            result = new PhotoSet();
            result.setTitle(name);
            result.setType(PhotoSetType.Gallery);
        }

        return result;
    }


    private final String  login;


    private final String password;


    private final ZfApi connection;


    private String authToken;
}
