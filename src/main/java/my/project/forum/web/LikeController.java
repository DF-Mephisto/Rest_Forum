package my.project.forum.web;

import my.project.forum.aop.annotation.Loggable;
import my.project.forum.data.postgres.dto.LikeDto;
import my.project.forum.data.postgres.entity.Like;
import my.project.forum.data.postgres.entity.User;
import my.project.forum.data.postgres.repository.LikeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @Loggable(method = "get", controller = "like")
    public Iterable<Like> getLikes()
    {
        return likeRepo.findAll();
    }

    @PostMapping
    @Loggable(method = "post", controller = "like")
    public Like newLike(@Valid @RequestBody LikeDto likeDto,
                        @AuthenticationPrincipal User user) {
        Like like = likeDtoToLike(likeDto);

        like.setUser(user);
        return likeRepo.save(like);
    }

    private Like likeDtoToLike(LikeDto likeDto)
    {
        ModelMapper modelMapper = new ModelMapper();

        return modelMapper.map(likeDto, Like.class);
    }
}
