/*
 *  Copyright 2011 Leonid Dubinsky <dub@podval.org>.
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

package org.podval.zenfolio

import org.podval.photo.{PhotoNG, RotationNG}

import com.zenfolio.www.api._1_1.PhotoRotation

import java.io.File

import java.util.{Date, Map, HashMap}


final class ZenfolioPhoto extends PhotoNG {

    // I do not deal with photos in the Groups; just in the Galleries.
    /* package */ ZenfolioPhoto(final Gallery folder, final com.zenfolio.www.api._1_1.Photo photo) {
        super(folder);

        this.photo = photo;
    }


    @Override
    public String getName() {
        return photo.getFileName();
    }


    @Override
    public Date getTimestamp() {
        return photo.getTakenOn().getTime();
    }


    @Override
    public int getSize() {
        return photo.getSize();
    }


    private static final Map<PhotoRotation, Rotation> rotations = new HashMap<PhotoRotation, Rotation>();

    static {
        rotations.put(PhotoRotation.None, Rotation.None);
//        rotations.put(PhotoRotation.Flip, Rotation.Flip);
        rotations.put(PhotoRotation.Rotate180, Rotation.R180);
//        rotations.put(PhotoRotation.Rotate180Flip, Rotation.Rotate180Flip);
        rotations.put(PhotoRotation.Rotate270, Rotation.Left);
//        rotations.put(PhotoRotation.Rotate270Flip, Rotation.Rotate270);
        rotations.put(PhotoRotation.Rotate90, Rotation.Right);
//        rotations.put(PhotoRotation.Rotate90Flip, Rotation.Rotate90Flip);
    }


    @Override
    public Rotation getRotation() {
        final Rotation result = rotations.get(photo.getRotation());
        // TODO: deal with null...
        return result;
    }


    @Override
    public File getOriginalFile() {
        // TODO: download
        throw new UnsupportedOperationException("Not supported yet.");
    }


    private com.zenfolio.www.api._1_1.Photo photo;
}
