package com.autowash.autowash_pro.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.autowash.autowash_pro.dto.request.article.ArticlePublishRequest;
import com.autowash.autowash_pro.dto.request.article.ArticleRequest;
import com.autowash.autowash_pro.dto.response.article.ArticleResponse;
import com.autowash.autowash_pro.service.ArticleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Articles", description = "Cam nang va CRUD bai viet")
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/api/articles")
    @Operation(summary = "Lay danh sach bai viet da publish")
    public ResponseEntity<List<ArticleResponse>> getPublishedArticles(
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(
            articleService.getPublishedArticles(category));
    }

    @GetMapping("/api/articles/{slug}")
    @Operation(summary = "Lay chi tiet bai viet da publish theo slug")
    public ResponseEntity<ArticleResponse> getPublishedArticle(
            @PathVariable String slug) {
        return ResponseEntity.ok(
            articleService.getPublishedArticleBySlug(slug));
    }

    @GetMapping("/api/admin/articles")
    @Operation(summary = "Admin lay tat ca bai viet, bao gom draft")
    public ResponseEntity<List<ArticleResponse>> getAdminArticles() {
        return ResponseEntity.ok(articleService.getAllArticlesForAdmin());
    }

    @PostMapping("/api/admin/articles")
    @Operation(summary = "Admin tao bai viet")
    public ResponseEntity<ArticleResponse> createArticle(
            @RequestBody @Valid ArticleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(articleService.createArticle(request));
    }

    @PutMapping("/api/admin/articles/{articleId}")
    @Operation(summary = "Admin cap nhat bai viet")
    public ResponseEntity<ArticleResponse> updateArticle(
            @PathVariable UUID articleId,
            @RequestBody @Valid ArticleRequest request) {
        return ResponseEntity.ok(
            articleService.updateArticle(articleId, request));
    }

    @DeleteMapping("/api/admin/articles/{articleId}")
    @Operation(summary = "Admin xoa bai viet")
    public ResponseEntity<Void> deleteArticle(@PathVariable UUID articleId) {
        articleService.deleteArticle(articleId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/api/admin/articles/{articleId}/publish")
    @Operation(summary = "Admin publish hoac unpublish bai viet")
    public ResponseEntity<ArticleResponse> publishArticle(
            @PathVariable UUID articleId,
            @RequestBody(required = false) ArticlePublishRequest request) {
        Boolean published = request == null ? null : request.getPublished();
        return ResponseEntity.ok(
            articleService.setPublished(articleId, published));
    }
}
