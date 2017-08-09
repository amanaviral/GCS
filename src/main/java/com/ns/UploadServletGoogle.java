package com.ns;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

// [START example]
@SuppressWarnings("serial")
@WebServlet(name = "upload1", value = "/upload1")
@MultipartConfig()
public class UploadServletGoogle  extends HttpServlet{

    private static Storage storage = null;

    // [START init]
    static {
        storage = StorageOptions.getDefaultInstance().getService();
    }
    // [END init]

    // [START uploadFile]

    /**
     * Uploads a file to Google Cloud Storage to the bucket specified in the BUCKET_NAME
     * environment variable, appending a timestamp to end of the uploaded filename.
     */
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("-YYYY-MM-dd-HHmmssSSS");
        DateTime dt = DateTime.now(DateTimeZone.UTC);
        String dtString = dt.toString(dtf);
        Part filePart = req.getPart("photo");
        final String fileName = filePart.getSubmittedFileName() + dtString;

        // the inputstream is closed by default, so we don't need to close it here
        BlobInfo blobInfo =
                storage.create(
                        BlobInfo
                                .newBuilder("kuvrrvideo", fileName)
                                // Modify access list to allow all users with link to read file
                                .setAcl(new ArrayList(Arrays.asList(Acl.of(User.ofAllUsers(), Role.READER))))
                                .build(),
                        filePart.getInputStream());
        // return the public download link
        resp.getWriter().print(blobInfo.getMediaLink());
    }
    // [END uploadFile]

    // [START getImageUrl]

    /**
     * Extracts the file payload from an HttpServletRequest, checks that the file extension
     * is supported and uploads the file to Google Cloud Storage.
     */
    /*public String getImageUrl(HttpServletRequest req, HttpServletResponse resp,
                              final String bucket) throws IOException, ServletException {
        Part filePart = req.getPart("file");
        final String fileName = filePart.getSubmittedFileName();
        String imageUrl = req.getParameter("imageUrl");
        // Check extension of file
        if (fileName != null && fileName.contains(".")) {
            final String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
            String[] allowedExt = {"jpg", "jpeg", "png", "gif"};
            for (String s : allowedExt) {
                if (extension.equals(s)) {
                    return this.uploadFile(filePart, bucket);
                }
            }
            throw new ServletException("file must be an image");
        }
        return imageUrl;
    }*/
    // [END getImageUrl]
}
// [END example]

