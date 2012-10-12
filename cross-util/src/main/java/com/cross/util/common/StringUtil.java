package com.cross.util.common;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author xiaodong.shen
 */
public class StringUtil {
    public static String convert(byte[] bytes, String charset)
    {
        if (bytes!=null)
        {
            try
            {
                return new String(bytes, charset);
            } catch (UnsupportedEncodingException uee)
            {
                System.out.println("StrinUtil.convert() failed: " + uee.getMessage());
            }
        }
        return null;
    }
}