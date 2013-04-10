/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.mvc;

import com.wadpam.docrest.domain.RestCode;
import com.wadpam.docrest.domain.RestReturn;
import com.wadpam.open.exceptions.NotFoundException;
import static com.wadpam.open.mvc.CrudController.LOG;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author sosandstrom
 */
public abstract class CrawlerCrudController<
        J extends Object, 
        T extends Object, 
        ID extends Serializable,
        S extends CrudService<T, ID>> 
    extends CrudController<J, T, ID, S>{
    
    protected final String resourcePathname;

    protected CrawlerCrudController(Class<J> jsonClazz, String resourcePathname) {
        super(jsonClazz);
        this.resourcePathname = resourcePathname;
    }

    /**
     * Writes the entities as CSV on response body
     * @param response
     * @param startDate
     * @param endDate
     * @throws IOException 
     */
    @RestReturn(value=String.class, code={
        @RestCode(code=200, description="A CSV stream with Entities", message="OK")})
    @RequestMapping(value="v10", method= RequestMethod.GET, params={"startDate"})
    public void exportCsv(
            HttpServletResponse response,
            @RequestParam Long startDate,
            @RequestParam(required=false) Long endDate
    ) throws IOException 
    {
        response.setContentType("text/csv");
        final String contentDisposition = String.format("attachment;filename=%ss.csv",
                service.getTableName());
        response.setHeader("Content-Disposition", contentDisposition);
        final OutputStream out = response.getOutputStream();
        
        service.exportCsv(out, startDate, endDate);
    }
    
    @RequestMapping(value="{country}/{city}/{venueName}/{category}/{itemName}/{id}.html")
    public ModelAndView getForFriendlyUrl(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            @PathVariable String country,
            @PathVariable String city,
            @PathVariable String venueName,
            @PathVariable String category,
            @PathVariable String itemName,
            @PathVariable ID id,
            @RequestParam(required=false) String parentKeyString,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            Model model) {
        LOG.debug("GET {} for crawler {}", id, userAgent);
        
        preService(request, domain, CrudListener.GET, null, null, id);
        T d = service.get(parentKeyString, id);
        if (null == d) {
            throw new NotFoundException(ERR_GET_NOT_FOUND, request.getQueryString());
        }
        
        // default for GET is to include inner objects if any
        if (null != getInnerParameterNames()) {
            for (String name : getInnerParameterNames()) {
                request.setAttribute(name, Boolean.TRUE);
            }
        }
        J body = convertWithInner(request,response, domain, model, d);
        postService(request, domain, CrudListener.GET, body, id, d);
        
        model.addAttribute("domain", domain);
        model.addAttribute("resourceName", resourcePathname);
        model.addAttribute("country", country);
        model.addAttribute("city", city);
        model.addAttribute("venueName", venueName);
        model.addAttribute("category", category);
        model.addAttribute("itemName", itemName);
        model.addAttribute("id", id);
        model.addAttribute("userAgent", userAgent);
        model.addAttribute("entity", d);
        model.addAttribute("body", body);

        ModelAndView mav = new ModelAndView(getViewName(userAgent), model.asMap());
        return mav;
    }

    /**
     * Resolves crawler for Googlebot, facebookexternalhit, Twitterbot, GooglePlusBot, LinkedInBot.
     * @param userAgent as the HTTP request header User-Agent
     * @return defaults to "crawler/default"
     */
    protected String getViewName(String userAgent) {
        if (null != userAgent && userAgent.contains("Googlebot")) {
            return "crawler/Googlebot";
        }
        if (null != userAgent && userAgent.contains("facebookexternalhit")) {
            return "crawler/facebookexternalhit";
        }
        if (null != userAgent && userAgent.contains("Twitterbot")) {
            return "crawler/Twitterbot";
        }
        if (null != userAgent && userAgent.contains("GooglePlusBot")) {
            return "crawler/GooglePlusBot";
        }
        if (null != userAgent && userAgent.contains("LinkedInBot")) {
            return "crawler/LinkedInBot";
        }
                
        return "crawler/default";
    }
    
}
