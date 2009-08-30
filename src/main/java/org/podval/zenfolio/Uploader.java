package org.podval.zenfolio;

import com.zenfolio.www.api._1_1.ZfApi;
import com.zenfolio.www.api._1_1.ZfApiStub;
import com.zenfolio.www.api._1_1.Group;
import com.zenfolio.www.api._1_1.PhotoSet;
import com.zenfolio.www.api._1_1.ArrayOfChoice1Choice;

import java.rmi.RemoteException;

import java.security.NoSuchAlgorithmException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public final class Uploader {

    public Uploader(final String login, final String password) {
        this.login = login;
        this.password = password;
    }


    protected final void connect() throws RemoteException, UnsupportedEncodingException,
        NoSuchAlgorithmException, IOException
    {
        zenfolio = new ZfApiStub();
        Login.login(zenfolio, login, password);
    }


    public void list() throws RemoteException {
        final Group group = zenfolio.loadGroupHierarchy(login);
        list(group, 0, 3);
    }


    private void list(final Group group, final int level, final int upto) {
        indent(level);
        System.out.println(group.getTitle());

        final ArrayOfChoice1Choice[] elements = group.getElements().getArrayOfChoice1Choice();
        if (elements != null) {
            for (int i = 0; i < elements.length; i++) {
                final ArrayOfChoice1Choice element = elements[i];
                final Group subGroup = element.getGroup();
                if (subGroup != null) {
                    list(subGroup, level + 1, upto);
                } else {
                    list(element.getPhotoSet(), level + 1);
                }
            }
        }
    }


    private void list(final PhotoSet set, final int level) {
        indent(level);
        System.out.println(set.getTitle());
    }


    private void indent(final int level) {
        for (int i = 0; i < level; i++) {
            System.out.print(' ');
            System.out.print(' ');
        }
    }


    public static void main(final String[] args) throws Exception {
        final String login = args[0];
        final String password = args[1];
        final Uploader uploader = new Uploader(login, password);
        uploader.connect();
        uploader.list();
    }


    private final String login;


    private final String password;


    private ZfApi zenfolio;
}
