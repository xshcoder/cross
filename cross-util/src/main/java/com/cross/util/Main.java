package com.cross.util;

import com.cross.util.common.FileUtil;
import com.cross.util.common.MimeMapper;
import com.cross.util.common.StringUtil;
import com.cross.util.crawler.InvalidUrlException;
import com.cross.util.crawler.Link;
import com.cross.util.crawler.SimpleWebCrawler;
import com.cross.util.crawler.WebCrawlerException;
import com.cross.util.crawler.WebPage;
import com.cross.util.mongo.MongoAccessException;
import com.cross.util.mongo.MongoUtil;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;

/**
 *
 * @author xiaodong.shen
 */
public class Main {

    public static final String CRAWL_CHARSET = "charset";
    public static final String CRAWL = "crawl";
    public static final String URL = "url";
    public static final String FILE = "file";
    public static final String MONGO = "mongo";
    public static final String MONGO_HOST = "host";
    public static final String MONGO_PORT = "port";
    public static final String MONGO_DB = "db";
    public static final String MONGO_COLLECTION = "collection";

    @SuppressWarnings("static-access")
    private static Option getCrawlOption() {
        return OptionBuilder.withLongOpt(CRAWL).withDescription("crawl mode").create(CRAWL);
    }

    @SuppressWarnings("static-access")
    private static Option getUrlOption() {
        return OptionBuilder.withLongOpt(URL).hasArg().withDescription("crawl url").create(URL);
    }

    @SuppressWarnings("static-access")
    private static Option getCharsetOption() {
        return OptionBuilder.withLongOpt(CRAWL_CHARSET).hasArg().withDescription("crawl charset").create(CRAWL_CHARSET);
    }

    @SuppressWarnings("static-access")
    private static Option getFileOption() {
        return OptionBuilder.withLongOpt(FILE).hasArg().withDescription("file").create(FILE);
    }

    @SuppressWarnings("static-access")
    private static Option getMongoOption() {
        return OptionBuilder.withLongOpt(MONGO).withDescription("mongo mode").create(MONGO);
    }

    @SuppressWarnings("static-access")
    private static Option getMongoHostOption() {
        return OptionBuilder.withLongOpt(MONGO_HOST).hasArg().withDescription("mongo db host address").create(MONGO_HOST);
    }

    @SuppressWarnings("static-access")
    private static Option getMongoPortOption() {
        return OptionBuilder.withLongOpt(MONGO_PORT).hasArg().withDescription("mongo db port number").create(MONGO_PORT);
    }

    @SuppressWarnings("static-access")
    private static Option getMongoDBOption() {
        return OptionBuilder.withLongOpt(MONGO_DB).hasArg().withDescription("mongo db database name").create(MONGO_DB);
    }

    @SuppressWarnings("static-access")
    private static Option getMongoCollectionOption() {
        return OptionBuilder.withLongOpt(MONGO_COLLECTION).hasArg().withDescription("mongo db collection name").create(MONGO_COLLECTION);
    }

    private static Options getAllOptions() {
        Options options = new Options();
        options.addOption(getCrawlOption());
        options.addOption(getUrlOption());
        options.addOption(getCharsetOption());
        options.addOption(getFileOption());
        options.addOption(getMongoOption());
        options.addOption(getMongoHostOption());
        options.addOption(getMongoPortOption());
        options.addOption(getMongoDBOption());
        options.addOption(getMongoCollectionOption());
        return options;
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("Help: java -jar cross-util-with-dependencies.jar -h");
        System.out.println("Crawl url: java -jar cross-util-with-dependencies.jar -crawl -url url -charset charset -file filepath");
        System.out.println("Save to MongoDB: java -jar cross-util-with-dependencies.jar -mongo -host host -port port -db db -collection collection -charset charset -file filepath");
    }

    private static void crawlUrl(CommandLine cmd) {
        String charset = "utf-8";
        String url = cmd.getOptionValue(URL);
        if (cmd.hasOption(CRAWL_CHARSET)) {
            charset = cmd.getOptionValue(CRAWL_CHARSET);
        }
        String file = cmd.getOptionValue(FILE);
        crawlUrl(url, charset, file);
    }

    private static void crawlUrl(String url, String charset, String file) {
        try {
            SimpleWebCrawler crawler = new SimpleWebCrawler();
            Link link = new Link();
            link.fromString(url);
            System.out.println("crawl url: " + link.toString());
            WebPage page = crawler.crawl(link);
            if (file == null) {
                String content = StringUtil.convert(page.getBytes(), charset);
                if (content != null) {
                    System.out.println(content);
                } else {
                    System.out.println("content is null");
                }
            } else {
                File f = FileUtil.createFile(file);
                FileUtil.saveBinaryFile(f, page.getBytes());
            }
        } catch (WebCrawlerException webCrawlerException) {
            System.out.println(webCrawlerException.getMessage());
        } catch (InvalidUrlException invalidUrlException) {
            System.out.println(invalidUrlException.getMessage());
        } catch (IOException ioException) {
            System.out.println(ioException.getMessage());
        }
    }

    private static void saveMongoDocument(CommandLine cmd) {
        saveMongoDocument(cmd.getOptionValue(MONGO_HOST), cmd.getOptionValue(MONGO_PORT), cmd.getOptionValue(URL), cmd.getOptionValue(CRAWL_CHARSET), cmd.getOptionValue(FILE), cmd.getOptionValue(MONGO_DB), cmd.getOptionValue(MONGO_COLLECTION));
    }

    private static void saveMongoDocument(String host, String port, String url, String charset, String filepath, String db, String collection) {
        try {
            File file = FileUtil.getFile(filepath);
            if (file != null) {
                Mongo mongo = null;
                if (port != null) {
                    int portnumber = Integer.valueOf(port).intValue();
                    mongo = MongoUtil.getMongoInstance(host, portnumber);
                } else {
                    mongo = MongoUtil.getMongoInstance(host);
                }
                Link link = new Link();
                link.fromString(url);
                WebPage page = new WebPage();
                page.setLink(link);
                page.setCharset(charset);
                page.setMimetype(MimeMapper.getMimetype(FileUtil.getExtension(file.getName())));
                page.setBytes(FileUtil.readFileToBytes(file));
                DBCollection col = MongoUtil.getCollection(mongo, db, collection);
                MongoUtil.saveDocument(page, col);
            } else {
                System.out.println("File " + filepath + " does not exist.");
            }
        } catch (UnknownHostException unknownHostException) {
            System.out.println(unknownHostException.getMessage());
        } catch (InvalidUrlException invalidUrlException) {
            System.out.println(invalidUrlException.getMessage());
        } catch (IOException ioException) {
            System.out.println(ioException.getMessage());
        } catch (MongoAccessException mongoAccessException)
        {
            System.out.println(mongoAccessException.getMessage());
        }
    }

    public static void main(String[] args) {
        //args = initCrawlTestParameters();
        //args = initMongoTestParameters();
        Options options = getAllOptions();
        CommandLineParser parser = new GnuParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args, false);
        } catch (UnrecognizedOptionException e) {
            System.out.println(e.getMessage());
            printUsage();
            return;
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
            printUsage();
        }
        if (cmd.hasOption("h")) {
            printUsage();
            return;
        }
        if (cmd.hasOption(CRAWL)) {
            crawlUrl(cmd);
        } else if (cmd.hasOption(MONGO)) {
            saveMongoDocument(cmd);
        } else {
            printUsage();
        }
    }
    
    private static String[] initCrawlTestParameters()
    {
        String[] args = new String[7];
        args[0] = "-crawl";
        args[1] = "-url";
        args[2] = "http://www.jshongda.com/";
        args[3] = "-charset";
        args[4] = "gb2312";
        args[5] = "-file";
        args[6] = "c:\\Documents and Settings\\Ejer\\Dokumenter\\NetBeansProjects\\cross\\cross-util\\jshongda.html";
        return args;
    }
    
    private static String[] initMongoTestParameters()
    {
        String[] args = new String[15];
        args[0] = "-mongo";
        args[1] = "-url";
        args[2] = "http://www.jshongda.com/";
        args[3] = "-charset";
        args[4] = "gb2312";
        args[5] = "-file";
        args[6] = "c:\\Documents and Settings\\Ejer\\Dokumenter\\NetBeansProjects\\cross\\cross-util\\jshongda.html";
        args[7] = "-host";
        args[8] = "127.0.0.1";
        args[9] = "-port";
        args[10] = "27017";
        args[11] = "-db";
        args[12] = "test";
        args[13] = "-collection";
        args[14] = "testcol";
        return args;
    }
}