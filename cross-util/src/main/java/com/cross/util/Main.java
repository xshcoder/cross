package com.cross.util;

import com.cross.util.crawler.InvalidUrlException;
import com.cross.util.crawler.Link;
import com.cross.util.crawler.SimpleWebCrawler;
import com.cross.util.common.StringUtil;
import com.cross.util.crawler.WebCrawlerException;
import com.cross.util.crawler.WebPage;
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
    public static final String MONGO = "mongo";
    public static final String FILE = "file";

    @SuppressWarnings("static-access")
    private static Option getCrawlOption() {
        return OptionBuilder.withLongOpt(CRAWL).hasArg().withDescription("crawl url").create(CRAWL);
    }

    @SuppressWarnings("static-access")
    private static Option getCharsetOption() {
        return OptionBuilder.withLongOpt(CRAWL_CHARSET).hasArg().withDescription("crawl charset").create(CRAWL_CHARSET);
    }

    @SuppressWarnings("static-access")
    private static Option getMongoOption() {
        return OptionBuilder.withLongOpt(MONGO).hasArg().withDescription("mongo db").create(MONGO);
    }

    @SuppressWarnings("static-access")
    private static Option getFileOption() {
        return OptionBuilder.withLongOpt(FILE).hasArg().withDescription("file").create(FILE);
    }

    private static Options getAllOptions() {
        Options options = new Options();
        options.addOption(getCrawlOption());
        options.addOption(getCharsetOption());
        options.addOption(getMongoOption());
        options.addOption(getFileOption());
        return options;
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("Help: java -jar cross-util-with-dependencies.jar -h");
        System.out.println("Crawl url: java -jar cross-util-with-dependencies.jar -crawl url -charset charset");
    }

    private static void crawlUrl(CommandLine cmd)
    {
        String charset = "utf-8";
        String url = cmd.getOptionValue(CRAWL);
        if (cmd.hasOption(CRAWL_CHARSET))
        {
            charset = cmd.getOptionValue(CRAWL_CHARSET);
        }
        crawlUrl(cmd.getOptionValue(CRAWL), charset);
    }

    private static void crawlUrl(String url, String charset)
    {
        try {
            SimpleWebCrawler crawler = new SimpleWebCrawler();
            Link link = new Link();
            link.fromString(url);
            System.out.println("crawl url: " + link.toString());
            WebPage page = crawler.crawl(link);
            String content = StringUtil.convert(page.getBytes(), charset);
            if (content != null)
            {
                System.out.println(content);
            } else
            {
                System.out.println("content is null");
            }
        } catch (WebCrawlerException webCrawlerException) {
            System.out.println(webCrawlerException.getMessage());
        } catch (InvalidUrlException invalidUrlException) {
            System.out.println(invalidUrlException.getMessage());
        }
    }
    
    public static void main(String[] args)
    {
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
        } else {
            printUsage();
        }
    }
}