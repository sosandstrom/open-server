package com.wadpam.open.web;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.wadpam.docrest.domain.RestCode;
import com.wadpam.docrest.domain.RestReturn;
import java.io.IOException;
import java.util.ArrayList;
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

    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    private final ImagesService imagesService = ImagesServiceFactory.getImagesService();

    /**
     * Get an upload url to blobstore.
     * @return a blobstore upload url
     */
    @RestReturn(value=Map.class, entity=Map.class, code={
            @RestCode(code=200, message="OK", description="Upload url created")
    })
    @RequestMapping(value="upload", method= RequestMethod.GET)
    @ResponseBody
    public Map<String, String> getUploadUrl(HttpServletRequest request) {
        LOG.debug("Get blobstore upload url");

        String callbackUrl = request.getRequestURI();

        Map<String, String> response = new HashMap<String, String>();
        response.put("url", blobstoreService.createUploadUrl(callbackUrl));

        return response;
    }

    @RequestMapping(value = "v10", method = RequestMethod.GET, params={"callbackPath"})
    @ResponseBody
    public Map<String, String> getUploadUrl(HttpServletRequest request, 
            @PathVariable String domain,
            @RequestParam String callbackPath) {

        // include an upload URL
        String callbackUrl = 
                null != callbackPath ? callbackPath : String.format("/api/%s/blob/v10", domain);
        
        // if the callback URL should contain query parameters, e.g. access_token,
        // set callbackPath to "#"
        if ("#".equals(callbackPath)) {
            final String queryString = request.getQueryString();
            callbackUrl = String.format("%s?%s", 
                    request.getRequestURI(), 
                    null != queryString ? queryString : "");
        }

        // get uploadUrl from blob service
        Map<String, String> response = new HashMap<String, String>();
        response.put("url", blobstoreService.createUploadUrl(callbackUrl));

        return response;
    }
    
    /**
     * Upload file to blobstore callback.
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

        final Map<String, List<String>> body = new TreeMap<String, List<String>>();
        Map<String, List<BlobInfo>> blobInfos = blobstoreService.getBlobInfos(request);
        String accessUrl;

        for (Entry<String, List<BlobInfo>> field : blobInfos.entrySet()) {
            
            ArrayList<String> urls = new ArrayList<String>();
            body.put(field.getKey(), urls);
            for (BlobInfo blobInfo : field.getValue()) {
                
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
                    accessUrl = String.format("%s://%s/api/%s/blob/v10?key=%s", 
                        request.getScheme(), request.getHeader("Host"), 
                        domain, blobKey.getKeyString());
                }
                urls.add(accessUrl);
            }
        }

        return body;
    }

    /**
     * Get a blob.
     * @param  key The blob store key
     * @return the blob
     */
    @RequestMapping(value="", method= RequestMethod.GET, params = "key")
    @ResponseBody
    public void getBlob(HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) String key) throws IOException {
        LOG.debug("Get blob with key:{}", key);

        BlobKey blobKey = new BlobKey(key);
        this.blobstoreService.serve(blobKey, response);
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
            @RequestParam(required = true) String key) throws IOException {
        LOG.debug("Delete blob with key:{}", key);

        BlobKey blobKey = new BlobKey(key);
        blobstoreService.delete(blobKey);
    }
}
