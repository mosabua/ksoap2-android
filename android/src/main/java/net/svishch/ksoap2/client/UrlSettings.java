package net.svishch.ksoap2.client;

public class UrlSettings {
    String url = "";
    String user = "";
    String password = "";
    boolean debug = false;

    public UrlSettings setUrl(String url) {
        this.url = url;
        return this;
    }

    public UrlSettings setUser(String user) {
        this.user = user;
        return this;
    }

    public UrlSettings setPassword(String password) {
        this.password = password;
        return this;
    }

    public UrlSettings setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public boolean isDebug() {
        return debug;
    }
}
