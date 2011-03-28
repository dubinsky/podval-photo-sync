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

import org.podval.things.Connection;
import org.podval.things.ConnectionFactory;
import org.podval.things.ConnectionDescriptor;
import org.podval.things.PhotoException;

/**
 *
 * @author dub
 */
public class PicasaFactory extends ConnectionFactory<PicasaThing> {

    public static final String SCHEME = "picasa";


    @Override
    public Connection<PicasaThing> createConnection(final ConnectionDescriptor ticket) throws PhotoException {
        // TODO: check that ticket.getPath() is empty
        return new Picasa(ticket.getLogin(), ticket.getPassword());
    }


    @Override
    public String getScheme() {
        return SCHEME;
    }
}
