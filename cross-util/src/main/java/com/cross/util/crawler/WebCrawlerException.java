package com.cross.util.crawler;

/**
 *
 * @author xiaodong.shen
 */
public class WebCrawlerException extends Exception {

    public WebCrawlerException(String s) {
        super(s);
    }

    public WebCrawlerException(Throwable t) {
        super(t);
    }

    public WebCrawlerException(String s, Throwable t)
    {
        super(s, t);
    }
}
