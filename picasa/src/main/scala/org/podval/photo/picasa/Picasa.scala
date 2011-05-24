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
import org.podval.picasa.model.{Namespaces, PicasaUrl}

import com.google.api.client.googleapis.{GoogleTransport, GoogleHeaders}
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin
import com.google.api.client.http.{HttpTransport, HttpResponseException}
import com.google.api.client.xml.atom.AtomParser

import java.util.logging.{Logger, Level}

import java.io.IOException


final class Picasa(connector: PicasaConnector) extends Connection[HttpTransport](connector) {

    type F = PicasaFolder


    override def enableLowLevelLogging() {
        val logger = Logger.getLogger("com.google.api.client")
        logger.setLevel(Level.CONFIG);

        PicasaUrl.isLoggingEnabled = true;
    }


    override def isLoginRequired: Boolean = true


    protected override def createTransport(): HttpTransport = {
        val result = GoogleTransport.create()

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










    @Throws(classOf[IOException])
    def executeDeleteEntry(entry: Entry) {
        val request = requestFactory.buildDeleteRequest(new GenericUrl(entry.getEditLink()))
        request.headers.ifMatch = entry.etag
        request.execute().ignore()
    }


    @Throws(classOf[IOException])
  def executeGetEntry(url: PicasaUrl, <? extends Entry> entryClass){
    url.fields = GoogleAtom.getFieldsFor(entryClass);
    HttpRequest request = requestFactory.buildGetRequest(url);
    return request.execute().parseAs(entryClass);
  }

    @Throws(classOf[IOException])
  Entry executePatchEntryRelativeToOriginal(Entry updated, Entry original)  {
    AtomPatchRelativeToOriginalContent content = new AtomPatchRelativeToOriginalContent();
    content.namespaceDictionary = DICTIONARY;
    content.originalEntry = original;
    content.patchedEntry = updated;
    HttpRequest request =
        requestFactory.buildPatchRequest(new GenericUrl(updated.getEditLink()), content);
    request.headers.ifMatch = updated.etag;
    return request.execute().parseAs(updated.getClass());
  }

    @Throws(classOf[IOException])
  public AlbumEntry executeGetAlbum(String link){
    PicasaUrl url = new PicasaUrl(link);
    return (AlbumEntry) executeGetEntry(url, AlbumEntry.class);
  }

    @Throws(classOf[IOException])
  public AlbumEntry executePatchAlbumRelativeToOriginal(AlbumEntry updated, AlbumEntry original)
      {
    return (AlbumEntry) executePatchEntryRelativeToOriginal(updated, original);
  }

    @Throws(classOf[IOException])
  <F extends Feed> F executeGetFeed(PicasaUrl url, Class<F> feedClass)
      {
    url.fields = GoogleAtom.getFieldsFor(feedClass);
    HttpRequest request = requestFactory.buildGetRequest(url);
    return request.execute().parseAs(feedClass);
  }

    @Throws(classOf[IOException])
  Entry executeInsert(Feed feed, Entry entry){
    AtomContent content = new AtomContent();
    content.namespaceDictionary = DICTIONARY;
    content.entry = entry;
    HttpRequest request =
        requestFactory.buildPostRequest(new GenericUrl(feed.getPostLink()), content);
    return request.execute().parseAs(entry.getClass());
  }

    @Throws(classOf[IOException])
  public AlbumFeed executeGetAlbumFeed(PicasaUrl url) {
    url.kinds = "photo";
    url.maxResults = 5;
    return executeGetFeed(url, AlbumFeed.class);
  }

    @Throws(classOf[IOException])
  public UserFeed executeGetUserFeed(PicasaUrl url) {
    url.kinds = "album";
    url.maxResults = 3;
    return executeGetFeed(url, UserFeed.class);
  }

    @Throws(classOf[IOException])
  public AlbumEntry insertAlbum(UserFeed userFeed, AlbumEntry entry) {
    return (AlbumEntry) executeInsert(userFeed, entry);
  }

    @Throws(classOf[IOException])
    def executeInsertPhotoEntry(
      String albumFeedLink, InputStreamContent content, String fileName): PhotoEntry = {
        val request = requestFactory.buildPostRequest(new GenericUrl(albumFeedLink), content)
        GoogleHeaders headers = (GoogleHeaders) request.headers;
    headers.setSlugFromFileName(fileName);
    return request.execute().parseAs(PhotoEntry.class);
  }

    @Throws(classOf[IOException])
    def executeInsertPhotoEntryWithMetadata(
        photo: PhotoEntry,
        albumFeedLink: String,
        content: AbstractInputStreamContent): PhotoEntry =
    {
        val request = requestFactory.buildPostRequest(new GenericUrl(albumFeedLink), null)
        val atomContent = new AtomContent()
        atomContent.namespaceDictionary = DICTIONARY
        atomContent.entry = photo
        val multiPartContent = MultipartRelatedContent.forRequest(request)
        multiPartContent.parts.add(atomContent)
        multiPartContent.parts.add(content)
        request.content = multiPartContent
        return request.execute().parseAs(PhotoEntry.class)
  }
}



final class PicasaConnector extends Connector("picasa") {

    def connect() = new Picasa(this)
}
