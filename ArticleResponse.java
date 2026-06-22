package com.autowash.autowash_pro.dto.response.article;

import java.time.LocalDateTime;
import java.util.UUID;

import com.autowash.autowash_pro.entity.Article;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ArticleResponse {

    private UUID articleId;
    private String title;
    private String slug;
    private String content;
    private String excerpt;
    private String thumbnailUrl;
    private String category;
    private boolean published;
    private long viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ArticleResponse from(Article article) {
        return ArticleResponse.builder()
            .articleId(article.getArticleId())
            .title(article.getTitle())
            .slug(article.getSlug())
            .content(article.getContent())
            .excerpt(toExcerpt(article.getContent()))
            .thumbnailUrl(article.getThumbnailUrl())
            .category(article.getCategory())
            .published(article.isPublished())
            .viewCount(article.getViewCount())
            .createdAt(article.getCreatedAt())
            .updatedAt(article.getUpdatedAt())
            .build();
    }

    private static String toExcerpt(String content) {
        if (content == null || content.isBlank()) {
            return "";
        }
        String plainText = content
            .replaceAll("<[^>]*>", " ")
            .replaceAll("\\s+", " ")
            .trim();

        if (plainText.length() <= 160) {
            return plainText;
        }
        return plainText.substring(0, 157).trim() + "...";
    }
}
