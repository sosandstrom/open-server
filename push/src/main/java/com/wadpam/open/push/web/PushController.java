/***/
package com.wadpam.open.push.web;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.wadpam.docrest.domain.RestCode;
import com.wadpam.docrest.domain.RestReturn;
import com.wadpam.open.push.json.JSubscription;
import com.wadpam.open.push.service.PushService;

/**
 * @author sophea <a href='mailto:sm@goldengekko.com'> sophea </a>
 * @version $id$ - $Revision$
 * @date 2013
 */
@Controller
@RequestMapping(value = "{domain}/push")
@RestReturn(value = JSubscription.class)
public class PushController {

    static final Logger         LOG                     = LoggerFactory.getLogger(PushController.class);

    private PushService   pushService;

    /**
     * <xmp>
     * 
     * This method is used for push notification subscription (register token)
     * jSubscription elements belows :
     * 
     * -deviceId - string required it is deviceId for each device (hardware IMEI) 
     * -deviceToken - string required This is the end point of the notification message: - 
     *          email   - this will be the email address
     *          iOS     - this will be the deviceToken 
     *          Android - this will be the C2DMToken
     *          SMS     - this will be the phone number
     *           
     * -pushType string - it  is the type of push notification {urban, email, sms } by default urban
     *  
     * -deviceType long - it is the type of device in case of iOS and Android. (required) for urban pushType. The values are: 0 : iOS (default value), and 1 : Android.
     * 
     * -tag  string - it is the key which is used for grouping the subscribers. for telenor it is countryId value
     * 
     * -userId is the id of user (optional). This is not applicable for those application without login
     * </xmp>
     * 
     * @param jSubscription see JSubscription object
     * @return a response entity with status indicating whether the request is successful performed or not.
     */
    @RestReturn(value = JSubscription.class, entity = JSubscription.class, code = {
            @RestCode(code = 200, description = "On successful subscription", message = "OK"),
            @RestCode(code = 401, description = "On invalid provider token", message = "Unauthorized"),
            @RestCode(code = 409, description = "Already exists", message = "Conflict")})
    @RequestMapping(value = "v10/register", method = RequestMethod.POST,params={"deviceToken","deviceId"})
    public ResponseEntity register(HttpServletRequest request, @ModelAttribute JSubscription jSubscription,
            @RequestParam(defaultValue = PushService.PUSH_URBAN) String pushType,
            @RequestParam(defaultValue = "0") Long deviceType) {
        jSubscription.setDeviceType(deviceType);
        jSubscription.setPushType(pushType);
        final JSubscription body = pushService.subscribe(jSubscription);
        if (body == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * <xmp>
     * This method is used for push notification un-subscription
     * jSubscription elements belows :
     * 
     * -deviceToken This is the end point of the notification message: 
     *          email   - this will be the email address
     *          iOS     - this will be the deviceToken
     *          Android - this will be the C2DMToken
     *          SMS     - this will be the phone number
     * </xmp>
     * @return a response entity with status indicating whether the request is successful performed or not.
     */
    @RestReturn(value = JSubscription.class, entity = JSubscription.class, code = {
            @RestCode(code = 200, description = "On successful unsubscription", message = "OK"),
            @RestCode(code = 404, description = "On invalid provider token", message = "not found"),
            @RestCode(code = 500, description = "Internal server error", message = " internal server error")})
    @RequestMapping(value = "v10/unregister", method = RequestMethod.DELETE)
    public ResponseEntity unsubscribe(HttpServletRequest request, @RequestParam String deviceToken) {

        final String body = pushService.unsubscribe(deviceToken);
        if (body == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    public void setPushService(PushService pushService) {
        this.pushService = pushService;
    }
    /**
     * Sending push notification to subscribers (mobile devices, email addresses, or Short messages (SMS)) which is associated to the tag.
     * @param message message to be sent to the client
     * @param tag is used for grouping receivers arrays of string.
     * @param pushType - string - it  is the type of push notification {urban, email, sms } by default urban
     * @param deviceType is the type of device in case of iOS and Android. (required) for 'urban' pushType. The values are: 0 (default value): iOS, and 1 : Android , 2 both.
     * @return a response entity with status
     * @throws Exception 
     */
    @RestReturn(value = JSubscription.class, entity = JSubscription.class, code = {
            @RestCode(code = 200, description = "On successful push", message = "OK"),
            @RestCode(code = 401, description = "On invalid provider token", message = "Unauthorized")})
    @RequestMapping(value = "v10/push", method = RequestMethod.POST, params = {"tag[]"})
    public ResponseEntity pushTag(HttpServletRequest request, 
            @PathVariable String domain,
            @RequestParam String message, 
            @RequestParam (value = "tag[]")String tag[], 
            @RequestParam (defaultValue=PushService.PUSH_URBAN)String pushType,
            @RequestParam(defaultValue ="0") long deviceType) throws Exception {
        
        if (PushService.DEVICE_TYPE_IOS == deviceType ) {
            pushService.pushTags(message, tag);
        } else if (PushService.DEVICE_TYPE_ANDROID == deviceType) {
            //pushService.pushTagsForAndroid(domain, message, tag);
            LOG.debug("====== Push Android will be available soon ================");
        }
        
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     *  Sending push notification to subscribers (tokens,email, or sms ...) base on you set pustType.
     * 
     * @param message - message to be sent to the client for urban ,sms, email content for email ,
     * @param subject -mandatory  it is subject for sending as email type, if pushType is email this subject must be set
     * @param tokens mandatory fields list of deviceToken which the message is sent to. tokens can be device_tokens for urban, emails for sending email, phone-number for sms,... 
     * @param pushType string - it  is the type of push notification {urban, email, sms } by default urban
     * @param deviceType is the type of device in case of iOS and Android. (required) for 'urban' pushType. The values are: 0 (default value): iOS, and 1 : Android.
     * @return a response entity with status
     * @throws Exception 
     */
    @RestReturn(value = JSubscription.class, entity = JSubscription.class, code = {
            @RestCode(code = 200, description = "On successful push", message = "OK"),
            @RestCode(code = 422, description = "Unprocessable Entity.", message = "Unprocessable Entity."),
            @RestCode(code = 401, description = "On invalid provider token", message = "Unauthorized")})
    @RequestMapping(value = "v10/push", method = RequestMethod.POST, params= {"tokens[]","message"})
    public ResponseEntity pushMessages(HttpServletRequest request, 
            @PathVariable String domain,
            @RequestParam String message,
            @RequestParam (required = false) String subject,
            @RequestParam(value = "tokens[]", required = false) String tokens[],
            @RequestParam(defaultValue = PushService.PUSH_URBAN) String pushType,
            @RequestParam(value = "deviceType", defaultValue="0") long deviceType) throws Exception {
        
        LOG.debug(" push message with type {} ", pushType);
        
        pushService.pushTokens(domain, pushType, deviceType, message, subject, Arrays.asList(tokens));

  
        return new ResponseEntity(HttpStatus.OK);
    }
    /**
     * Enqueuing the push notification task for Urban airship (iOS and Android).
     * @param request HTTP request object
     * @param message to be sent to the recipients
     * @param deviceType the type of device (iOS or Android)
     * @param toDeviceToken a list of mobile device token
     * @param extra the extra payload for the push notification
     */
    @RequestMapping(value = "v10/_queue/push", method = RequestMethod.POST)
    public ResponseEntity queuePushMessages(HttpServletRequest request, 
            @PathVariable String domain,
            @RequestParam String message,
            @RequestParam (required = false) String subject,
            @RequestParam(value = "tokens[]", required = false) String tokens[],
            @RequestParam(defaultValue = PushService.PUSH_URBAN) String pushType,
            @RequestParam(value = "deviceType", defaultValue="0") long deviceType) throws Exception {
        
        LOG.debug(" _queue push message with type {} ", pushType);
        pushService.pushTokens(domain, pushType, deviceType, message, subject, Arrays.asList(tokens));
        return new ResponseEntity(HttpStatus.OK);
    }
//

    
}
