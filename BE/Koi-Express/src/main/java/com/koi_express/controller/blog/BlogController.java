package com.koi_express.controller.blog;

import com.koi_express.entity.promotion.Blog;
import com.koi_express.enums.BlogStatus;
import com.koi_express.service.blog.BlogService;
import com.koi_express.service.verification.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;
    private final S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = s3Service.uploadFile("blogs", LocalDate.now().toString(), "images", file);
        return ResponseEntity.ok(imageUrl);
    }

    @PostMapping
    public ResponseEntity<Blog> createBlog(@RequestBody Blog blog) {
        return ResponseEntity.ok(blogService.createBlog(blog));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Blog> getBlogById(@PathVariable Long id) {
        return blogService.getBlogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/approve/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Blog> approveBlog(@PathVariable Long id) {
        return ResponseEntity.ok(blogService.approveBlog(id));
    }

    @GetMapping("/all-blogs/{status}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<Blog>> getAllBlogs(@PathVariable BlogStatus status) {
        return ResponseEntity.ok(blogService.getBlogsByStatus(status));
    }

}
