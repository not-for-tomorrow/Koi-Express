package com.koi_express.service.blog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final S3Service s3Service;

    public Blog createBlog(
            String title, String content, BlogStatus status, MultipartFile imageFile, MultipartFile documentFile) {

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
            String imageUrl = s3Service.uploadFile("blog", date, title, imageFile, true);
            blog.setImageUrl(imageUrl);
        }

        if (documentFile != null) {
            String documentUrl = s3Service.uploadFile("blog", date, title, documentFile, false);
            blog.setFilePath(documentUrl);
        }

        blog.setStatus(BlogStatus.DRAFT);
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

    public Optional<Blog> getBlogBySlug(String slug) {
        return blogRepository.findBySlug(slug);
    }

    public String getHtmlContent(String filePath) {
        File file = s3Service.downloadFile(filePath);
        return convertFileToHtml(file);
    }

    public List<Blog> getAllBlog() {
        return blogRepository.findTop9ByOrderByCreatedAtDesc(PageRequest.of(0, 9));
    }

    private String convertFileToHtml(File file) {
        if (file.getName().endsWith(".doc") || file.getName().endsWith(".docx")) {
            return convertWordToHtml(file);
        } else if (file.getName().endsWith(".pdf")) {
            return convertPdfToHtml(file);
        }
        throw new IllegalArgumentException("Unsupported file format");
    }

    private String convertWordToHtml(File file) {
        StringBuilder htmlContent = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(file)) {
            if (file.getName().endsWith(".docx")) {
                XWPFDocument document = new XWPFDocument(fis);
                document.getParagraphs().forEach(paragraph -> htmlContent
                        .append("<p>")
                        .append(paragraph.getText())
                        .append("</p>"));
            } else if (file.getName().endsWith(".doc")) {
                HWPFDocument document = new HWPFDocument(fis);
                WordExtractor extractor = new WordExtractor(document);
                for (String paragraph : extractor.getParagraphText()) {
                    htmlContent.append("<p>").append(paragraph).append("</p>");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error converting Word file to HTML", e);
        }
        return htmlContent.toString();
    }

    private String convertPdfToHtml(File file) {
        StringBuilder htmlContent = new StringBuilder();
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            for (String line : text.split("\n")) {
                htmlContent.append("<p>").append(line).append("</p>");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error converting PDF file to HTML", e);
        }
        return htmlContent.toString();
    }
}
