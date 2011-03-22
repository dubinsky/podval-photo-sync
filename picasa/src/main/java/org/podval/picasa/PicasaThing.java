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

import org.podval.things.Rotation;
import org.podval.things.Thing;

import org.podval.picasa.model.PhotoEntry;

import java.util.Date;


/**
 *
 * @author dub
 */
public final class PicasaThing extends Thing {

    /* package */ PicasaThing(final Picasa picasa, final PhotoEntry photo) {
        this.picasa = picasa;
        this.photo = photo;
    }


    @Override
    public String getName() {
        return photo.title;
    }


    @Override
    public Date getTimestamp() {
        return new Date(photo.timestamp);
    }


    @Override
    public int getSize() {
        return photo.size;
    }


    @Override
    public Rotation getRotation() {
        final Rotation result;
        final int value = photo.rotation;
        if (value == 0) {
            result = Rotation.None;
        } else if(value == 90) {
            result = Rotation.Right;
        } else if (value == 180) {
            result = Rotation.R180;
        } else if (value == 270) {
            result = Rotation.Left;
        } else {
            result = null;
            // TODO deal with null...
        }

        return result;
    }


    private final Picasa picasa;


    private final PhotoEntry photo;
}
