package my.project.forum.web;

import my.project.forum.dto.LikeDto;
import my.project.forum.entity.Like;
import my.project.forum.entity.User;
import my.project.forum.repository.LikeRepository;
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
    public Iterable<Like> getLikes()
    {
        return likeRepo.findAll();
    }

    @PostMapping
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
