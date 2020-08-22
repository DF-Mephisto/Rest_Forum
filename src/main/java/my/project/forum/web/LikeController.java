package my.project.forum.web;

import my.project.forum.entity.Like;
import my.project.forum.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/likes")
public class LikeController {

    private LikeRepository likeRepo;

    @Autowired
    public LikeController(LikeRepository likeRepo)
    {
        this.likeRepo = likeRepo;
    }

    @GetMapping(produces = "application/json")
    public Iterable<Like> getLikes()
    {
        return likeRepo.findAll();
    }

    @PostMapping
    public void newLike(@Valid @RequestBody Like like) {

        likeRepo.save(like);
    }
}
