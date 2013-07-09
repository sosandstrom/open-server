/***/
package com.wadpam.open.push.service;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TransientFailureException;
import com.wadpam.open.push.dao.DuSubscriptionDao;
import com.wadpam.open.push.domain.DuSubscription;
import com.wadpam.open.push.json.JSubscription;
import com.wadpam.open.service.EmailSender;
import com.wadpam.open.web.BaseConverter;

/**
 * @author sophea <a href='mailto:sm@goldengekko.com'> sophea </a>
 * @version $id$ - $Revision$
 * @date 2013
 */
public class PushService {

    public static final String                       PUSH_URBAN                  = "urban";
    public static final String                       PUSH_EMAIL                  = "email";
    public static final String                       PUSH_XMPP                   = "XMPP";
    public static final String                       PUSH_SMS                    = "SMS";
    public static final String                       PUSH_JUNIT                  = "jUnit";
    
    private static final String URBAN_QUEUE_NAME = "urban-queue";
    private static final String PATH_URBAN_QUEUE = "/api/%s/push/v10/_queue/push";
    private static final String EMAIL_QUEUE_NAME = "email-queue";
    private static final String PATH_EMAIL_QUEUE = "/api/%s/push/v10/_queue/push";
    public static final int                          SHORT_TIME                  = 2000;
    public static final int                          LONG_TIME                   = 5000;
   

    /** Device types: IOS = 0; ANDROID = 1; */
    public static final Long                         DEVICE_TYPE_IOS             = 0L;
    public static final Long                         DEVICE_TYPE_ANDROID         = 1L;
    public static final Long                         DEVICE_TYPE_IOS_ANDROID         = 2L;
    
    public static final int                         MAX_RECORD_TOKENS         =50;
    public static final int                         MAX_RECORD_EMAILS         =50;
    
    static final Logger        LOG        = LoggerFactory.getLogger(PushService.class);
    
    private DuSubscriptionDao                        subscriptionDao;
    private PushNotificationService pushNotificationService;
    
    private String                                   emailFromAddress;
    private String                                   emailFromName;
    /**
     * subscribe
     * 
     * @param jSubscription
     * @return JSubscription
     */
    public JSubscription subscribe(JSubscription jSubscription) {
        final DuSubscription body = convert(jSubscription);
        if (!updateTokens(jSubscription.getDeviceId(), body)) {
            subscriptionDao.persist(body);
        }

        //submit to notification provider
        if (null != getPushNotificationService()) {
            try {
                pushNotificationService.register(jSubscription.getDeviceToken(),jSubscription.getTag());
            } catch (IOException e) {
                LOG.error(e.getMessage(),e);
            }
        } 
        return jSubscription;
    }
    
   
    public boolean updateTokens(String deviceId, DuSubscription from) {

        LOG.debug(" update device id : {}", deviceId);
        Iterable<DuSubscription> dSubscribers = subscriptionDao.queryByDeviceId(deviceId);
        
        for (DuSubscription to : dSubscribers) {
            
            /**update*/
            to.setDeviceToken(from.getDeviceToken());
            to.setUserId(from.getUserId());
            to.setDeviceId(from.getDeviceId());
            to.setDeviceToken(from.getDeviceToken());
            to.setKey(from.getKey());
            to.setPushType(from.getPushType());
            to.setEndDate(from.getEndDate());
            to.setStartDate(from.getStartDate());
            to.setTag(from.getTag());
            to.setDeviceType(from.getDeviceType());
            subscriptionDao.update(to);
        }
        return dSubscribers !=null && dSubscribers.iterator().hasNext();
     }

    /**
     * unsubscribe
     * 
     * @param jSubscription
     * @return JSubscription
     */
    public String unsubscribe(String deviceToken) {

        final Iterable<DuSubscription> body = subscriptionDao.queryByDeviceToken(deviceToken);
        if (!body.iterator().hasNext()) {
            return null;
        }
        for (DuSubscription duSubscription : body) {
            subscriptionDao.delete(duSubscription);
        }
        
      //submit to notification provider
        if (null != getPushNotificationService()) {
            try {
                pushNotificationService.unregister(deviceToken);
            } catch (IOException e) {
                LOG.warn("Cannot be unsubscribe");
            }
        }

        return deviceToken;
    }
    
    public void pushTags(String message, String... tags) throws Exception {
        if (null != getPushNotificationService()) {
            pushNotificationService.pushTags(message, tags);
        }
    }
    public void pushTagsForAndroid(String domain,String message, String... tags)  {
       /* LOG.debug(" sending to android ================");
        for (String tag : tags) {
            Iterable<DuSubscription> items = subscriptionDao.queryByTag(tag);
            List<String> tokens = new ArrayList<String>();
            for (DuSubscription duSubscription : items) {
                tokens.add(duSubscription.getDeviceToken());
            }
            enqueueUrbanPush(domain,PUSH_URBAN,DEVICE_TYPE_ANDROID,message, tokens);
        }*/
    }
    public void pushTokens(String domain, String pushType, long deviceType, String message,String subject, List<String> tokens) throws Exception {
       
        if (PushService.PUSH_URBAN.equals(pushType)) {
            enqueueUrbanPush(domain, pushType, deviceType ,message, tokens);
        } else if ( PushService.PUSH_EMAIL.equals(pushType)) {
            enqueueEmailPush(domain, pushType, message, subject, tokens);
        } else if ( PushService.PUSH_SMS.equals(pushType)) {
            LOG.info("push sms is not implemented");
        }
    }
    
    /**
     * Enqueues the email for sending process
     * @param message body of the email
     * @param subject subject of the email
     * @param receivers list of receivers
     * @param domain path of the domain 
     */
    public void enqueueEmailPush(String domain, String pushType, String message, String subject, List<String> tokens) {
        Queue queue = QueueFactory.getQueue(EMAIL_QUEUE_NAME);
        TaskOptions options = TaskOptions.Builder.withUrl(String.format(PATH_EMAIL_QUEUE,domain));
        LOG.info("{} email(s) will be enqueue for sending.", tokens.size());
        List<String> sendTokens =null;
        
        if (tokens.size() <=MAX_RECORD_TOKENS) {
            sendTokens =tokens;
        } else {
            sendTokens =tokens.subList(0, MAX_RECORD_TOKENS-1);
            options.param("pustType", pushType);
            options.param("message", message);
            options.param("subject", subject);
            for(int i=MAX_RECORD_TOKENS; i<tokens.size();i++) {
                options.param("tokens[]", tokens.get(i));
            }
            //enqueue
            if (addOptionsToQueueWithRetry(queue, options)) {
                LOG.debug("Enqueued push notification updates");
            }
        }
        
        sendEmail(sendTokens,subject,message);
    }

    
    /**
     * Enqueue the push notification for sending process
     * @param message message to be sent to the subscribers
     * @param receivers list of subscribers
     * @throws IOException exception
     */
    public void enqueueUrbanPush(String domain, String pushType, long deviceType, String message,List<String> tokens) {
        // obtaining the queue
        Queue urbanQueue = QueueFactory.getQueue(URBAN_QUEUE_NAME);
        TaskOptions iOSOptions = TaskOptions.Builder.withUrl(String.format(PATH_URBAN_QUEUE, domain));
        LOG.debug("{} push(es) will be enqueue for sending.", tokens.size());
        
        String []arrayTokens =null;
        if (tokens.size() <=MAX_RECORD_TOKENS) {
            arrayTokens =(String[])tokens.toArray();
        } else {
            arrayTokens =(String[])tokens.subList(0, MAX_RECORD_TOKENS-1).toArray();
            iOSOptions.param("deviceType", String.valueOf(deviceType));
            iOSOptions.param("pustType", pushType);
            iOSOptions.param("message", message);
            
            for(int i=MAX_RECORD_TOKENS; i<tokens.size();i++) {
                iOSOptions.param("tokens[]", tokens.get(i));
            }
            if (addOptionsToQueueWithRetry(urbanQueue, iOSOptions)) {
                LOG.debug("Enqueued push notification updates");
            }
        }
        
        if (null != getPushNotificationService()) {
            
            try {
                pushNotificationService.push(message, arrayTokens);
            } catch (IOException e) {
                LOG.error(e.getMessage(),e);
            }
        }
    }
    
    public static boolean addOptionsToQueueWithRetry(Queue queue, TaskOptions options) {
        boolean successful = true;
        try {
            queue.add(options);
        } catch (TransientFailureException tfe) {
            try {
                try {
                    Thread.sleep(SHORT_TIME);
                } catch (InterruptedException e) {
                    // Leave it at this
                }
                LOG.warn("Failed to add task to queue. This is first retry");
                queue.add(options);
            } catch (TransientFailureException tfe2) {
                try {
                    try {
                        Thread.sleep(LONG_TIME);
                    } catch (InterruptedException e) {
                        // Leave it at this
                    }
                    LOG.warn("Failed to add task to queue. This is second retry");
                    queue.add(options);
                } catch (TransientFailureException tfe3) {
                    successful = false;
                    LOG.error("Failed to add task to queue with exception {} ", tfe3);
                }
            }
        } catch (IllegalStateException e) {
            LOG.info("Queue, {}, is not found in the application configuration. Default queue is being used.",
                    queue.getQueueName());
            queue = QueueFactory.getDefaultQueue();
            addOptionsToQueueWithRetry(queue, options);
        }
        return successful;
    }
    
    public static JSubscription convert(DuSubscription from) {
        if (null == from) {
            return null;
        }

        final JSubscription to = new JSubscription();
        BaseConverter.convert(from, to);

        // other properties
        to.setUserId(from.getUserId());
        to.setDeviceId(from.getDeviceId());
        to.setDeviceToken(from.getDeviceToken());
        
        to.setKey(from.getKey());
        to.setPushType(from.getPushType());
        to.setEndDate(BaseConverter.toLong(from.getEndDate()));
        to.setStartDate(BaseConverter.toLong(from.getStartDate()));
        to.setTag(from.getTag());
        to.setDeviceType(from.getDeviceType());
        return to;
    }

    public static DuSubscription convert(JSubscription from) {
        if (null == from) {
            return null;
        }

        final DuSubscription to = new DuSubscription();
        BaseConverter.convert(from, to);

        // other properties
        to.setUserId(from.getUserId());
        to.setDeviceId(from.getDeviceId());
        to.setDeviceToken(from.getDeviceToken());
        to.setEndDate(BaseConverter.toDate(from.getEndDate()));
        to.setKey(from.getKey());
        to.setPushType(from.getPushType());
        to.setStartDate(BaseConverter.toDate(from.getStartDate()));
        to.setTag(from.getTag());
        to.setDeviceType(from.getDeviceType());
        return to;
    }


    /**
     * A basic send email method that only support plain text and to/from email address.
     * @param toEmail the to email address
     * @param fromEmail the from email address.
     *                  Please note the restrictions on GAE when setting the from email.
     *                  https://developers.google.com/appengine/docs/java/mail/usingjavamail#Senders_and_Recipients
     * @param subject the subject/title
     * @param body text body
     * @return true if send was successful
     */
    public boolean sendEmail(List<String> toEmail,  String subject, String body) {
        return EmailSender.sendEmail(emailFromName, emailFromAddress, toEmail, null, null, subject, null, body, null, null, null);
    }
    
    public void setSubscriptionDao(DuSubscriptionDao subscriptionDao) {
        this.subscriptionDao = subscriptionDao;
    }


    public void setPushNotificationService(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }
    
    public PushNotificationService  getPushNotificationService() {
        if (null == pushNotificationService) {
            LOG.warn("*****************No push notifiction service setup*********************");
        }
        return pushNotificationService;
    }


    public void setEmailFromAddress(String emailFromAddress) {
        this.emailFromAddress = emailFromAddress;
    }


    public void setEmailFromName(String emailFromName) {
        this.emailFromName = emailFromName;
    }
}
