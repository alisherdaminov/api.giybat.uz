package api.giybat.uz.controller;

import api.giybat.uz.dto.PostDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/post")
public class PostController {

    @PostMapping("/create")
    public String create(@RequestBody PostDTO postDTO) {
        return "post created";
    }
}
