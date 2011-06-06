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
import org.podval.photo.picasa.model.{Feed, Entry, UserFeed, AlbumFeed, AlbumEntry, PhotoEntry,  PicasaUrl}

import com.google.api.client.googleapis.auth.clientlogin.ClientLogin

import com.google.api.client.googleapis.GoogleHeaders

import com.google.api.client.googleapis.xml.atom.{GoogleAtom, AtomPatchRelativeToOriginalContent}
import com.google.api.client.http.xml.atom.{AtomContent, AtomParser}
import com.google.api.client.xml.XmlNamespaceDictionary;

import com.google.api.client.http.{HttpRequestFactory, GenericUrl, HttpRequest, HttpRequestInitializer,
    HttpResponseException, MultipartRelatedContent, AbstractInputStreamContent, InputStreamContent}

import com.google.api.client.http.javanet.NetHttpTransport;

import java.util.logging.{Logger, Level}

import java.io.IOException


final class Picasa(connector: PicasaConnector) extends Connection[Picasa, PicasaFolder, PicasaPhoto](connector) {

    type T = HttpRequestFactory


    override def enableLowLevelLogging() {
        val logger = Logger.getLogger("com.google.api.client")
        logger.setLevel(Level.CONFIG);

        PicasaUrl.isLoggingEnabled = true;
    }


    override def isLoginRequired: Boolean = true


    protected override def createTransport(): HttpRequestFactory = {
        new NetHttpTransport().createRequestFactory(new HttpRequestInitializer() {
            @throws(classOf[IOException])
            def initialize(request: HttpRequest) = initializeRequest(request)
        })
    }


    @throws(classOf[PhotoException])
    protected override def login(login: String, password: String) {
        try {
            val authenticator = new ClientLogin()
            authenticator.authTokenType = "lh2" //"ndev";
            authenticator.username = login
            authenticator.password = password
            authorizationHeaderValue = Some(authenticator.authenticate().getAuthorizationHeaderValue())
        } catch {
            case e: HttpResponseException => throw new PhotoException(e)
            case e: IOException => throw new PhotoException(e)
        }
    }


    private var authorizationHeaderValue: Option[String] = None


    @throws(classOf[IOException])
    private def initializeRequest(request: HttpRequest) {
        val headers = new GoogleHeaders();
        request.headers = headers;

        headers.setApplicationName("Podval-Photo-Sync/1.0")
        headers.gdataVersion = "2"

        if (authorizationHeaderValue.isDefined) {
            headers.authorization = authorizationHeaderValue.get
        }
        
        val parser = new AtomParser()
        parser.namespaceDictionary = Picasa.DICTIONARY
        request.addParser(parser)
    }


    // TPDP: transport.shutdown()


    protected override def createRootFolder(): R = new PicasaAlbumList(this)


    @throws(classOf[IOException])
    def executeDeleteEntry(entry: Entry) {
        val request = transport.buildDeleteRequest(new GenericUrl(entry.getEditLink()))
        request.headers.ifMatch = entry.etag
        request.execute().ignore()
    }


    @throws(classOf[IOException])
    def executeGetEntry(url: PicasaUrl, entryClass: Class[_ <: Entry]){
        url.fields = GoogleAtom.getFieldsFor(entryClass)
        val request = transport.buildGetRequest(url)
        request.execute().parseAs(entryClass)
    }


    @throws(classOf[IOException])
    def executePatchEntryRelativeToOriginal(updated: Entry, original: Entry): Entry = {
        val content = new AtomPatchRelativeToOriginalContent()
        content.namespaceDictionary = Picasa.DICTIONARY
        content.originalEntry = original
        content.patchedEntry = updated
        val request = transport.buildPatchRequest(new GenericUrl(updated.getEditLink()), content)
        request.headers.ifMatch = updated.etag
        request.execute().parseAs(classOf[Entry])
    }


    @throws(classOf[IOException])
    def executeGetAlbum(link: String): AlbumEntry =
        executeGetEntry(new PicasaUrl(link), classOf[AlbumEntry]).asInstanceOf[AlbumEntry]


    @throws(classOf[IOException])
    def executeGetFeed[F <: Feed](url: PicasaUrl, feedClass: Class[F]): F = {
        url.fields = GoogleAtom.getFieldsFor(feedClass)
        val request = transport.buildGetRequest(url)
        request.execute().parseAs(feedClass)
    }


    @throws(classOf[IOException])
    def executeInsert(feed: Feed, entry: Entry): Entry = {
        val content = new AtomContent()
        content.namespaceDictionary = Picasa.DICTIONARY
        content.entry = entry
        val request = transport.buildPostRequest(new GenericUrl(feed.getPostLink()), content)
        request.execute().parseAs(classOf[Entry])
    }


    @throws(classOf[IOException])
    def executeInsertPhotoEntry(
        albumFeedLink: String, content: InputStreamContent, fileName: String): PhotoEntry =
    {
        val request = transport.buildPostRequest(new GenericUrl(albumFeedLink), content)
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
        val request = transport.buildPostRequest(new GenericUrl(albumFeedLink), null)
        val atomContent = new AtomContent()
        atomContent.namespaceDictionary = Picasa.DICTIONARY
        atomContent.entry = photo
        val multiPartContent = MultipartRelatedContent.forRequest(request)
        multiPartContent.parts.add(atomContent)
        multiPartContent.parts.add(content)
        request.content = multiPartContent
        request.execute().parseAs(classOf[PhotoEntry])
    }
}



import com.google.api.client.xml.XmlNamespaceDictionary


object Picasa {

    val DICTIONARY = new XmlNamespaceDictionary()
        .set("", "http://www.w3.org/2005/Atom")
        .set("exif", "http://schemas.google.com/photos/exif/2007")
        .set("gd", "http://schemas.google.com/g/2005")
        .set("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#")
        .set("georss", "http://www.georss.org/georss")
        .set("gml", "http://www.opengis.net/gml")
        .set("gphoto", "http://schemas.google.com/photos/2007")
        .set("media", "http://search.yahoo.com/mrss/")
        .set("openSearch", "http://a9.com/-/spec/opensearch/1.1/")
        .set("xml", "http://www.w3.org/XML/1998/namespace")
}



final class PicasaConnector extends Connector("picasa") {

    def connect() = new Picasa(this)
}
