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

import org.podval.things.Indenter;
import org.podval.things.Thing;

import org.podval.picasa.model.PhotoEntry;


/**
 *
 * @author dub
 */
public final class PicasaThing extends Thing {

    public PicasaThing(final Picasa picasa, final PhotoEntry photo) {
        this.picasa = picasa;
        this.photo = photo;
    }


    @Override
    public String getName() {
        return photo.title;
    }


    @Override
    public void list(final Indenter out, final int level) {
        out.println(level,
            "<photo name=\"" + getName() +
            // TODO !!!
//            "\" date=\"" + getTakenOn() +
//            "\" size=\"" + getSize() +
//            "\" rotation=\"" + getRotation() + "\"" +
            "/>");
    }


    private final Picasa picasa;


    private final PhotoEntry photo;
}
