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

import org.podval.photo.{Photo, Rotation}

import com.zenfolio.www.api._1_1.PhotoRotation

import java.io.File

import java.util.{Date, Map, HashMap}


final class ZenfolioPhoto(folder: Gallery, var photo: com.zenfolio.www.api._1_1.Photo) extends Photo {

    // I do not deal with photos in the Groups; just in the Galleries.


    override def name(): String = photo.getFileName()


    override def timestamp(): Date = photo.getTakenOn().getTime()


    override def size(): Int = photo.getSize()


    // TODO: deal with null...
    override def getRotation(): Rotation = ZenfolioPhoto.getRotation(photo.getRotation())


    override def getOriginalFile(): File = throw new UnsupportedOperationException("Not supported yet.")  // TODO: download
}



private object ZenfolioPhoto {

    private val rotations = new Map[PhotoRotation, Rotation](
      PhotoRotation.None -> Rotation.None,
//        rotations.put(PhotoRotation.Flip, Rotation.Flip);
      PhotoRotation.Rotate180 -> Rotation.R180,
//        rotations.put(PhotoRotation.Rotate180Flip, Rotation.Rotate180Flip);
      PhotoRotation.Rotate270 -> Rotation.Left,
//        rotations.put(PhotoRotation.Rotate270Flip, Rotation.Rotate270);
      PhotoRotation.Rotate90, Rotation.Right
//        rotations.put(PhotoRotation.Rotate90Flip, Rotation.Rotate90Flip);
    )


    // TODO: deal with Nobe...
    override def getRotation(rotation: PhotoRotation): Rotation = rotations.get(photo.getRotation(rotation)).get
}
