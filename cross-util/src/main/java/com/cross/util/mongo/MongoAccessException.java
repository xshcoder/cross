package com.cross.util.mongo;

/**
 *
 * @author xiaodong.shen
 */
public class MongoAccessException extends Exception {

    public MongoAccessException(String s) {
        super(s);
    }

    public MongoAccessException(Throwable t) {
        super(t);
    }

    public MongoAccessException(String s, Throwable t) {
        super(s, t);
    }
}
