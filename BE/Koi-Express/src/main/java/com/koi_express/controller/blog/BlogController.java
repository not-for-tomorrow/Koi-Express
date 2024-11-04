package com.koi_express.controller.blog;

import com.koi_express.entity.promotion.Blog;
import com.koi_express.enums.BlogStatus;
import com.koi_express.service.blog.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @PostMapping("/create-blog")
    @PreAuthorize("hasRole('SALES_STAFF')") // Assuming only managers can create blogs
    public ResponseEntity<Blog> createBlog(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("status") BlogStatus status,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "documentFile", required = false) MultipartFile documentFile) {

        try {
            Blog blog = blogService.createBlog(title, content, status, imageFile, documentFile);
            return ResponseEntity.status(HttpStatus.CREATED).body(blog);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
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

    @GetMapping("/{slug}")
    public ResponseEntity<Blog> getBlogBySlug(@PathVariable String slug) {
        return blogService.getBlogBySlug(slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
