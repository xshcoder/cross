package com.cross.util.mongo;

/**
 *
 * @author xiaodong.shen
 */
import com.cross.util.common.MimeMapper;
import com.cross.util.common.StringUtil;
import com.cross.util.crawler.InvalidUrlException;
import com.cross.util.crawler.Link;
import com.cross.util.crawler.SimpleWebCrawler;
import com.cross.util.crawler.WebCrawlerException;
import com.cross.util.crawler.WebPage;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.WriteResult;
import java.net.UnknownHostException;

public class MongoUtil {

    public static Mongo mongo = null;

    public static Mongo getMongoInstance(String ip) throws UnknownHostException {
        if (mongo == null) {
            mongo = new Mongo(ip);
        }
        return mongo;
    }

    public static Mongo getMongoInstance(String ip, int port) throws UnknownHostException {
        if (mongo == null) {
            mongo = new Mongo(ip, port);
        }
        return mongo;
    }

    public static DB getDB(Mongo mongo, String dbname) {
        return mongo.getDB(dbname);
    }

    public static DBCollection getCollection(Mongo mongo, String dbname, String collectionname) {
        DB db = mongo.getDB(dbname);
        DBCollection collection = db.getCollection(collectionname);
        return collection;
    }

    public static void dropDB(Mongo mongo, String dbname) {
        DB db = mongo.getDB(dbname);
        if (db != null) {
            db.dropDatabase();
            System.out.println("drop database " + dbname);
        } else {
            System.out.println("database does not exist, can not drop database " + dbname);
        }
    }

    public static void dropCollection(Mongo mongo, String dbname, String collectionname) {
        DB db = mongo.getDB(dbname);
        if (db != null) {
            DBCollection collection = db.getCollection(collectionname);
            if (collection != null) {
                collection.drop();
                System.out.println("drop collection " + dbname + "." + collectionname);
            } else {
                System.out.println("collection does not exist, can not drop collection" + dbname + "." + collectionname);
            }
        }
    }

    public static void closeMongo() {
        if (mongo != null) {
            mongo.close();
            mongo = null;
        }
    }

    public static void saveDocument(WebPage page, DBCollection collection) throws MongoAccessException {
        BasicDBObject doc = new BasicDBObject();
        String url = page.getLink().toString();
        if (url == null) {
            throw new MongoAccessException("null url");
        }
        doc.put("url", url);
        doc.put("mimetype", page.getMimetype());
        doc.put("charset", page.getCharset());
        doc.put("content", page.getBytes());
        if (getDocument(url, collection) != null) {
            WriteResult result = collection.update(new BasicDBObject().append("url", page.getLink().toString()), doc);
            if (result.getLastError().ok() != true) {
                throw new MongoAccessException("saveDocument failed", result.getLastError().getException());
            }
        } else {
            WriteResult result = collection.insert(doc);
            if (result.getLastError().ok() != true) {
                throw new MongoAccessException("saveDocument failed", result.getLastError().getException());
            }
        }
    }

    public static DBObject getDocument(String url, DBCollection collection) {
        BasicDBObject query = new BasicDBObject();
        query.put("url", url);
        DBObject doc = collection.findOne(query);
        return doc;
    }

    public static void main(String args[]) {
        try {
            Mongo mg = MongoUtil.getMongoInstance("127.0.0.1");
            DB db = MongoUtil.getDB(mg, "dbtest");
            DBCollection collection = MongoUtil.getCollection(mg, "dbtest", "collectiontest");
            if (db == null) {
                System.out.println("db does not exist");
            } else if (collection == null) {
                System.out.println("collection does not exist");
            }
            String url = "http://s1.pet.mop.com";
            String charset = "gb2312";
            try {
                SimpleWebCrawler crawler = new SimpleWebCrawler();
                Link link = new Link();
                link.fromString(url);
                System.out.println("crawl url: " + link.toString());
                WebPage page = crawler.crawl(link);
                page.setMimetype(MimeMapper.HTML);
                page.setCharset(charset);
                MongoUtil.saveDocument(page, collection);
                DBObject doc = MongoUtil.getDocument(url, collection);
                if (doc != null) {
                    byte[] bytes = (byte[]) doc.get("content");
                    String content = StringUtil.convert(bytes, charset);
                    System.out.println(content);
                } else {
                    System.out.println("document with url='" + url + "' does not exist in mongodb");
                }
            } catch (WebCrawlerException webCrawlerException) {
                System.out.println(webCrawlerException.getMessage());
            } catch (InvalidUrlException invalidUrlException) {
                System.out.println(invalidUrlException.getMessage());
            } catch (MongoAccessException mongoAccessException)
            {
                System.out.println(mongoAccessException.getMessage());
            }
        } catch (UnknownHostException unknownHostException) {
            System.out.println(unknownHostException.getMessage());
        }
    }
}