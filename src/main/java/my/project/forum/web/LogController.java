package my.project.forum.web;

import my.project.forum.data.mongodb.entity.Log;
import my.project.forum.data.mongodb.repository.LogRepository;
import my.project.forum.error.ItemNotFoundException;
import my.project.forum.service.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/log")
public class LogController {

    private LogRepository logRepo;
    private Properties props;

    @Autowired
    public LogController(LogRepository logRepo, Properties props)
    {
        this.logRepo = logRepo;
        this.props = props;
    }

    @GetMapping(produces = "application/json")
    public Page<Log> getLogs(@RequestParam(value = "page", defaultValue = "0") int page)
    {
        Pageable pageable = PageRequest.of(page, props.getLogPageSize());
        return logRepo.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Log getLog(@PathVariable String id)
    {
        return logRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Log with id " + id + " doesn't exist"));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteLog(@PathVariable String id) {
        logRepo.deleteById(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/clear")
    public void deleteAllLogs() {
        logRepo.deleteAll();
    }
}
