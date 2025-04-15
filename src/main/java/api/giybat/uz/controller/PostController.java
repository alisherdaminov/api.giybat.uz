package api.giybat.uz.controller;

import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.post.*;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.services.post.PostService;
import api.giybat.uz.util.PageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/post")
@Tag(name = "PostController", description = "Api's for creating new post")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/api/v1/create")
    public ResponseEntity<PostDTO> createPost(@Valid @RequestBody PostCreatedDTO dto) {
        return ResponseEntity.ok(postService.createPost(dto));
    }

    @Operation(summary = "get Similar Post List", description = "get similar post list for user")
    @PostMapping("/similar-post")
    public ResponseEntity<List<PostDTO>> getSimilarPostList(@Valid @RequestBody SimilarPostListDTO dto) {
        return ResponseEntity.ok(postService.getSimilarPostList(dto));
    }

    @PostMapping("/public/filter")
    @Operation(summary = "postFilter", description = "public POST list for user by given pagination 1..12")
    public ResponseEntity<PageImpl<PostDTO>> postPublicFilter(@Valid @RequestBody PostPublicFilterDTO dto,
                                                              @RequestParam(value = "page", defaultValue = "1") int page,
                                                              @RequestParam(value = "size", defaultValue = "12") int size) {
        //  return ResponseEntity.ok(postService.postFilter(dto, page - 1, size));
        return ResponseEntity.ok(postService.postPublicFilter(dto, PageUtil.page(page), size));
    }

    @Operation(summary = "adminFilter", description = "adminFilter post list for admin")
    @PostMapping("/admin/filter")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PageImpl<PostDTO>> postAdminFilter(@RequestBody PostAdminFilterDTO dto,
                                                             @RequestParam(value = "page", defaultValue = "1") int page,
                                                             @RequestParam(value = "size", defaultValue = "12") int size) {
        return ResponseEntity.ok(postService.postAdminFilter(dto, PageUtil.page(page), size));
    }


    @Operation(summary = "getAllPostList", description = "public GET list for user by given pagination 1..12")
    @GetMapping("/all-lists")
    public ResponseEntity<PageImpl<PostDTO>> createPost(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "12") int size) {
        return ResponseEntity.ok(postService.getProfilePostList(PageUtil.page(page), size));
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

    @DeleteMapping("/deleteBy/admin/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AppResponse<String>> deleteByVisibleFalse(@PathVariable("id") String id, AppLanguage language) {
        return ResponseEntity.ok(postService.deleteByVisibleFalse(id, language));
    }


}
