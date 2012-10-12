package com.cross.util.crawler;

import java.util.Iterator;
import java.util.LinkedHashMap;
import org.apache.log4j.Logger;

/**
 *
 * @author xiaodong.shen
 */
public class Link {

    public static Logger logger = Logger.getLogger(Link.class);
    private Link parent = null;
    private int depth = 0;
    private String scheme = null;
    private String user = null;
    private String pass = null;
    private String host = null;
    private String port = null;
    private String path = null;
    private String fragment = null;
    private LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();

    public Link() {
    }

    public Link(Link parent, int depth, String scheme, String host, String port, LinkedHashMap<String, Object> params, String path) {
        this(parent, depth, scheme, null, null, host, port, params, path, null);
    }

    public Link(Link parent, int depth, String scheme, String user, String pass, String host, String port, LinkedHashMap<String, Object> params, String path, String fragment) {
        this.setParent(parent);
        this.setDepth(depth);
        this.setScheme(scheme);
        this.setUser(user);
        this.setPass(pass);
        this.setHost(host);
        this.setPort(port);
        this.setPath(path);
        this.setParams(params);
        this.setFragment(fragment);
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public LinkedHashMap<String, Object> getParams() {
        return params;
    }

    public void setParams(LinkedHashMap<String, Object> params) {
        this.params = params;
    }

    public Link getParent() {
        return parent;
    }

    public void setParent(Link parent) {
        this.parent = parent;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getFragment() {
        return fragment;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(scheme + "://");
        if (user != null) {
            sb.append(user);
            if (pass != null) {
                sb.append(":" + pass + "@");
            }
        }
        sb.append(host);
        if (port != null) {
            sb.append(":" + port);
        }
        if (path != null) {
            sb.append(path);
        }
        if (params != null && !params.isEmpty()) {
            sb.append("?");
            Iterator iter = params.keySet().iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                Object value = params.get(key);
                if (value!=null)
                {
                    sb.append(key + "=" + value.toString());
                } else
                {
                    sb.append(key);
                }
                if (iter.hasNext())
                {
                    sb.append("&");
                }
            }
            if (fragment != null) {
                sb.append("#" + fragment);
            }
        }

        return sb.toString();
    }

    public void fromString(String url) throws InvalidUrlException {
        parsePathParamsFragment(parseUserPassHostPort(parseScheme(url)));
    }

    private String parseScheme(String url) throws InvalidUrlException {
        String afterscheme = null;
        try {
            scheme = url.substring(0, url.indexOf("://"));
            afterscheme = url.substring(url.indexOf("://") + 3);
        } catch (Exception e) {
            throw new InvalidUrlException(e);
        }
        return afterscheme;
    }

    private String parseUserPassHostPort(String afterscheme) throws InvalidUrlException {
        String remainurl = null;
        try {
            int i = afterscheme.indexOf("/");
            remainurl = ((i > 0) ? afterscheme.substring(i) : null);
            String s = afterscheme;
            if (i > 0) {
                s = afterscheme.substring(0, i);
            }
            String userpassword = null;
            String hostport = s;
            if (s.contains("@")) {
                userpassword = s.substring(0, s.indexOf("@"));
                hostport = s.substring(s.indexOf("@") + 1);
            }
            parseUserPassword(userpassword);
            parseHostPort(hostport);
        } catch (Exception e) {
            throw new InvalidUrlException(e);
        }

        return remainurl;
    }

    private void parseUserPassword(String userpassword) throws InvalidUrlException {
        if (userpassword != null) {
            if (userpassword.contains(":")) {
                user = userpassword.substring(0, userpassword.indexOf(":"));
                pass = userpassword.substring(userpassword.indexOf(":") + 1);
            } else {
                user = userpassword;
            }
        }
    }

    private void parseHostPort(String hostport) throws InvalidUrlException {
        if (hostport != null) {
            if (hostport.contains(":")) {
                host = hostport.substring(0, hostport.indexOf(":"));
                port = hostport.substring(hostport.indexOf(":") + 1);
                try {
                    Integer.valueOf(port);
                } catch (NumberFormatException numberFormatException) {
                    throw new InvalidUrlException(numberFormatException);
                }
            } else {
                host = hostport;
            }
        }
    }

    private void parsePathParamsFragment(String pathparamsfragment) throws InvalidUrlException {
        if (pathparamsfragment != null) {
            int i = pathparamsfragment.indexOf("?");
            if (i>0)
            {
                path = pathparamsfragment.substring(0, i);
                parseParamsFragment(pathparamsfragment.substring(i+1));
            } else
            {
                path = pathparamsfragment;
            }
        }
    }

    private void parseParamsFragment(String paramsfragment) throws InvalidUrlException {
        if (paramsfragment != null) {
            int i = paramsfragment.lastIndexOf("#");
            String paramsstring = paramsfragment;
            if (i > (paramsfragment.lastIndexOf("&"))) {
                fragment = paramsfragment.substring(i + 1);
                paramsstring = paramsfragment.substring(0, i);
            }
            parseParams(paramsstring);
        }
    }

    private void parseParams(String paramsstring) throws InvalidUrlException
    {
        if (paramsstring!=null)
        {
            // here is a simple implementation which I assume a param value can not contain '&amp;'
            while (!paramsstring.equals(""))
            {
                int i = paramsstring.indexOf("&");
                String keyvalue = paramsstring;
                if (i>0)
                {
                    keyvalue = paramsstring.substring(0, i);
                }
                int j = keyvalue.indexOf("=");
                if (j>0)
                {
                    params.put(keyvalue.substring(0, j), keyvalue.substring(j+1));
                } else
                {
                    params.put(keyvalue.substring(0, j), null);
                }
                paramsstring = ((i>0)?paramsstring.substring(i+1):"");
            }
        }
    }
}