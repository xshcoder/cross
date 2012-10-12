package com.cross.util.crawler;

import java.io.IOException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NoHttpResponseException;
import org.apache.commons.httpclient.StatusLine;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

/**
 *
 * @author xiaodong.shen
 */
public class SimpleWebCrawler implements IWebCrawler {

    private int retries = 3;

    public SimpleWebCrawler() {
    }

    public WebPage crawl(Link link) throws WebCrawlerException {
        WebPage crawlpage = new WebPage();
        crawlpage.setLink(link);
        HttpClient httpclient = new HttpClient();
        GetMethod get = new GetMethod(link.toString());
        get.setFollowRedirects(true);
        HttpMethodRetryHandler myretryhandler = new HttpMethodRetryHandler() {

            public boolean retryMethod(
                    final HttpMethod method,
                    final IOException exception,
                    int executionCount) {
                if (executionCount >= retries) {
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {
                    System.out.println("retry " + executionCount + " times");
                    return true;
                }
                if (!method.isRequestSent()) {
                    System.out.println("retry " + executionCount + " times");
                    return true;
                }
                return false;
            }
        };
        get.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, myretryhandler);
        try {
            int status = httpclient.executeMethod(get);
            StatusLine statusline = get.getStatusLine();
            byte[] bytes = get.getResponseBody();
            if (status != HttpStatus.SC_OK) {
                System.out.println("status ERROR: " + statusline.toString());
            } else {
                crawlpage.setBytes(bytes);
                System.out.println("status OK:" + statusline.toString());
            }
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        } finally {
            get.releaseConnection();
        }
        return crawlpage;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }
}