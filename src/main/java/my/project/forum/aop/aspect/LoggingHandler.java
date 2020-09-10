package my.project.forum.aop.aspect;

import my.project.forum.aop.annotation.Loggable;
import my.project.forum.data.builder.entity.LogBuilder;
import my.project.forum.data.mongodb.entity.Log;
import my.project.forum.data.mongodb.repository.LogRepository;
import my.project.forum.data.postgres.entity.User;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingHandler {

    private LogRepository logRepo;

    @Autowired
    LoggingHandler(LogRepository logRepo)
    {
        this.logRepo = logRepo;
    }

    @Pointcut("@annotation(my.project.forum.aop.annotation.Loggable)")
    public void loggableMethod(){}

    @Before("loggableMethod()")
    public void Logging(JoinPoint jp){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username = principal instanceof User ? ((User) principal).getUsername() : "null";
        String method = ((MethodSignature) jp.getSignature())
                .getMethod().getAnnotation(Loggable.class).method();

        String controller = ((MethodSignature) jp.getSignature())
                .getMethod().getAnnotation(Loggable.class).controller();

        String content = method + " method was activated in " + controller + " controller";
        Log log = new LogBuilder().id(null).username(username).desc(content).build();
        logRepo.save(log);
    }
}
