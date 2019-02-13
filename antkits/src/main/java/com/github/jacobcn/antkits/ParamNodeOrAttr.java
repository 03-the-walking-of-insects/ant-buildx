package com.github.jacobcn.antkits;

public class ParamNodeOrAttr {
    private String nodeXpath;
    private String attrName;
    private String value;

    // text content
    public void addText(String valueText) {
        this.value = valueText;
    }

    // getters and setters
    public String getXpath() {
        return nodeXpath;
    }

    public void setXpath(String nodeXpath) {
        this.nodeXpath = nodeXpath;
    }

    public String getName() {
        return attrName;
    }

    public void setName(String attrName) {
        this.attrName = attrName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value.trim();
    }

}
