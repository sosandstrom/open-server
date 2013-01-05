package com.wadpam.open.json;

/**
 *
 * @author os
 */
public class JSample extends JBaseObject {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected String subString() {
        return String.format("createdDate:%d", getCreatedDate());
    }
    
    
}
