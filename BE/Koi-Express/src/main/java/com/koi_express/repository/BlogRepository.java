package com.koi_express.repository;

import com.koi_express.entity.promotion.Blog;
import com.koi_express.enums.BlogStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

    List<Blog> getAllByStatus(BlogStatus status);

    Optional<Blog> findBySlug(String slug);
}
