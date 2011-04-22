package org.podval.zenfolio;

import org.podval.photo.{NonRootAlbum, PhotoException}

import com.zenfolio.www.api._1_1.{PhotoSet, Photo => ZPhoto}

import java.rmi.RemoteException

import org.apache.commons.httpclient.{HttpClient, HttpStatus}
import org.apache.commons.httpclient.methods.{PostMethod, RequestEntity}
import org.apache.commons.httpclient.methods.multipart.{MultipartRequestEntity, Part, FilePart, StringPart}

import java.util.Date

import java.io.{File, IOException}


/* package */ final class Gallery(parentArg: ZenfolioFolder, element: PhotoSet) extends ZenfolioFolder[PhotoSet](element) with NonRootAlbum {

    protected val parent = parentArg


    protected override def retrievePhotos(): Seq[P] = { // throws PhotoException {
        // PhotoSet needs to be loaded, since in the "structure" it is not populated with the Photos.

        val id = element.getId()

        if (id != 0) {
            try {
                element = getConnection().getConnection().loadPhotoSet(id)
            } catch {
                case e: RemoteException => throw new PhotoException(e)
            }

            if ((element.getPhotos() != null) && (getElement().getPhotos().getPhoto() != null)) {
                for (rawPhoto: ZPhoto <- element.getPhotos().getPhoto())
                    yield new ZenfolioPhoto(this, rawPhoto)
            }
        }
    }


//    @Override
//    protected void checkFolderType(final FolderType folderType) {
//        // will not be called - checked in the base class
//    }
//
//
//    @Override
//    protected Gallery doCreateFolder(
//        final String name,
//        final FolderType folderType) throws PhotoException
//    {
//        // will not be called - checked in the base class
//        throw new UnsupportedOperationException("Gallery can not have folders");
//    }
//
//
//    @Override
//    protected Gallery doCreateFakeFolder(
//        final String name,
//        final FolderType folderType) throws PhotoException
//    {
//        // will not be called - checked in the base class
//        throw new UnsupportedOperationException("Gallery can not have folders");
//    }
//
//
//    @Override
//    public void doAddFile(final String name, final File file) throws PhotoException {
//        try {
//            final String message = postFile(name, file);
//            if (message != null) {
//                throw new PhotoException(message);
//            }
//        } catch (final IOException e) {
//            throw new PhotoException(e);
//        }
//    }
//
//
//    private String postFile(final String name, final File file) throws IOException {
//        final String url = "http://www.zenfolio.com" + getElement().getUploadUrl();
//
//        final PostMethod filePost = new PostMethod(url);
//
//        filePost.setRequestHeader(getConnection().getAuthTokenHeader());
//
//        final String date = new Date(file.lastModified()).toString();
//
//        final RequestEntity entity =
////        makeSimplifiedPost
//        makeMultiPartPost
//            (filePost, name, file, date);
//
//        filePost.setRequestEntity(entity);
//
//        final HttpClient client = new HttpClient();
//        final int status = client.executeMethod(filePost);
//
//        filePost.releaseConnection();
//
//        return (status == HttpStatus.SC_OK) ? null : HttpStatus.getStatusText(status);
//    }
//
//
//    private RequestEntity makeMultiPartPost(
//        final PostMethod filePost,
//        final String name,
//        final File file,
//        final String date) throws IOException
//    {
////        filePost.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true);
//
//        final Part[] parts = {
//            new FilePart("file", name, file, "image/jpeg", "UTF-8"),
//            new StringPart("file_modified", date)
//        };
//
//        return new MultipartRequestEntity(parts, filePost.getParams());
//    }
//
//
////    private RequestEntity makeSimplifiedPost(
////        final PostMethod filePost,
////        final String name,
////        final File file,
////        final String date)
////    {
////        final NameValuePair[] queryParameters = new NameValuePair[2];
////        queryParameters[0] = new NameValuePair("filename", name);
////        queryParameters[1] = new NameValuePair("modified", date);
////
////        filePost.setQueryString(queryParameters);
////
////        return new FileRequestEntity(file, "image/jpeg");
////    }
//
//
//    @Override
//    public void updateIfChanged() throws PhotoException {
//        // TODO
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
}
