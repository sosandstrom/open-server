package com.wadpam.open.web;

import com.wadpam.docrest.domain.RestCode;
import com.wadpam.docrest.domain.RestReturn;
import com.wadpam.open.domain.DAppDomain;
import com.wadpam.open.exceptions.NotFoundException;
import com.wadpam.open.json.JAppDomain;
import com.wadpam.open.service.DomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller for managing domains.
 * @author mattiaslevin
 */
public class DomainController extends AbstractRestController {
    static final Logger LOG = LoggerFactory.getLogger(DomainController.class);

    public static final int ERR_BASE_DOMAIN = DomainService.ERR_BASE_DOMAIN;
    public static final int ERR_DOMAIN_NOT_FOUND = ERR_BASE_DOMAIN + 101;

    private DomainService domainService;

    private Converter CONVERTER = new Converter();

    /**
     * Create a new domain.
     *
     * The domain name must be unique and not exist already.
     * @param domain the domain name
     * @param username the api username, used during basic authentication
     * @param password the api password, used during basic authentication
     * @param description optional domain description
     * @param toEmail optional domain email the might be required is some services
     * @param trackingCode optional domain tracking code the will be used for analytics
     * @return redirect to the newly created domain
     */
    @RestReturn(value=JAppDomain.class, entity=JAppDomain.class, code={
            @RestCode(code=302, message="OK", description="Redirect to the newly created domain"),
            @RestCode(code=409, message="NOK", description="Domain already exists")
    })
    @RequestMapping(value="_admin/domain", method= RequestMethod.POST)
    public RedirectView createDomain(HttpServletRequest request,
                                HttpServletResponse response,
                                UriComponentsBuilder uriBuilder,
                                @RequestParam String domain,
                                @RequestParam String username,
                                @RequestParam String password,
                                @RequestParam(required=false) String description,
                                @RequestParam(required=false) String toEmail,
                                @RequestParam(required=false) String trackingCode) {


        DAppDomain dAppDomain = domainService.createDomain(domain, username, password,
                description, toEmail, trackingCode);

        return new RedirectView(uriBuilder.path("/_admin/domain/{domain}").
                buildAndExpand(dAppDomain.getAppDomain()).toUriString());
    }

    /**
     * Update an existing domain.
     * @param domain the domain name
     * @param username the api username, used during basic authentication
     * @param password the api password, used during basic authentication
     * @param description optional domain description
     * @param toEmail optional domain email the might be required is some services
     * @param trackingCode optional domain tracking code the will be used for analytics
     * @return redirect to the newly created domain
     */
    @RestReturn(value=JAppDomain.class, entity=JAppDomain.class, code={
            @RestCode(code=302, message="OK", description="Redirect to the newly updated domain"),
            @RestCode(code=404, message="NOK", description="Domain not found")
    })
    @RequestMapping(value="_admin/domain/{domain}", method= RequestMethod.POST)
    public RedirectView updateDomain(HttpServletRequest request,
                                     HttpServletResponse response,
                                     UriComponentsBuilder uriBuilder,
                                     @PathVariable String domain,
                                     @RequestParam String username,
                                     @RequestParam String password,
                                     @RequestParam(required=false) String description,
                                     @RequestParam(required=false) String toEmail,
                                     @RequestParam(required=false) String trackingCode) {


        DAppDomain dAppDomain = domainService.updateDomain(domain, username, password,
                description, toEmail, trackingCode);

        return new RedirectView(uriBuilder.path("/_admin/domain/{domain}").
                buildAndExpand(dAppDomain.getAppDomain()).toUriString());
    }


    /**
     * Get domain details.
     * @param domain the domain name
     * @return domain details
     */
    @RestReturn(value=JAppDomain.class, entity=JAppDomain.class, code={
            @RestCode(code=200, message="OK", description="Domain found"),
            @RestCode(code=404, message="NOK", description="Domain not found")
    })
    @RequestMapping(value="_admin/domain/{domain}", method= RequestMethod.GET)
    @ResponseBody
    public JAppDomain getDomain(HttpServletRequest request,
                                  HttpServletResponse response,
                                  UriComponentsBuilder uriBuilder,
                                  @PathVariable String domain) {

        DAppDomain dAppDomain = domainService.getDomain(domain);
        if (null == dAppDomain) {
            throw new NotFoundException(ERR_DOMAIN_NOT_FOUND,
                    String.format("Domain:%s not found", domain));
        }

        return CONVERTER.convert(dAppDomain);
    }


    /**
     * Delete a domain.
     * @param domain the domain name
     * @return
     */
    @RestReturn(value=Void.class, entity=Void.class, code={
            @RestCode(code=200, message="OK", description="Domain found"),
            @RestCode(code=404, message="OK", description="Domain not found")
    })
    @RequestMapping(value="_admin/domain/{domain}", method= RequestMethod.DELETE)
    public void deleteDomain(HttpServletRequest request,
                                   HttpServletResponse response,
                                   UriComponentsBuilder uriBuilder,
                                   @PathVariable String domain) {

        DAppDomain dAppDomain = domainService.deleteDomain(domain);
    }


    // Setters and getters
    public void setDomainService(DomainService domainService) {
        this.domainService = domainService;
    }
}
