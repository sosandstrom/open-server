package com.wadpam.open.analytics.google.trackinginfo;

/**
 * The content being tracked, typically a web page or app screen view.
 *
 * @author mattiaslevin
 */
public class ContentInfo {


    public ContentInfo() {
    }

    /**
     * Construct content info based on host name, path, title and description.
     * @param hostName host name
     * @param path document path. Should start with /
     * @param title document title
     * @param contentDescription content description, optional
     */
    public ContentInfo(String hostName, String path, String title, String contentDescription) {
        this.hostName = hostName;
        this.path = path;
        this.title = title;

        // Set a default document url
        if (null != hostName && null != path) {
            absoluteUrl = String.format("%s%s", hostName,
                    path.startsWith("/") ? path : "/" + path);
        }

        if (null != contentDescription) {
            this.contentDescription = contentDescription;
        } else {
            // Set a default value
            this.contentDescription = absoluteUrl;
        }

    }

    /**
     * Construct content info based on absolute url, title and description
     * @param absoluteUrl the absolute url of the document
     * @param title document title
     * @param contentDescription  content description, optional
     */
    public ContentInfo(String absoluteUrl, String title, String contentDescription) {
        this.absoluteUrl = absoluteUrl;
        this.title = title;

        if (null != contentDescription) {
            this.contentDescription = contentDescription;
        } else {
            // Set a default value
            this.contentDescription = this.absoluteUrl;
        }
    }

    /**
     * The full url of the page on which the content reside, e.g. http://foo.com/home?a=b
     * Setting hostName and path will override this value.
     */
    private String absoluteUrl;

    /**
     * Host name where the document is stored.
     * foo.com
     */
    private String hostName;

    /**
     * Document path. Should start with /, e.g. /home?a=b
     */
    private String path;

    /**
     * Document title.
     */
    private String title;

    /**
     * ContentInfo description.
     * Used for tracking the screen name when tracking apps screen views.
     * If not set either the absoluteUrl will be used as default.
     *
     * Added in the Measurement protocol.
     */
    private String contentDescription;


    // Setters and getters
    public String getContentDescription() {
        return contentDescription;
    }

    public void setContentDescription(String contentDescription) {
        this.contentDescription = contentDescription;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbsoluteUrl() {
        return absoluteUrl;
    }

    public void setAbsoluteUrl(String absoluteUrl) {
        this.absoluteUrl = absoluteUrl;
    }
}
