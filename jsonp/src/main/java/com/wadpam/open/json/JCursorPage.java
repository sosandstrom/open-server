package com.wadpam.open.json;

import java.util.Collection;

/**
 * Json object for a pages of items.
 * @author mattiaslevin
 */
public class JCursorPage<T extends JBaseObject> {

    /**
     * The cursor used to return the next page of items
     * The no cursor is returned, end of pagination have been reached
     */
    private String cursor;

    /**
     * The number of items to return.
     * If the number of items actually returned are less then the requested page size, end of pagination have been reached.
     */
    private Long pageSize;

    /** The products */
    private Collection<T> items;

    @Override
    public String toString() {
        return String.format("cursor:%s page size:%d products:%s", cursor, pageSize, items);
    }

    // Setters and getters
    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public Collection<T> getItems() {
        return items;
    }

    public void setItems(Collection<T> items) {
        this.items = items;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }
}
