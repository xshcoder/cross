package com.cross.util.crawler;

/**
 *
 * @author xiaodong.shen
 */
public class InvalidUrlException extends Exception {

    public InvalidUrlException(String s) {
        super(s);
    }

    public InvalidUrlException(Throwable t) {
        super(t);
    }

    public InvalidUrlException(String s, Throwable t)
    {
        super(s, t);
    }
}
