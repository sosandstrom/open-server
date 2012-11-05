package com.wadpam.open.service;

import com.google.appengine.api.datastore.Email;
import com.wadpam.open.transaction.Idempotent;
import com.wadpam.open.dao.DAppAdminDao;
import com.wadpam.open.dao.DAppDao;
import com.wadpam.open.domain.DApp;
import com.wadpam.open.domain.DAppAdmin;
import com.wadpam.open.security.GaeUserDetails;
import com.wadpam.server.exceptions.BadRequestException;
import com.wadpam.server.exceptions.ServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

/**
 * This class implemented functionality related to creating and managing a RnR App.
 * @author mattiaslevin
 */
public class AppService {
    static final Logger LOG = LoggerFactory.getLogger(AppService.class);

    private DAppDao appDao;
    private DAppAdminDao appAdminDao;
    private EmailSender emailSender;

    static final int API_PASSWORD_LENGTH = 30;

    static final String API_PASSWORD_CHARS = "1234567890abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPQRSTUVXYZ";
    public static final String ACCOUNT_PENDING = "pending";
    public static final String ACCOUNT_ACTIVE = "active";

    public static final String ACCOUNT_SUSPENDED = "suspended";
    static final int DEFAULT_MAX_NUMBER_OF_APPS = 10;

    public static final String CREATE_ACCOUNT_START_STATE = ACCOUNT_ACTIVE;

    private String createAccountStartState = CREATE_ACCOUNT_START_STATE;
    private int maxNumberOfAppsStartValue = DEFAULT_MAX_NUMBER_OF_APPS;


    /* App related methods */

    // Create new app for a specific domain
    @Idempotent
    @Transactional
    @PreAuthorize("hasPermission(#domain, 'isAppAdmin')")
    public DApp createApp(String domain, String adminEmail, String description) {
        LOG.debug("Create new app for domain:{}", domain);

//        // Code for testing the Spring exception handling
//        if (true)
//            throw new NullPointerException(String.format("Throwing a test exception when creating an app:%s", domain));

        DApp dApp = appDao.findByPrimaryKey(domain);
        if (null == dApp) {

            // Check that the user has not reach the max number of apps
            Iterator<DApp> dAppIterator= appDao.queryByAdminEmail(new Email(adminEmail)).iterator();
            int currentNumberOfApps = 0;
            while (dAppIterator.hasNext()) {
                currentNumberOfApps++;
                dAppIterator.next();
            }

            DAppAdmin dAppAdmin = appAdminDao.findByEmail(new Email(adminEmail));
            if (currentNumberOfApps >= dAppAdmin.getMaxNumberOfApps())
                // This user is not allowed to create additional apps
            {
                throw new BadRequestException(400, String.format("Admin have reached the limit of apps allowed:%s", dAppAdmin.getMaxNumberOfApps()));
            }

            // Create new app
            dApp = new DApp();

            // Only set these properties when created first time
            Collection<Email> adminEmails = new ArrayList<Email>();
            adminEmails.add(new Email(adminEmail));
            dApp.setAppAdmins(adminEmails);

            dApp.setDomainName(domain);
            dApp.setApiUser(generateApiUser(domain));
            dApp.setApiPassword(generateApiPassword(domain));
            dApp.setCreatedBy(getCurrentUserId());
        } else {
            // Set updater
            dApp.setUpdatedBy(getCurrentUserId());
        }

        // Properties that should be possible to update
        dApp.setDescription(description);

        // Store in datastore
        appDao.persist(dApp);

        return dApp;
    }

    // Get the current user id from Spring security
    private String getCurrentUserId() {
        return getCurrentUserDetails().getUsername();
    }

    // Get Google app engine user from the security context
    private GaeUserDetails getCurrentUserDetails() {
        return (GaeUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // Generate api user, the MD5 hash of the domain string
    private String generateApiUser(String domain) {

        try {

            byte[] bytes = domain.getBytes("UTF-8");

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.digest(bytes);

            // Covert into a hex string
            final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
            char[] hexChars = new char[bytes.length * 2];
            int v;
            for ( int j = 0; j < bytes.length; j++ ) {
                v = bytes[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }

            return new String(hexChars);

        } catch (Exception e) {
            throw new ServerErrorException(500, String.format("Not possible to generate REST api user string with reason:%s", e.getMessage()));
        }
    }

    // Generate app password
    private String generateApiPassword(String domain) {
        Random rand = new Random();

        char[] key = new char[API_PASSWORD_LENGTH];
        for (int i = 0; i < API_PASSWORD_LENGTH; i++)
            key[i] = API_PASSWORD_CHARS.charAt(rand.nextInt(API_PASSWORD_CHARS.length()));

        return new String(key);
    }

    // Update list a of app admin emails
    @Idempotent
    @Transactional
    @PreAuthorize("hasPermission(#domain, 'isAppAdmin')")
    public DApp setAppAdmins(String domain, Collection<String> adminEmails) {
        LOG.debug("Set app admins:{} for domain:{}", adminEmails, domain);

        DApp dApp = getApp(domain);
        if (null == dApp)
            return null;

        Collection<Email> emails = new ArrayList<Email>(adminEmails.size());
        for (String appAdmin : adminEmails)
            emails.add(new Email(appAdmin));

        dApp.setAppAdmins(emails);
        dApp.setUpdatedBy(getCurrentUserId());
        appDao.persist(dApp);

        return dApp;
    }

    // Get app details
    @PreAuthorize("hasPermission(#domain, 'isAppAdmin')")
    public DApp getApp(String domain) {
        LOG.debug("Get app for domain:{}", domain);
        return appDao.findByPrimaryKey(domain);
    }

    // Delete app
    @Idempotent
    @Transactional
    @PreAuthorize("hasPermission(#domain, 'isAppAdmin')")
    public DApp deleteApp(String domain) {
        LOG.debug("Delete app for domain:{}", domain);

        DApp dApp = appDao.findByPrimaryKey(domain);
        if (null == dApp)
            return null;

        appDao.delete(dApp);
        return dApp;
    }

    // Get all apps for a user
    public Iterable<DApp> getAllAppsForAppAdmin(String adminEmail) {
        LOG.debug("Get all apps for admin with emai1l:{}", adminEmail);
        return appDao.queryByAdminEmail(new Email(adminEmail));
    }

    // Get all apps in the system
    public Iterable<DApp> getAllApps() {
        LOG.debug("Get all apps in the system");
        return appDao.queryAll();
    }

    // Generate new api password
    @Idempotent
    @Transactional
    @PreAuthorize("hasPermission(#domain, 'isAppAdmin')")
    public DApp generateNewApiPassword(String domain) {
        LOG.debug("Generate new api password for domain:{}", domain);

        DApp dApp = appDao.findByPrimaryKey(domain);
        if (null == dApp)
            return null;

        dApp.setApiPassword(generateApiPassword(domain));
        dApp.setUpdatedBy(getCurrentUserId());
        appDao.persist(dApp);

        return dApp;
    }


    /* App admin related methods */

    // Create a new app admin      <
    @Idempotent
    @Transactional
    @PreAuthorize("hasPermission(#adminEmail, 'isAdmin')")
    public DAppAdmin createAppAdmin(String adminEmail, String adminId, String name, String detailUrl) {
        LOG.debug("Create app admin with email:{} ", adminEmail);

        DAppAdmin dAppAdmin = appAdminDao.findByEmail(new Email(adminEmail));
        if (null == dAppAdmin) {
            // User does not exist
            dAppAdmin = new DAppAdmin();
            dAppAdmin.setAdminId(adminId);
            dAppAdmin.setEmail(new Email(adminEmail));
            dAppAdmin.setAccountStatus(createAccountStartState);
            dAppAdmin.setMaxNumberOfApps((long) maxNumberOfAppsStartValue);
            dAppAdmin.setCreatedBy(getCurrentUserId());

            // Send email to indicate new app admin joined
            StringBuilder sb = new StringBuilder();
            sb.append("A new admin just joined Pocket-Reviews.\n");
            sb.append("Name: " + name + "\n");
            sb.append("Email: " + dAppAdmin.getEmail() + "\n");
            sb.append(UriComponentsBuilder.fromUriString(detailUrl).path("/{email}").buildAndExpand(dAppAdmin.getEmail()));
            emailSender.sendEmailToAdmin("Pocket-Review have a new app admin", sb.toString());
        } else {
            // Set updater
            dAppAdmin.setUpdatedBy(getCurrentUserId());
        }

        // Update the name each time, not only when first created
        dAppAdmin.setName(name);

        // Store in datastore
        appAdminDao.persist(dAppAdmin);

        return dAppAdmin;
    }

    // Delete specified app admin
    @Idempotent
    @Transactional
    @PreAuthorize("hasPermission(#adminEmail, 'isAdmin')")
    public DAppAdmin deleteAppAdmin(String adminEmail) {
        LOG.debug("Remove admin with email:{}", adminEmail);

        DAppAdmin dAppAdmin = appAdminDao.findByEmail(new Email(adminEmail));
        if (null == dAppAdmin)
            return null;

        // Delete from datastore
        appAdminDao.delete(dAppAdmin);

        return dAppAdmin;
    }

    // Get details for a app admin
    @PreAuthorize("hasPermission(#adminEmail, 'isAdmin')")
    public DAppAdmin getAppAdmin(String adminEmail) {
        LOG.debug("Get details for admin with email:{}", adminEmail);
        return appAdminDao.findByEmail(new Email(adminEmail));
    }

    // Get all app admins in the system
    public Iterable<DAppAdmin> getAllAppAdmins() {
        LOG.debug("Get all app admins in the system");
        return  appAdminDao.queryAll();
    }

    // Update app admin account status
    @Idempotent
    @Transactional
    @PreAuthorize("hasPermission(#adminEmail, 'isAdmin')")
    public DAppAdmin updateAdminAccountStatus(String adminEmail, String accountStatus) {
        LOG.debug("Set account status to:{} for app admin with email:{}", accountStatus, adminEmail);

        DAppAdmin dAppAdmin = appAdminDao.findByEmail(new Email(adminEmail));
        if (null == dAppAdmin)
            return null;

        dAppAdmin.setAccountStatus(accountStatus);
        dAppAdmin.setUpdatedBy(getCurrentUserId());

        // Update datastore
        appAdminDao.persist(dAppAdmin);

        return dAppAdmin;
    }

    // Setters and Getters
    public void setAppDao(DAppDao appDao) {
        this.appDao = appDao;
    }

    public void setEmailSender(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void setAppAdminDao(DAppAdminDao appAdminDao) {
        this.appAdminDao = appAdminDao;
    }

    public void setCreateAccountStartState(String createAccountStartState) {
        this.createAccountStartState = createAccountStartState;
    }

    public void setMaxNumberOfAppsStartValue(int maxNumberOfAppsStartValue) {
        this.maxNumberOfAppsStartValue = maxNumberOfAppsStartValue;
    }
}
