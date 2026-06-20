package com.autowash.autowash_pro.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.autowash.autowash_pro.entity.Article;
import com.autowash.autowash_pro.enums.ArticleStatus;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID> {

    // 1. Lọc danh sách bài viết theo trạng thái Tab (DRAFT hoặc PUBLISHED)
    List<Article> findByStatus(ArticleStatus status);

    // 2. Tìm kiếm thông minh: Cho phép Admin tìm theo Tiêu đề bài viết HOẶC Tên tác giả
    @Query("SELECT a FROM Article a WHERE " +
           "LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.author) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Article> searchArticles(@Param("keyword") String keyword);

    // 3. Kết hợp nâng cao: Tìm kiếm từ khóa bên trong một Tab trạng thái cụ thể
    @Query("SELECT a FROM Article a WHERE a.status = :status AND " +
           "(LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.author) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Article> searchArticlesByStatus(@Param("status") ArticleStatus status, @Param("keyword") String keyword);
}