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

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleTransport;
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.xml.atom.AtomParser;

import java.io.IOException;

import org.podval.picasa.model.Util;


/**
 *
 * @author dub
 */
public final class Transport {

    public static HttpTransport create(final String applicationName) {
        final HttpTransport result = GoogleTransport.create();
        final GoogleHeaders headers = (GoogleHeaders) result.defaultHeaders;
        headers.setApplicationName(applicationName);
        headers.gdataVersion = "2";
        final AtomParser parser = new AtomParser();
        parser.namespaceDictionary = Util.NAMESPACE_DICTIONARY;
        result.addParser(parser);
        return result;
    }


    public static void authenticate(
        final String username,
        final String password,
        final HttpTransport transport) throws HttpResponseException, IOException
    {
        final ClientLogin authenticator = new ClientLogin();
        authenticator.authTokenType = "lh2"; //"ndev";
        authenticator.username = username;
        authenticator.password = password;
        authenticator.authenticate().setAuthorizationHeader(transport);
    }


    private Transport() {
    }
}
