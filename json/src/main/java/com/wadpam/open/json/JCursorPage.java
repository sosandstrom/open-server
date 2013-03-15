package com.wadpam.open.json;

import java.io.Serializable;
import java.util.Collection;

/**
 * Json object for a pages of items.
 * @author mattiaslevin
 */
public class JCursorPage<T extends Object> {

    /**
     * The cursor used to return the next page of items
     * The no cursor is returned, end of pagination have been reached
     */
    private String cursorKey;

    /**
     * The number of items to return.
     * If the number of items actually returned are less then the requested page size, end of pagination have been reached.
     */
    private int pageSize;
    
    /**
     * The total number of items available. Use for progress indication.
     */
    private Integer totalSize;

    /** The products */
    private Collection<T> items;

    @Override
    public String toString() {
        return String.format("JCursorPage{cursorKey:%s, pageSize:%d, totalSize:%d}", cursorKey, pageSize, totalSize);
    }

    // Setters and getters

    public String getCursorKey() {
        return cursorKey;
    }

    public void setCursorKey(String cursorKey) {
        this.cursorKey = cursorKey;
    }

    public Collection<T> getItems() {
        return items;
    }

    public void setItems(Collection<T> items) {
        this.items = items;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Integer totalSize) {
        this.totalSize = totalSize;
    }
}
