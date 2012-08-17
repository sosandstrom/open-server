package com.wadpam.open.json;

/**
 *
 * @author sosandstrom
 */
public class JBaseObject {
    /** Unique id for this Entity in the database */
    private String id;
    
    /** Milliseconds since 1970 when this Entity was created in the database */
    private Long createdDate;
    
    /** Possible states are DELETED (-1), PENDING (0), ACTIVE (1) and REDEEMED (2) */
    private Long state;
    
    /** Milliseconds since 1970 when this Entity was last updated in the database */
    private Long updatedDate;

    public JBaseObject() {
    }

    public JBaseObject(String id, Long createdDate, Long state, Long updatedDate) {
        this.id = id;
        this.createdDate = createdDate;
        this.state = state;
        this.updatedDate = updatedDate;
    }

    @Override
    public String toString() {
        return String.format("%s{id:%s, updatedDate:%s, %s}", getClass().getSimpleName(), id, updatedDate, subString());
    }
    
    protected String subString() { 
        return ""; 
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public Long getState() {
        return state;
    }

    public void setState(Long state) {
        this.state = state;
    }

    public Long getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Long updatedDate) {
        this.updatedDate = updatedDate;
    }

}
