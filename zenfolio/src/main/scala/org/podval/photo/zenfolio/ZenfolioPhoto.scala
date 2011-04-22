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

package org.podval.photo.zenfolio

import org.podval.photo.{Photo, Rotation}

import com.zenfolio.www.api._1_1.{Photo => ZPhoto, PhotoRotation}

import java.io.File

import java.util.{Date}


final class ZenfolioPhoto(protected val parent: Gallery, var photo: ZPhoto) extends Photo {

    // I do not deal with photos in the Groups; just in the Galleries.


    override def name(): String = photo.getFileName()


    override def timestamp(): Date = photo.getTakenOn().getTime()


    override def size(): Int = photo.getSize()


    // TODO: deal with null...
    override def rotation(): Rotation.Value = ZenfolioPhoto.rotations.get(photo.getRotation()).get


    override def getOriginalFile(): File = throw new UnsupportedOperationException("Not supported yet.")  // TODO: download
}



private object ZenfolioPhoto {

    val rotations = Map[PhotoRotation, Rotation.Value](
      PhotoRotation.None -> Rotation.None,
//        rotations.put(PhotoRotation.Flip, Rotation.Flip);
      PhotoRotation.Rotate180 -> Rotation.R180,
//        rotations.put(PhotoRotation.Rotate180Flip, Rotation.Rotate180Flip);
      PhotoRotation.Rotate270 -> Rotation.Left,
//        rotations.put(PhotoRotation.Rotate270Flip, Rotation.Rotate270);
      PhotoRotation.Rotate90 -> Rotation.Right
//        rotations.put(PhotoRotation.Rotate90Flip, Rotation.Rotate90Flip);
    )
}
