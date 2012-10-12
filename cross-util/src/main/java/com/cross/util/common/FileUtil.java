package com.cross.util.common;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 *
 * @author xiaodong.shen
 */
public class FileUtil {
    private static final String UTF8 = "UTF-8";

    public static byte[] readFileToBytes(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        long length = file.length();

        if (length > Integer.MAX_VALUE) {
            throw new IOException("file is too large");
        }
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }

    public static String readTextFile(File file) throws IOException
    {
        byte[] bytes = readFileToBytes(file);
        return StringUtil.convert(bytes, UTF8);
    }

    public static void saveBytesToTextFile(File file, byte[] bytes, String charset) throws IOException {
        String content = StringUtil.convert(bytes, charset);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), UTF8));
        out.write(content);
        out.flush();
        out.close();
    }

    public static void saveBinaryFile(File file, byte[] bytes, String charset) throws IOException {
        OutputStream output = new BufferedOutputStream(new FileOutputStream(file));
        output.write(bytes);
        output.close();
    }
    
    public static File createFile(String filepath) throws IOException
    {
        File file = new File(filepath);
        if (!file.exists())
        {
            File parentfile = file.getParentFile();
            if (!parentfile.exists())
                parentfile.mkdirs();
            file.createNewFile();
        }
        return file;
    }
}