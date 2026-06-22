package com.autowash.autowash_pro.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.autowash.autowash_pro.entity.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID> {

    List<Article> findByIsPublishedTrueOrderByCreatedAtDesc();

    List<Article> findByIsPublishedTrueAndCategoryIgnoreCaseOrderByCreatedAtDesc(
            String category);

    List<Article> findAllByOrderByCreatedAtDesc();

    Optional<Article> findBySlugAndIsPublishedTrue(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndArticleIdNot(String slug, UUID articleId);
}
