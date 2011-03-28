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

import org.podval.photo.Connection;
import org.podval.photo.Folder;
import org.podval.photo.PhotoException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.HttpResponseException;

import java.io.IOException;


/**
 *
 * @author dub
 */
public class Picasa extends Connection<PicasaPhoto> {

    public Picasa(final String login, final String password) throws PhotoException {
        this.login = login;
        this.password = password;
        this.transport = Transport.create();
    }


    public String getLogin() {
        return login;
    }


    @Override
    public String getScheme() {
        return PicasaFactory.SCHEME;
    }


    @Override
    public void open() throws PhotoException {
        if (password != null) {
            login();
        }
    }


    private void login() throws PhotoException {
        try {
            Transport.authenticate(login, password, transport);

        } catch (final HttpResponseException e) {
            throw new PhotoException(e);
        } catch (final IOException e) {
            throw new PhotoException(e);
        }
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
