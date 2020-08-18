package my.project.forum.error;

public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException()
    {

    }

    public ItemNotFoundException(String desc)
    {
        super(desc);
    }

}
