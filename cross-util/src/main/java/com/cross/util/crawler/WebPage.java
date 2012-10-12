package com.cross.util.crawler;

import org.apache.log4j.Logger;

/**
 *
 * @author xiaodong.shen
 */
public class WebPage {
    private Link link = null;
    private String mimetype = null;
    private byte[] bytes = null;

    public WebPage()
    {
    }

    public WebPage(Link link, byte[] bytes, String mimetype)
    {
        this.link = link;
        this.bytes = bytes;
        this.mimetype = mimetype;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }
}
