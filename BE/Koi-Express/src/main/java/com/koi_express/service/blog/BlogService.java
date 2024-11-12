package com.koi_express.service.blog;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.koi_express.entity.promotion.Blog;
import com.koi_express.enums.BlogStatus;
import com.koi_express.exception.ResourceNotFoundException;
import com.koi_express.repository.BlogRepository;
import com.koi_express.service.verification.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlogService {

    private final BlogRepository blogRepository;
    private final S3Service s3Service;

    public Blog createBlog(String title, String content, MultipartFile imageFile) {

        Blog blog = new Blog();
        blog.setTitle(title);
        blog.setContent(content);
        blog.setStatus(BlogStatus.DRAFT);
        blog.setCreatedAt(LocalDateTime.now());

        String baseSlug = title.toLowerCase().replaceAll("[^a-z0-9\\s]", "").replace(" ", "-");
        String uniqueSlug = baseSlug + "-" + System.currentTimeMillis();
        blog.setSlug(uniqueSlug);

        String date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        if (imageFile != null) {
            try {
                String imageUrl = s3Service.uploadImage("blog", date, title, imageFile);
                log.info("Image URL: {}", imageUrl);
                blog.setImageUrl(imageUrl);
                if (imageUrl == null) {
                    log.warn("Image uploaded to S3 but returned a null URL");
                }
            } catch (Exception e) {
                log.error("Failed to upload image to S3", e);
                throw new RuntimeException("Failed to upload image to S3", e);
            }
        }

        return blogRepository.save(blog);
    }

    public Optional<Blog> getBlogById(Long id) {
        return blogRepository.findById(id);
    }

    public Blog approveBlog(Long blogId) {
        Blog blog = blogRepository.findById(blogId).orElseThrow(() -> new ResourceNotFoundException("Blog not found"));

        blog.setStatus(BlogStatus.PUBLISHED);
        return blogRepository.save(blog);
    }

    public List<Blog> getBlogsByStatus(BlogStatus status) {
        return blogRepository.getAllByStatus(status);
    }

    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }

    public Optional<Blog> getBlogBySlug(String slug) {
        return blogRepository.findBySlug(slug);
    }
}
