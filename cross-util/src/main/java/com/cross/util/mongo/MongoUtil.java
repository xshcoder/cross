package com.cross.util.mongo;

/**
 *
 * @author xiaodong.shen
 */
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import java.net.UnknownHostException;

public class MongoUtil {

    public static Mongo mongo = null;

    public static Mongo getMongoInstance(String ip) throws UnknownHostException
    {
        if (mongo == null)
        {
            mongo = new Mongo(ip);
        }
        return mongo;
    }

    public static void dropPipeline(Mongo mongo, String dbname)
    {
        DB db = mongo.getDB(dbname);
        if (db!=null)
        {
            db.dropDatabase();
            System.out.println("drop database " + dbname);
        } else
        {
            System.out.println("database does not exist, can not drop database " + dbname);
        }
    }

    public static void dropCollection(Mongo mongo, String dbname, String collectionname)
    {
        DB db = mongo.getDB(dbname);
        if (db!=null)
        {
            DBCollection collection = db.getCollection(collectionname);
            if (collection!=null)
            {
                collection.drop();
                System.out.println("drop collection " + dbname + "." + collectionname);
            } else
            {
                System.out.println("collection does not exist, can not drop collection" + dbname + "." + collectionname);
            }
        }
    }

    public static void closeMongo()
    {
        if (mongo != null)
        {
            mongo.close();
            mongo = null;
        }
    }
}