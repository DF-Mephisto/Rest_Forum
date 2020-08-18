package my.project.forum.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "forum")
public class Properties {
    private int sectionsPageSize;
    private int topicsPageSize;
    private int commentsPageSize;

    public void setSectionsPageSize(int sectionsPageSize)
    {
        this.sectionsPageSize = sectionsPageSize;
    }
    public void setTopicsPageSize(int topicsPageSize)
    {
        this.topicsPageSize = topicsPageSize;
    }
    public void setCommentsPageSize(int commentsPageSize)
    {
        this.commentsPageSize = commentsPageSize;
    }
}
