package com.wadpam.open.task;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * A ping controller for that create a GAE task every X ms and send to it self.
 * This is useful to ensure that GAE instances are always running and not powered
 * down.
 * @author os
 */
@Controller
@RequestMapping("_admin/{domain}/ping")
public class PingController {
    /** Will only schedule a next PING if current time is beyond this timestamp */
    public static final String NEXT_TIMESTAMP = "_next_timestamp";
    
    final MemcacheService MEMCACHE_SERVICE = MemcacheServiceFactory.getMemcacheService(null);
    
    @RequestMapping(value="v10", method= RequestMethod.GET)
    @ResponseBody
    public String setFrequency(
            HttpServletRequest request,
            @PathVariable String domain,
            @RequestParam(defaultValue="60000") Long interval
            ) {
        final String uri = request.getRequestURI();
        
        // create the task
        final long currentMillis = System.currentTimeMillis();
        final String token = Long.toString(currentMillis);
        scheduleTask(uri, currentMillis, interval, token);
        
        // set the timestamp for this task
        MEMCACHE_SERVICE.put(uri, token);
        
        return token;
    }
    
    @RequestMapping(value="v10", method= RequestMethod.POST)
    @ResponseBody
    public String onPing(
            HttpServletRequest request,
            @PathVariable String domain,
            @RequestParam Long interval,
            @RequestParam String token
            ) {
        final String uri = request.getRequestURI();
        final String cachedToken = (String) MEMCACHE_SERVICE.get(uri);
        
        // re-queue the task ?
        if ((null == cachedToken || cachedToken.equals(token)) && 0 < interval) {
            if (null == cachedToken) {
                MEMCACHE_SERVICE.put(uri, token);
            }
            scheduleTask(uri, System.currentTimeMillis(), interval, token);
        }
        
        return cachedToken;
    }

    protected void scheduleTask(final String uri, final long currentMillis, Long interval, final String token) {
        // Will only schedule a next PING if current time is beyond this timestamp
        final Long minNextTime = (Long) MEMCACHE_SERVICE.get(NEXT_TIMESTAMP);
        if (null == minNextTime || minNextTime <= currentMillis) {
            MEMCACHE_SERVICE.put(NEXT_TIMESTAMP, currentMillis + interval);
            final TaskOptions options = TaskOptions.Builder
                    .withUrl(uri)
                    .etaMillis(currentMillis + interval)
                    .param("interval", interval.toString())
                    .param("token", token);

            final Queue queue = QueueFactory.getDefaultQueue();
            queue.add(options);
        }
    }
    
}
