package com.cross.util.common;

import java.util.HashMap;

/**
 *
 * @author xiaodong.shen
 */
public class MimeMapper {

    public static final String HTML = "text/html";
    public static final String WORD = "application/word";
    public static final String PDF = "application/pdf";

    public static final HashMap<String, String> mimemap = new HashMap<String, String>();

    static
    {
        mimemap.put(".html", HTML);
        mimemap.put(".htm", HTML);
        mimemap.put(".doc", WORD);
        mimemap.put(".docx", WORD);
        mimemap.put(".pdf", PDF);
    }

    public static String getMimetype(String extension)
    {
        return mimemap.get(extension);
    }
}
