/*
 *  Copyright 2011 dub.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.podval.picasa;

import org.podval.photo.ConnectionDescriptor;
import org.podval.photo.Connection;
import org.podval.photo.Folder;
import org.podval.photo.PhotoException;

import org.podval.picasa.model.PicasaUrl;
import org.podval.picasa.model.Namespaces;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleTransport;
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin;
import com.google.api.client.xml.atom.AtomParser;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;


/**
 *
 * @author dub
 */
public class Picasa extends Connection<PicasaPhoto> {

    public static final String SCHEME = "picasa";


    public Picasa(final ConnectionDescriptor descriptor) throws PhotoException {
        if (!descriptor.getPath().isEmpty() && !descriptor.getPath().equals("/")) {
            throw new PhotoException("Picasa does not support hierarchy; path must be empty!");
        }

        this.login = descriptor.getLogin();
        this.password = descriptor.getPassword();

        this.transport = createTransport();
    }


    private HttpTransport createTransport() {
        return createTransport("Podval-PicasaSync/1.0");
    }


    private HttpTransport createTransport(final String applicationName) {
        final HttpTransport result = GoogleTransport.create();
        final GoogleHeaders headers = (GoogleHeaders) result.defaultHeaders;
        headers.setApplicationName(applicationName);
        headers.gdataVersion = "2";
        final AtomParser parser = new AtomParser();
        parser.namespaceDictionary = Namespaces.DICTIONARY;
        result.addParser(parser);
        return result;
    }


    public String getLogin() {
        return login;
    }


    @Override
    public String getScheme() {
        return SCHEME;
    }


    @Override
    public void enableLowLevelLogging() {
        Logger logger = Logger.getLogger("com.google.api.client");
        logger.setLevel(Level.CONFIG);

        PicasaUrl.isLoggingEnabled = true;
    }


    @Override
    public void open() throws PhotoException {
        if (password != null) {
            login();
        }
    }


    private void login() throws PhotoException {
        try {
            authenticate();

        } catch (final HttpResponseException e) {
            throw new PhotoException(e);
        } catch (final IOException e) {
            throw new PhotoException(e);
        }
    }


    private void authenticate() throws HttpResponseException, IOException {
        final ClientLogin authenticator = new ClientLogin();
        authenticator.authTokenType = "lh2"; //"ndev";
        authenticator.username = login;
        authenticator.password = password;
        authenticator.authenticate().setAuthorizationHeader(transport);
    }


    @Override
    public Folder<Picasa, PicasaPhoto> getRootFolder() throws PhotoException {
        return new RootFolder(this);
    }


    public HttpTransport getTransport() {
        return transport;
    }


    private final HttpTransport transport;


    private final String  login;


    private final String password;
}
