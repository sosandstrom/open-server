package com.wadpam.open.analytics;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.wadpam.open.analytics.google.EventDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLEncoder;

/**
 * An event dispatcher using GAE tasks.
 * @author mattiaslevin
 */
public class TaskEventDispatcher extends EventDispatcher {
    static final Logger LOG = LoggerFactory.getLogger(TaskEventDispatcher.class);


    // Constructor
    public TaskEventDispatcher(String host, String remoteAddress, String userAgent) {
        super(host, remoteAddress, userAgent);
    }


    // Dispatch the event using GAE task queue
    public boolean dispatch(URI uri) {
        LOG.debug("Dispatch the event using GAE task queue");

        // URL encode the uri
        try {
            String encodedURL = URLEncoder.encode(uri.toURL().toString(), "UTF-8");
            Queue queue = QueueFactory.getDefaultQueue();

            // TODO Incomplete implementation
            String workerUrl = String.format("/api/_admin/%s/user/export", "brand");

            queue.add(TaskOptions.Builder.withUrl(workerUrl).param("url", encodedURL));
        } catch (UnsupportedEncodingException e) {
            LOG.error("Not possible to encode url with reason:{}", e.getMessage());
            throw new IllegalArgumentException(e);
        } catch (MalformedURLException e) {
            LOG.error("Malformed url with reason:{}", e.getMessage());
            throw new IllegalArgumentException(e);
        }

        return true;
    }
}
