package com.cross.util.crawler;

/**
 *
 * @author xiaodong.shen
 */
public interface IWebCrawler {

    public WebPage crawl(Link link) throws WebCrawlerException;
}