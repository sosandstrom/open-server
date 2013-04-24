package com.wadpam.open.web;

import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.wadpam.docrest.domain.RestCode;
import com.wadpam.docrest.domain.RestReturn;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.servlet.http.HttpServletResponse;

/**
 * Mange blobs in GAE blobstore.
 * @author mattiaslevin
 */

@Controller
@RequestMapping(value="{domain}/blob")
public class BlobController extends AbstractRestController {
    private static final Logger LOG = LoggerFactory.getLogger(BlobController.class);

    private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    private final ImagesService imagesService = ImagesServiceFactory.getImagesService();

    private final String HEADER_CACHE_CONTROL = "Cache-Control";
    private final String HEADER_CONTENT_DESPOSITION = "Content-Disposition";


    /**
     * Get an upload url to blobstore.
     * @param callbackPath Optional. The url that Blobstore should use as callback url.
     *                     Only set this values if you want a specific handled  method to run
     *                     after a file upload (when default behaviour is not enough)
     * @param request Optional. If set to true, the callback url will also contain the same
     *                query parameters as the this quests. Use this to forward parameters to
     *                the callback method, e.g. access_token.
     * @return a blobstore upload url
     */
    @RestReturn(value=Map.class, entity=Map.class, code={
            @RestCode(code=200, message="OK", description="Upload url created")
    })
    @RequestMapping(value="upload", method= RequestMethod.GET)
    @ResponseBody
    public Map<String, String> getUploadUrl(HttpServletRequest request,
                                            @RequestParam(required=false) String callbackPath,
                                            @RequestParam(defaultValue="false") Boolean retainParams) {
        LOG.debug("Get blobstore upload url");

        // Use callback path if provided
        String callbackUrl = null != callbackPath ? callbackPath : request.getRequestURI();

        // Forward any existing query parameters, e.g. access_token
        if (retainParams) {
            final String queryString = request.getQueryString();
            callbackUrl = String.format("%s?%s", callbackUrl, null != queryString ? queryString : "");
        }

        // Response
        Map<String, String> response = new HashMap<String, String>();
        response.put("uploadUrl", blobstoreService.createUploadUrl(callbackUrl));

        return response;
    }

    /**
     * Upload file to blobstore callback.
     * This method will be call by the Blonstore service after a successful file upload.
     * @return the download url for each of the uploaded files
     */
    @RestReturn(value=Map.class, entity=Map.class, code={
            @RestCode(code=200, message="OK", description="File uploaded")
    })
    @RequestMapping(value="upload", method= RequestMethod.POST)
    @ResponseBody
    public Map<String, List<String>> uploadCallback(HttpServletRequest request,
                                              @PathVariable String domain,
                                              UriComponentsBuilder uriBuilder) {
        LOG.debug("Blobstore upload callback");

        // Get all uploaded blob info records
        Map<String, List<BlobInfo>> blobInfos = blobstoreService.getBlobInfos(request);
        // Response body
        final Map<String, List<String>> body = new TreeMap<String, List<String>>();

        for (Entry<String, List<BlobInfo>> field : blobInfos.entrySet()) {
            // All urls for a specific upload
            ArrayList<String> urls = new ArrayList<String>();
            body.put(field.getKey(), urls);

            for (BlobInfo blobInfo : field.getValue()) {
                String accessUrl;
                final BlobKey blobKey = blobInfo.getBlobKey();

                final String contentType = blobInfo.getContentType();
                if (null != contentType && contentType.startsWith("image")) {
                    // we want to serve directly from ImagesService,
                    // to avoid involving the GAE app, avoid spinning up instances,
                    // and to use the awesome Google CDN.
                    ServingUrlOptions suo = ServingUrlOptions.Builder.withBlobKey(blobKey);
                    accessUrl = imagesService.getServingUrl(suo);
                }
                else {
                    // serve via this BlobController
                    accessUrl = String.format("%s://%s/api/%s/blob?key=%s", request.getScheme(), request.getHeader("Host"),
                            domain, blobKey.getKeyString().toString());
                }
                urls.add(accessUrl);
            }
        }

        return body;
    }



    /**
     * Get a blob.
     * @param key The blob store key
     * @param maxCacheAge Optional. Decides the value the Cache-Control header sent back end the response.
     *                    Default value is 1 day.
     *                    Set the 0 if set the cache directive to "no-cache" to avoid the http client
     *                    to do any caching.
     * @param asAttachment Optional. Set the Content-Disposition header to decide if the file
     *                     should be returned as an attachment. Default is false.
     * @return the blob
     */
    @RequestMapping(value="", method= RequestMethod.GET, params = "key")
    @ResponseBody
    public void getBlob(HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam String key,
            @RequestParam(defaultValue="86400") int maxCacheAge,
            @RequestParam(defaultValue ="false") boolean asAttachment) throws IOException {
        LOG.debug("Get blob with key:{}", key);

        // make sure iOS caches the image (default 1 day)
        if (maxCacheAge > 0) {
            response.setHeader(HEADER_CACHE_CONTROL, String.format("public, max-age=%d", maxCacheAge));
        } else {
            response.setHeader(HEADER_CACHE_CONTROL, "no-cache");
        }

        BlobKey blobKey = new BlobKey(key);
        BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
        //set response header
        response.setContentType(blobInfo.getContentType());

        if (asAttachment) {
            // Download the file as attachment
            response.setHeader(HEADER_CONTENT_DESPOSITION, String.format("filename=\"%s\"",
                    getEncodeFileName(request.getHeader("User-Agent"), blobInfo.getFilename())));
        }

        // serve blob
        blobstoreService.serve(blobKey, response);
    }


    // Encode header value for Content-Disposition
    public static String getEncodeFileName(String userAgent, String fileName) {
        String encodedFileName = fileName;
        try {
            if (userAgent.contains("MSIE") || userAgent.contains("Opera")) {
                encodedFileName = URLEncoder.encode(fileName, "UTF-8");
            } else {
                encodedFileName = "=?UTF-8?B?" + new String(Base64.encodeBase64(fileName.getBytes("UTF-8")), "UTF-8") + "?=";
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return encodedFileName;
    }

    /**
     * Delete a blob.
     * @param  key The blob store key
     * @return 200 if successful
     */
    @RequestMapping(value="", method= RequestMethod.DELETE, params = "key")
    @ResponseBody
    public void deleteBlob(HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam String key) throws IOException {
        LOG.debug("Delete blob with key:{}", key);

        BlobKey blobKey = new BlobKey(key);
        blobstoreService.delete(blobKey);
    }
}
