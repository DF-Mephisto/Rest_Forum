package my.project.forum.error;

public class ActionNotAllowed extends RuntimeException {

    public ActionNotAllowed()
    {

    }

    public ActionNotAllowed(String desc)
    {
        super(desc);
    }

}
