package my.project.forum.error;

public class ItemAlreadyExistsException extends RuntimeException {

    public ItemAlreadyExistsException()
    {

    }

    public ItemAlreadyExistsException(String desc)
    {
        super(desc);
    }

}
