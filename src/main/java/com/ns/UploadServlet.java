package com.ns;

import com.google.cloud.storage.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// [START example]
@SuppressWarnings("serial")
@WebServlet(name = "upload", value = "/upload")
@MultipartConfig()
public class UploadServlet extends HttpServlet {

    private static final String BUCKET_NAME = "kuvrrvideo";
    private static Storage storage = null;

    @Override
    public void init() {
        storage = StorageOptions.getDefaultInstance().getService();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            ServletException {
        Part filePart = req.getPart("photo");
        String fileName = "qwerty555421.jpg";

        // Modify access list to allow all users with link to read file
        List<Acl> acls = new ArrayList();
        acls.add(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
        // the inputstream is closed by default, so we don't need to close it here
        Blob blob =
                storage.create(
                        BlobInfo.newBuilder(BUCKET_NAME, fileName).setAcl(acls).build(),
                        filePart.getInputStream());

        // return the public download link
        resp.getWriter().print(blob.getMediaLink());
    }
}