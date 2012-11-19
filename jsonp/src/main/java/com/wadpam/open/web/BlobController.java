package com.wadpam.open.web;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.wadpam.docrest.domain.RestCode;
import com.wadpam.docrest.domain.RestReturn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mange blobs in GAE blobstore.
 * @author mattiaslevin
 */

@Controller
@RequestMapping(value="{domain}/blob")
public class BlobController extends AbstractRestController {
    private static final Logger LOG = LoggerFactory.getLogger(CommonController.class);

    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();


    /**
     * Get an upload url to blobstore.
     * @return a blobstore upload url
     */
    @RestReturn(value=Map.class, entity=Map.class, code={
            @RestCode(code=200, message="OK", description="Upload url created"),
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

    /**
     * Upload file to blobstore callback.
     * @return the download url for each of the uploaded files
     */
    @RestReturn(value=Map.class, entity=Map.class, code={
            @RestCode(code=200, message="OK", description="File uploaded"),
    })
    @RequestMapping(value="upload", method= RequestMethod.POST)
    @ResponseBody
    public Map<String, String> uploadCallback(HttpServletRequest request,
                                              @PathVariable String domain,
                                              UriComponentsBuilder uriBuilder) {
        LOG.debug("Blobstore upload callback");

        Map<String, List<BlobKey>> blobKeys = blobstoreService.getUploads(request);

        uriBuilder.path("/{domain}/blob").query("key={blobKey}");
        Map<String, String> response = new HashMap<String, String>();
        for (Map.Entry<String, List<BlobKey>> entry : blobKeys.entrySet()) {
            response.put(entry.getKey(),
                    uriBuilder.buildAndExpand(domain, entry.getValue().get(0).getKeyString()).toUriString());
        }

        return response;
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
        this.blobstoreService.delete(blobKey);
    }

}
