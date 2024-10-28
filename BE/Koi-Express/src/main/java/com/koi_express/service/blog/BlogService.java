package com.koi_express.service.blog;

import com.koi_express.entity.promotion.Blog;
import com.koi_express.enums.BlogStatus;
import com.koi_express.exception.ResourceNotFoundException;
import com.koi_express.repository.BlogRepository;
import com.koi_express.service.verification.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final S3Service s3Service;

    public Blog createBlog(Blog blog) {
        String baseSlug = blog.getTitle().toLowerCase().replace(" ", "-");
        String uniqueSlug = baseSlug + "-" + System.currentTimeMillis();
        blog.setSlug(uniqueSlug);

        blog.setStatus(BlogStatus.DRAFT);
        return blogRepository.save(blog);
    }

    public Optional<Blog> getBlogById(Long id) {
        return blogRepository.findById(id);
    }

    public Blog approveBlog(Long blogId) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found"));

        blog.setStatus(BlogStatus.PUBLISHED);
        return blogRepository.save(blog);
    }

    public List<Blog> getBlogsByStatus(BlogStatus status) {
        return blogRepository.getAllByStatus(status);
    }

}
