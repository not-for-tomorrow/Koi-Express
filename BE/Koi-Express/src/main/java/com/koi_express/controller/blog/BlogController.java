package com.koi_express.controller.blog;

import java.util.List;

import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.promotion.Blog;
import com.koi_express.enums.BlogStatus;
import com.koi_express.service.blog.BlogService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogController {

    private static final Logger logger = LoggerFactory.getLogger(BlogController.class);

    private final BlogService blogService;

    @PostMapping("/create-blog")
    @PreAuthorize("hasRole('SALES_STAFF')")
    public ResponseEntity<ApiResponse<Blog>> createBlog(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("status") BlogStatus status,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "documentFile", required = false) MultipartFile documentFile) {

        try {
            Blog blog = blogService.createBlog(title, content, status, imageFile, documentFile);
            logger.info("Blog created successfully with title: {}", title);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(blog));
        } catch (Exception e) {
            logger.error("Error creating blog: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to create blog", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Blog>> getBlogById(@PathVariable Long id) {
        return blogService.getBlogById(id)
                .map(blog -> ResponseEntity.ok(ApiResponse.success(blog)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "Blog not found", null)));
    }

    @PutMapping("/approve/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<Blog>> approveBlog(@PathVariable Long id) {
        try {
            Blog approvedBlog = blogService.approveBlog(id);
            logger.info("Blog approved with ID: {}", id);
            return ResponseEntity.ok(ApiResponse.success(approvedBlog));
        } catch (Exception e) {
            logger.error("Error approving blog: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to approve blog", e.getMessage()));
        }
    }

    @GetMapping("/all-blogs/{status}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<Blog>>> getAllBlogs(@PathVariable BlogStatus status) {
        List<Blog> blogs = blogService.getBlogsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(blogs));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<Blog>> getBlogBySlug(@PathVariable String slug) {
        return blogService.getBlogBySlug(slug)
                .map(blog -> ResponseEntity.ok(ApiResponse.success(blog)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "Blog not found", null)));
    }

    @GetMapping("/latest")
    public ResponseEntity<List<Blog>> getLatestBlogs() {
        List<Blog> latestBlogs = blogService.getAllBlog();
        return ResponseEntity.ok(latestBlogs);
    }
}
