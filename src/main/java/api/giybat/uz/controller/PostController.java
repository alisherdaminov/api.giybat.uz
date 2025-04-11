package api.giybat.uz.controller;

import api.giybat.uz.dto.post.PostCreatedDTO;
import api.giybat.uz.dto.post.PostDTO;
import api.giybat.uz.dto.post.PostFilter;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.services.post.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
@Tag(name = "PostController", description = "Api's for creating new post")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/create")
    public ResponseEntity<PostDTO> createPost(@Valid @RequestBody PostCreatedDTO dto) {
        return ResponseEntity.ok(postService.createPost(dto));
    }

    @PostMapping("/public/filter")
    @Operation(summary = "postFilter", description = "public post filter for user")
    public ResponseEntity<PageImpl<PostDTO>> postFilter(@Valid @RequestBody PostFilter dto,
                                                        @RequestParam(value = "page", defaultValue = "1") int page,
                                                        @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.postFilter(dto, page - 1, size));
    }

    @GetMapping("/all-lists")
    public ResponseEntity<List<PostDTO>> createPost() {
        return ResponseEntity.ok(postService.getProfilePostList());
    }

    @GetMapping("/get-profile-post-by-id/{id}")
    public ResponseEntity<PostDTO> getProfilePostById(@PathVariable("id") String id) {
        return ResponseEntity.ok(postService.getProfilePostById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PostDTO> update(@PathVariable("id") String id,
                                          @Valid @RequestBody PostCreatedDTO dto,
                                          AppLanguage language) {
        return ResponseEntity.ok(postService.update(id, dto, language));
    }

    @DeleteMapping("/delete-by-visible-false/{id}")
    public ResponseEntity<Boolean> deleteByVisibleFalse(@PathVariable("id") String id, AppLanguage language) {
        return ResponseEntity.ok(postService.deleteByVisibleFalse(id, language));
    }


}
