package com.wadpam.open.analytics;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.wadpam.open.analytics.google.Device;
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
public class TaskEventDispatcher implements EventDispatcher {
    static final Logger LOG = LoggerFactory.getLogger(TaskEventDispatcher.class);

    // The worker url that will run the task
    private String workerUrl = "api/%s/analytics/task";
    private String domain = "changeMe";


    // Dispatch the event using GAE task queue
    @Override
    public boolean dispatch(URI analyticsUri, final String userAgent, final String remoteAddress) {
        LOG.debug("Dispatch the event using GAE task queue");

        // URL encode the uri
        try {
            Queue queue = QueueFactory.getDefaultQueue();

            // We need to pass over a few parameters
            queue.add(TaskOptions.Builder.withUrl(String.format(workerUrl, domain))
                    .param("url", URLEncoder.encode(analyticsUri.toURL().toString(), "UTF-8"))
                    .param("userAgent", URLEncoder.encode(userAgent, "UTF-8"))
                    .param("remoteAddress", URLEncoder.encode(remoteAddress, "UTF-8")));

        } catch (UnsupportedEncodingException e) {
            LOG.error("Not possible to encode url with reason:{}", e.getMessage());
            throw new IllegalArgumentException(e);
        } catch (MalformedURLException e) {
            LOG.error("Malformed url with reason:{}", e.getMessage());
            throw new IllegalArgumentException(e);
        }

        return true;
    }


    // Getters and setters
    public String getWorkerUrl() {
        return workerUrl;
    }

    public void setWorkerUrl(String workerUrl) {
        this.workerUrl = workerUrl;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
