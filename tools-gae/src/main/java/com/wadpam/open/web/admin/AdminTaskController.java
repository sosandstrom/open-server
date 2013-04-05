package com.wadpam.open.web.admin;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TransientFailureException;
import com.wadpam.open.web.DomainNamespaceFilter;

/**
 * 
 * @author os
 */
@Controller
@RequestMapping("{domain}/_admin/task")
public class AdminTaskController {

    public static final String PATH_ADMIN_TASK = "/api/%s/_admin/task/%s";

    static final Logger        LOG             = LoggerFactory.getLogger(AdminTaskController.class);

    private AdminTask          adminTaskBean;

    @RequestMapping(value = "{taskName}", method = RequestMethod.GET)
    public ResponseEntity<Object> enqueueTask(final HttpServletRequest request, @PathVariable String domain,
            @PathVariable String taskName) {
        final Queue queue = QueueFactory.getDefaultQueue();
        final TaskOptions options = TaskOptions.Builder.withUrl(String.format(PATH_ADMIN_TASK, domain, taskName));
        Map<String, String[]> paramMap = request.getParameterMap();
        for(Entry<String, String[]> param : paramMap.entrySet()) {
            for(String value : param.getValue()) {
                options.param(param.getKey(), value);
            }
        }

        try {
            queue.add(options);
        }
        catch (TransientFailureException tfe) {
            LOG.error("Run admin task fail {} ", tfe.getMessage());
        }

        LOG.info("Added queue task for {}", taskName);

        return new ResponseEntity<Object>(HttpStatus.OK);
    }

    @RequestMapping(value = "{taskName}", method = RequestMethod.POST)
    public ResponseEntity<Object> processTask(@PathVariable String domain, @PathVariable final String taskName,
            final HttpServletRequest request) throws IOException, ServletException {

        LOG.info("Processing task for {}...", taskName);
        DomainNamespaceFilter.doInNamespace(domain, new Runnable() {

            @SuppressWarnings("unchecked")
            @Override
            public void run() {
                final Object body = adminTaskBean.processTask(taskName, (Map<String, String[]>) request.getParameterMap());
                LOG.info("Processed task for {}: {}", taskName, body);
            }
        });

        return new ResponseEntity<Object>(HttpStatus.OK);
    }

    public void setAdminTaskBean(AdminTask adminTaskBean) {
        this.adminTaskBean = adminTaskBean;
    }
}

