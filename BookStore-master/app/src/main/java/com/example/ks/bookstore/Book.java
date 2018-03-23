package com.example.ks.bookstore;



public class Book implements Cloneable{
    private long id=-1;
    private String name;
    private String author;
    private String serverId=null;
    private String tag;
    private String userId;


    public Book(long id, String name, String author, String tag, String serverId, String userId) {
        this.id=id;
        this.name = name;
        this.author = author;
        this.serverId = serverId;
        this.tag=tag;
        this.userId = userId;
    }

    public Book(String name, String author, String tag, String userId) {
        this.name = name;
        this.author = author;
        this.tag=tag;
        this.userId = userId;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getServerId() {
        return serverId;
    }

    public String getTag() {
        return tag;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUserId() {
        return userId;
    }
}
