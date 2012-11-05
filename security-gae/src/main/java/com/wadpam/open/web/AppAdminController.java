package com.wadpam.open.web;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.wadpam.docrest.domain.RestCode;
import com.wadpam.docrest.domain.RestReturn;
import com.wadpam.open.domain.DAppAdmin;
import com.wadpam.open.json.JAppAdmin;
import com.wadpam.open.security.GaeUserDetails;
import com.wadpam.open.service.AppService;
import com.wadpam.open.service.Converter;
import com.wadpam.server.exceptions.NotFoundException;
import com.wadpam.server.exceptions.RestException;
import com.wadpam.server.exceptions.ServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

/**
 * The apps controller implements all REST methods related to managing app admins.
 * @author mattiaslevin
 */
@Controller
@RequestMapping(value="backoffice/admin")
public class AppAdminController {

    static final Logger LOG = LoggerFactory.getLogger(AppAdminController.class);
    static final Converter CONVERTER = new Converter();

    private AppService appService;

    private static final String LOGGEDIN_HTML = "/loggedin.html";
    private static final String LOGGEDOUT_HTML = "/loggedout.html";


    /**
     * Login app admin. Redirect to Google authentication if needed.
     * @return the and http response code indicating the outcome of the operation
     */
    @RestReturn(value=JAppAdmin.class, entity=JAppAdmin.class, code={
            @RestCode(code=200, message="OK", description="Successful login")
    })
    @RequestMapping(value="login", method= RequestMethod.GET)
    public void loginAdmin(HttpServletRequest request,
                           HttpServletResponse response,
                           UriComponentsBuilder uriBuilder) {

        // Figure out the base url
        String destinationUrl = uriBuilder.replacePath(LOGGEDIN_HTML).build().toUriString();

        try {
            // Check if the user is already logged in or not
            UserService userService = UserServiceFactory.getUserService();
            if (userService.isUserLoggedIn()) {
                LOG.debug("User already logged with Google, no need to login");
                response.sendRedirect(destinationUrl);
            }  else {
                // User not logged in, redirect to Google login page
                LOG.debug("Log in new user with Google");
                String googleLoginUrl = userService.createLoginURL(destinationUrl);
                response.sendRedirect(googleLoginUrl);
            }
        }
        catch (IOException e) {
            LOG.error("Not possible to redirect user after login with reason:{}", e.getMessage());
            throw new ServerErrorException(500, "Not possible to redirect admin after Google login");
        }
    }

    /**
     * Logout app admin. Redirect to Google if needed.
     * @return the and http response code indicating the outcome of the operation
     */
    @RestReturn(value=JAppAdmin.class, entity=JAppAdmin.class, code={
            @RestCode(code=302, message="OK", description="Successful logout")
    })
    @RequestMapping(value="logout", method= RequestMethod.GET)
    public void logoutAdmin(HttpServletRequest request,
                            HttpServletResponse response,
                            UriComponentsBuilder uriBuilder) {

        // Figure out the base url
        // Figure out the base url
        String destinationUrl = uriBuilder.replacePath(LOGGEDOUT_HTML).build().toUriString();

        try {
            // Check if user already is logged out
            UserService userService = UserServiceFactory.getUserService();
            if (!userService.isUserLoggedIn()) {
                LOG.debug("User not logged in with Google, no need to logout");
                response.sendRedirect(destinationUrl);
            } else {
                LOG.debug("Logout Google user with email " + userService.getCurrentUser().getEmail());
                String googleLogoutUrl = userService.createLogoutURL(destinationUrl);
                response.sendRedirect(googleLogoutUrl);
            }
        }
        catch (IOException e) {
            LOG.error("Not possible to redirect user after logout with reason:{}", e.getMessage());
            throw new ServerErrorException(500, "Not possible to redirect admin after Google logout");
        }
    }

    /**
     * Create a new app admin for the currently logged in Google user.
     * @param name optional. The users nickname that will be used as display name
     * @return redirect to the newly create user details
     */
    @RestReturn(value=JAppAdmin.class, entity=JAppAdmin.class, code={
            @RestCode(code=302, message="OK", description="Redirect to newly created admin details")
    })
    @RequestMapping(value="", method= RequestMethod.POST)
    public RedirectView createAdmin(HttpServletRequest request,
                                    HttpServletResponse response,
                                    UriComponentsBuilder uriBuilder,
                                    @RequestParam(required = false) String name) {

        // Get current user
        if (null == getCurrentUserDetails()) {
            LOG.debug("Trying to create an admin that is not logged in:{}", getCurrentUserEmail());
            throw new RestException(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.value(), "Admin not logged in");
        }

        String detailsUrl = uriBuilder.path("/backoffice/admin").build().toUriString();
        final DAppAdmin body = appService.createAppAdmin(getCurrentUserEmail(), getCurrentUserId(), name, detailsUrl);

        return new RedirectView(detailsUrl);
    }

    // Get the current user email from Spring security
    private String getCurrentUserEmail() {
        return getCurrentUserDetails().getEmail();
    }

    // Get the current user id from Spring security
    private String getCurrentUserId() {
        return getCurrentUserDetails().getUsername();
    }

    // Get Google app engine user from the security context
    private GaeUserDetails getCurrentUserDetails() {
        return (GaeUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * Delete the app admin for the currently logged in Google user.
     * @return the http response code indicating the outcome of the operation
     */
    @RestReturn(value=JAppAdmin.class, entity=JAppAdmin.class, code={
            @RestCode(code=200, message="OK", description="Admin deleted"),
            @RestCode(code=404, message="NOK", description="Admin not found for current user")
    })
    @RequestMapping(value="", method= RequestMethod.DELETE)
    public ResponseEntity<JAppAdmin> deleteCurrentAdmin(HttpServletRequest request,
                                                        HttpServletResponse response) {

        // Get current user
        if (null == getCurrentUserDetails()) {
            LOG.debug("Trying to delete an admin that is not logged in:{}", getCurrentUserEmail());
            return new ResponseEntity<JAppAdmin>(HttpStatus.UNAUTHORIZED);
        }

        final DAppAdmin body = appService.deleteAppAdmin(getCurrentUserEmail());
        if (null == body)
            throw new NotFoundException(404, String.format("No app admin found for email:{}", getCurrentUserEmail()));

        return new ResponseEntity<JAppAdmin>(HttpStatus.OK);
    }

    /**
     * Delete a specified app admin.
     * @param email the admins email
     * @return the http response code indicating the outcome of the operation
     */
    @RestReturn(value=JAppAdmin.class, entity=JAppAdmin.class, code={
            @RestCode(code=200, message="OK", description="Admin deleted"),
            @RestCode(code=404, message="NOK", description="Admin not found for email")
    })
    @RequestMapping(value="{email}", method= RequestMethod.DELETE)
    public ResponseEntity<JAppAdmin> deleteAdmin(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 @PathVariable String email) {

        final DAppAdmin body = appService.deleteAppAdmin(email);
        if (null == body)
            throw new NotFoundException(404, String.format("No app admin found for email:{}", email));

        return new ResponseEntity<JAppAdmin>(HttpStatus.OK);
    }

    /**
     * Get app admin details for the currently logged in Google user.
     * @return the admin details
     */
    @RestReturn(value=JAppAdmin.class, entity=JAppAdmin.class, code={
            @RestCode(code=200, message="OK", description="Admin details found"),
            @RestCode(code=404, message="NOK", description="Admin details not found for current user")
    })
    @RequestMapping(value="", method= RequestMethod.GET)
    public ResponseEntity<JAppAdmin> getCurrentAdmin(HttpServletRequest request,
                                                     HttpServletResponse response) {

        // Get current user
        if (null == getCurrentUserDetails()) {
            LOG.debug("Trying to get for admin that is not logged in:{}", getCurrentUserEmail());
            return new ResponseEntity<JAppAdmin>(HttpStatus.UNAUTHORIZED);
        }

        final DAppAdmin body = appService.getAppAdmin(getCurrentUserEmail());
        if (null == body)
            throw new NotFoundException(404, String.format("No app admin found"));

        return new ResponseEntity<JAppAdmin>(CONVERTER.convert(body), HttpStatus.OK);
    }

    /**
     * Get app admin details for a specific user.
     * @param email the admins email
     * @return the officer details
     */
    @RestReturn(value=JAppAdmin.class, entity=JAppAdmin.class, code={
            @RestCode(code=200, message="OK", description="Admin details found"),
            @RestCode(code=404, message="NOK", description="Admin details not found for email")
    })
    @RequestMapping(value="{userId}", method= RequestMethod.GET)
    public ResponseEntity<JAppAdmin> getAdmin(HttpServletRequest request,
                                              HttpServletResponse response,
                                              @PathVariable String email) {

        final DAppAdmin body = appService.getAppAdmin(email);
        if (null == body)
            throw new NotFoundException(404, String.format("No app admin found for email:{}", email));

        return new ResponseEntity<JAppAdmin>(CONVERTER.convert(body), HttpStatus.OK);
    }

    /**
     * Get all app admins in the system.
     * @return a list of admins
     */
    @RestReturn(value=JAppAdmin.class, entity=JAppAdmin.class, code={
            @RestCode(code=200, message="OK", description="Admins found")
    })
    @RequestMapping(value="all", method= RequestMethod.GET)
    public ResponseEntity<Collection<JAppAdmin>> getAllAdmins(HttpServletRequest request,
                                                              HttpServletResponse response) {

        final Iterable<DAppAdmin> dAppAdmins = appService.getAllAppAdmins();

        return new ResponseEntity<Collection<JAppAdmin>>((Collection<JAppAdmin>)CONVERTER.convert(dAppAdmins), HttpStatus.OK);
    }

    /**
     * Change the status of an app admin account.
     * @param email the admin email
     * @param status the new status
     *               0 - pending
     *               1 - active approved and active
     *               2 - suspended
     * @return redirect to the updated admin details
     */
    @RestReturn(value=JAppAdmin.class, entity=JAppAdmin.class, code={
            @RestCode(code=302, message="OK", description="Redirect to the updated admin details")
    })
    @RequestMapping(value="{userId}/status/{status}", method= RequestMethod.POST)
    public RedirectView updateAdminAccountStatus(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 UriComponentsBuilder uriBuilder,
                                                 @PathVariable String email,
                                                 @PathVariable int status) {

        String accountStatus;
        switch (status) {
            case 0:
                accountStatus = AppService.ACCOUNT_PENDING;
                break;
            case 1:
                accountStatus = AppService.ACCOUNT_ACTIVE;
                break;
            case 2:
                accountStatus = AppService.ACCOUNT_SUSPENDED;
                break;
            default:
                LOG.error("Trying to set account status to state not supported:{}", status);
                throw new RestException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.value(), "Account status not supported");
        }

        final DAppAdmin body = appService.updateAdminAccountStatus(email, accountStatus);
        if (null == body)
            throw new NotFoundException(404, String.format("No app admin found for email:{}", email));

        // Figure out the url
        String redirectUrl = uriBuilder.path("/backoffice/admin/{email}").buildAndExpand(email).toUriString();

        return new RedirectView(redirectUrl);
    }


    // Setters and Getters
    public void setAppService(AppService appService) {
        this.appService = appService;
    }
}
