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

package org.podval.photo.picasa

import org.podval.photo.{Connector, Connection, PhotoException}
import org.podval.picasa.model.{Feed, Entry, UserFeed, AlbumFeed, AlbumEntry, PhotoEntry,  Namespaces, PicasaUrl}

import com.google.api.client.googleapis.GoogleHeaders
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.MethodOverride;
import com.google.api.client.googleapis.xml.atom.AtomPatchRelativeToOriginalContent;
import com.google.api.client.googleapis.xml.atom.GoogleAtom;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.MultipartRelatedContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.xml.atom.AtomContent;
import com.google.api.client.http.xml.atom.AtomParser;

import com.google.api.client.xml.XmlNamespaceDictionary;

import com.google.api.client.http.{HttpTransport, HttpResponseException}

import java.util.logging.{Logger, Level}

import java.io.IOException


final class Picasa(connector: PicasaConnector) extends Connection(connector) {

    type T = HttpTransport


    type C = Picasa


    type F = PicasaFolder


    override def enableLowLevelLogging() {
        val logger = Logger.getLogger("com.google.api.client")
        logger.setLevel(Level.CONFIG);

        PicasaUrl.isLoggingEnabled = true;
    }


    override def isLoginRequired: Boolean = true


    protected override def createTransport(): HttpTransport = {
        val result = new NetHttpTransport()

        val headers = result.defaultHeaders.asInstanceOf[GoogleHeaders]
        headers.setApplicationName("Podval-PicasaSync/1.0")
        headers.gdataVersion = "2"

        val parser = new AtomParser()
        parser.namespaceDictionary = Namespaces.DICTIONARY
        result.addParser(parser)

        result
    }


    @throws(classOf[PhotoException])
    protected override def login(login: String, password: String) {
        try {
            val authenticator = new ClientLogin()
            authenticator.authTokenType = "lh2" //"ndev";
            authenticator.username = login
            authenticator.password = password
            authenticator.authenticate().setAuthorizationHeader(transport)
        } catch {
            case e: HttpResponseException => throw new PhotoException(e)
            case e: IOException => throw new PhotoException(e)
        }
    }


    // TPDP: transport.shutdown()


    protected override def createRootFolder(): R = new PicasaAlbumList(this)


    private def getRequestFactory = {
        if (requestFactory.isEmpty) {
            requestFactory = Some(transport.createRequestFactory())
        }

        requestFactory.get
    }


    private var requestFactory: Option[HttpRequestFactory] = None


    @throws(classOf[IOException])
    def executeDeleteEntry(entry: Entry) {
        val request = getRequestFactory.buildDeleteRequest(new GenericUrl(entry.getEditLink()))
        request.headers.ifMatch = entry.etag
        request.execute().ignore()
    }


    @throws(classOf[IOException])
    def executeGetEntry(url: PicasaUrl, entryClass: Class[_ <: Entry]){
        url.fields = GoogleAtom.getFieldsFor(entryClass)
        val request = getRequestFactory.buildGetRequest(url)
        request.execute().parseAs(entryClass)
    }


    @throws(classOf[IOException])
    def executePatchEntryRelativeToOriginal(updated: Entry, original: Entry): Entry = {
        val content = new AtomPatchRelativeToOriginalContent()
        content.namespaceDictionary = Namespaces.DICTIONARY
        content.originalEntry = original
        content.patchedEntry = updated
        val request = getRequestFactory.buildPatchRequest(new GenericUrl(updated.getEditLink()), content)
        request.headers.ifMatch = updated.etag
        request.execute().parseAs(classOf[Entry])
    }


    @throws(classOf[IOException])
    def executeGetAlbum(link: String): AlbumEntry = {
        val url = new PicasaUrl(link)
        executeGetEntry(url, classOf[AlbumEntry]).asInstanceOf[AlbumEntry]
    }


    @throws(classOf[IOException])
    def executePatchAlbumRelativeToOriginal(updated: AlbumEntry, original: AlbumEntry): AlbumEntry =
        executePatchEntryRelativeToOriginal(updated, original).asInstanceOf[AlbumEntry]


    @throws(classOf[IOException])
    def executeGetFeed[F <: Feed](url: PicasaUrl, feedClass: Class[F]): F = {
        url.fields = GoogleAtom.getFieldsFor(feedClass)
        val request = getRequestFactory.buildGetRequest(url)
        request.execute().parseAs(feedClass)
    }


    @throws(classOf[IOException])
    def executeInsert(feed: Feed, entry: Entry): Entry = {
        val content = new AtomContent()
        content.namespaceDictionary = Namespaces.DICTIONARY
        content.entry = entry
        val request = getRequestFactory.buildPostRequest(new GenericUrl(feed.getPostLink()), content)
        request.execute().parseAs(classOf[Entry])
    }


    @throws(classOf[IOException])
    def executeGetAlbumFeed(url: PicasaUrl): AlbumFeed = {
        url.kinds = "photo"
        url.maxResults = 5
        executeGetFeed(url, classOf[AlbumFeed])
    }


    @throws(classOf[IOException])
    def executeGetUserFeed(url: PicasaUrl): UserFeed = {
        url.kinds = "album"
        url.maxResults = 3
        executeGetFeed(url, classOf[UserFeed])
    }


    @throws(classOf[IOException])
    def insertAlbum(userFeed: UserFeed, entry: AlbumEntry): AlbumEntry =
        executeInsert(userFeed, entry).asInstanceOf[AlbumEntry]


    @throws(classOf[IOException])
    def executeInsertPhotoEntry(
        albumFeedLink: String, content: InputStreamContent, fileName: String): PhotoEntry =
    {
        val request = getRequestFactory.buildPostRequest(new GenericUrl(albumFeedLink), content)
        val headers = request.headers.asInstanceOf[GoogleHeaders]
        headers.setSlugFromFileName(fileName)
        request.execute().parseAs(classOf[PhotoEntry])
    }


    @throws(classOf[IOException])
    def executeInsertPhotoEntryWithMetadata(
        photo: PhotoEntry,
        albumFeedLink: String,
        content: AbstractInputStreamContent): PhotoEntry =
    {
        val request = getRequestFactory.buildPostRequest(new GenericUrl(albumFeedLink), null)
        val atomContent = new AtomContent()
        atomContent.namespaceDictionary = Namespaces.DICTIONARY
        atomContent.entry = photo
        val multiPartContent = MultipartRelatedContent.forRequest(request)
        multiPartContent.parts.add(atomContent)
        multiPartContent.parts.add(content)
        request.content = multiPartContent
        request.execute().parseAs(classOf[PhotoEntry])
    }
}



final class PicasaConnector extends Connector("picasa") {

    def connect() = new Picasa(this)
}
