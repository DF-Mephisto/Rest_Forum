package my.project.forum.data.builder.entity;

import my.project.forum.data.mongodb.entity.Log;

public class LogBuilder {

    private String id = "0";
    private String username = "test_user";
    private String desc = "simple log";

    public LogBuilder id(String id)
    {
        this.id = id;
        return this;
    }

    public LogBuilder username(String username)
    {
        this.username = username;
        return this;
    }

    public LogBuilder desc(String desc)
    {
        this.desc = desc;
        return this;
    }

    public Log build()
    {
        return new Log(id, username, desc);
    }
}
