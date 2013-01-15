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
    private Serializable cursorKey;

    /**
     * The number of items to return.
     * If the number of items actually returned are less then the requested page size, end of pagination have been reached.
     */
    private int pageSize;

    /** The products */
    private Collection<T> items;

    @Override
    public String toString() {
        return String.format("cursor:%s page size:%d products:%s", cursorKey, pageSize, items);
    }

    // Setters and getters

    public Serializable getCursorKey() {
        return cursorKey;
    }

    public void setCursorKey(Serializable cursorKey) {
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
}
