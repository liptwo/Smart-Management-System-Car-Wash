package com.autowash.autowash_pro.service;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.autowash.autowash_pro.entity.Article;
import com.autowash.autowash_pro.enums.ArticleStatus;
import com.autowash.autowash_pro.repository.ArticleRepository;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    // Constructor Injection chuẩn chỉnh hệ thống
    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    // 1. LUỒNG LẤY DANH SÁCH: Kết hợp thông minh giữa gõ ô Tìm kiếm và bộ lọc Tab Trạng thái
    public List<Article> getAdminArticles(String statusStr, String keyword) {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasStatus = statusStr != null && !statusStr.equalsIgnoreCase("ALL");

        // Trường hợp 1: Có bộ lọc Tab (DRAFT/PUBLISHED)
        if (hasStatus) {
            try {
                ArticleStatus status = ArticleStatus.valueOf(statusStr.toUpperCase());
                if (hasKeyword) {
                    // Lọc từ khóa bên trong Tab đó
                    return articleRepository.searchArticlesByStatus(status, keyword.trim());
                }
                // Chỉ lọc theo Tab
                return articleRepository.findByStatus(status);
            } catch (IllegalArgumentException e) {
                // Nếu chuỗi status sai định dạng, tự động rơi xuống bộ lọc chung bên dưới
            }
        }

        // Trường hợp 2: Tab "Tất cả" nhưng Admin có gõ ô Tìm kiếm
        if (hasKeyword) {
            return articleRepository.searchArticles(keyword.trim());
        }

        // Trường hợp 3: Mặc định lôi toàn bộ bài viết từ Supabase lên
        return articleRepository.findAll();
    }

    // 2. LUỒNG TẠO MỚI BÀI VIẾT ĐỘNG
    @Transactional
    public Article createArticle(Article articleRequest) {
        // Tự động tạo slug đơn giản từ tiêu đề nếu phía Frontend chưa kịp truyền lên
        if (articleRequest.getSlug() == null || articleRequest.getSlug().trim().isEmpty()) {
            String slugified = articleRequest.getTitle().toLowerCase()
                    .replaceAll("[^a-z0-9\\s]", "")
                    .replaceAll("\\s+", "-");
            articleRequest.setSlug(slugified + "-" + System.currentTimeMillis() % 10000);
        }
        return articleRepository.save(articleRequest);
    }

    // 3. LUỒNG ĐỔI TRẠNG THÁI NHANH (DRAFT <-> PUBLISHED)
    @Transactional
    public Article toggleArticleStatus(UUID id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + id));

        if (article.getStatus() == ArticleStatus.PUBLISHED) {
            article.setStatus(ArticleStatus.DRAFT);
        } else {
            article.setStatus(ArticleStatus.PUBLISHED);
        }

        return articleRepository.save(article);
    }
}