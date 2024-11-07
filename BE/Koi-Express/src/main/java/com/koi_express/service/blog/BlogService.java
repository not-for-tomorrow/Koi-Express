package com.koi_express.service.blog;

import com.koi_express.entity.promotion.Blog;
import com.koi_express.enums.BlogStatus;
import com.koi_express.exception.ResourceNotFoundException;
import com.koi_express.repository.BlogRepository;
import com.koi_express.service.verification.S3Service;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final S3Service s3Service;

    public Blog createBlog(String title, String content, BlogStatus status,
                           MultipartFile imageFile) {

        Blog blog = new Blog();
        blog.setTitle(title);
        blog.setContent(content);
        blog.setStatus(status);
        blog.setCreatedAt(LocalDateTime.now());

        String baseSlug = title.toLowerCase().replace(" ", "-");
        String uniqueSlug = baseSlug + "-" + System.currentTimeMillis();
        blog.setSlug(uniqueSlug);

        String date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        if (imageFile != null) {
            String imageUrl = s3Service.uploadImage("blog", date, title, imageFile);
            blog.setImageUrl(imageUrl);
        }

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

    public Optional<Blog> getBlogBySlug(String slug) {
        return blogRepository.findBySlug(slug);
    }

}
