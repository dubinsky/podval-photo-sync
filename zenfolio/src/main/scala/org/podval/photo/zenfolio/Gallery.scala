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

import org.podval.photo.{NonRootAlbum, PhotoException}

import com.zenfolio.www.api._1_1.PhotoSet

import java.rmi.RemoteException

import org.apache.commons.httpclient.{HttpClient, HttpStatus, NameValuePair}
import org.apache.commons.httpclient.methods.{PostMethod, RequestEntity, FileRequestEntity}
import org.apache.commons.httpclient.methods.multipart.{MultipartRequestEntity, Part, FilePart, StringPart}

import java.util.Date

import java.io.{File, IOException}


/* package */ final class Gallery(override val parentFolder: ZenfolioFolder[_], el: PhotoSet)
extends ZenfolioFolder[PhotoSet](el) with NonRootAlbum with Named[PhotoSet] {

    @throws(classOf[PhotoException])
    protected override def retrievePhotos(): Seq[P] = {
        // PhotoSet needs to be loaded, since in the "structure" it is not populated with the Photos.

        val id = element.getId()

        if (id != 0) {
            try {
                element = (transport.loadPhotoSet(id))
            } catch {
                case e: RemoteException => throw new PhotoException(e)
            }

            if ((element.getPhotos() != null) && (element.getPhotos().getPhoto() != null)) {
                element.getPhotos().getPhoto().map(new ZenfolioPhoto(this, _))
            } else {
                Seq[P]()
            }
        } else {
            Seq[P]()
        }
    }


    protected final def transport = connection.transport


    @throws(classOf[PhotoException])
    private def postFile(name: String, file: File) {
        try {
            val url = "http://www.zenfolio.com" + element.getUploadUrl()
            
            val filePost = new PostMethod(url)
            
            filePost.setRequestHeader(connection.getAuthTokenHeader())
            
            val date = new Date(file.lastModified());
            
            val entity: RequestEntity =
//                makeSimplifiedPost
                makeMultiPartPost(filePost, name, file, date.toString)

            filePost.setRequestEntity(entity);

            val client = new HttpClient()
            val status = client.executeMethod(filePost)

            filePost.releaseConnection()

            if (status != HttpStatus.SC_OK) {
                throw new PhotoException(HttpStatus.getStatusText(status))
            }
        } catch {
            case e: IOException => throw new PhotoException(e)
        }
    }


    @throws(classOf[IOException])
    private def makeMultiPartPost(
        filePost: PostMethod,
        name: String,
        file: File,
        date: String): RequestEntity =
    {
//        filePost.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true);

        val parts = Array[Part](
            new FilePart("file", name, file, "image/jpeg", "UTF-8"),
            new StringPart("file_modified", date))

        new MultipartRequestEntity(parts, filePost.getParams())
    }


    private def makeSimplifiedPost(
        filePost: PostMethod,
        name: String,
        file: File,
        date: String): RequestEntity =
    {
        val queryParameters = Array[NameValuePair](
          new NameValuePair("filename", name),
          new NameValuePair("modified", date))

        filePost.setQueryString(queryParameters)

        new FileRequestEntity(file, "image/jpeg")
    }
}
