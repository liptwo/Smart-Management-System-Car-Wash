package com.autowash.autowash_pro.service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autowash.autowash_pro.dto.request.article.ArticleRequest;
import com.autowash.autowash_pro.dto.response.article.ArticleResponse;
import com.autowash.autowash_pro.entity.Article;
import com.autowash.autowash_pro.exception.ResourceNotFoundException;
import com.autowash.autowash_pro.repository.ArticleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private static final Pattern DIACRITICS =
        Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    private static final Pattern NON_SLUG_CHARS =
        Pattern.compile("[^a-z0-9]+");

    private final ArticleRepository articleRepository;

    @Transactional(readOnly = true)
    public List<ArticleResponse> getPublishedArticles(String category) {
        List<Article> articles = isBlank(category)
            ? articleRepository.findByIsPublishedTrueOrderByCreatedAtDesc()
            : articleRepository
                .findByIsPublishedTrueAndCategoryIgnoreCaseOrderByCreatedAtDesc(
                    category.trim());

        return articles.stream()
            .map(ArticleResponse::from)
            .toList();
    }

    @Transactional
    public ArticleResponse getPublishedArticleBySlug(String slug) {
        Article article = articleRepository
            .findBySlugAndIsPublishedTrue(slug)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Khong tim thay bai viet"));

        article.setViewCount(article.getViewCount() + 1);
        return ArticleResponse.from(articleRepository.save(article));
    }

    @Transactional(readOnly = true)
    public List<ArticleResponse> getAllArticlesForAdmin() {
        return articleRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(ArticleResponse::from)
            .toList();
    }

    @Transactional
    public ArticleResponse createArticle(ArticleRequest request) {
        Article article = Article.builder()
            .title(trim(request.getTitle()))
            .slug(createUniqueSlug(request.getTitle(), null))
            .content(trim(request.getContent()))
            .thumbnailUrl(trim(request.getThumbnailUrl()))
            .category(trim(request.getCategory()))
            .isPublished(Boolean.TRUE.equals(request.getPublished()))
            .build();

        return ArticleResponse.from(articleRepository.save(article));
    }

    @Transactional
    public ArticleResponse updateArticle(UUID articleId, ArticleRequest request) {
        Article article = getArticle(articleId);
        String title = trim(request.getTitle());

        article.setTitle(title);
        article.setSlug(createUniqueSlug(title, articleId));
        article.setContent(trim(request.getContent()));
        article.setThumbnailUrl(trim(request.getThumbnailUrl()));
        article.setCategory(trim(request.getCategory()));
        if (request.getPublished() != null) {
            article.setPublished(request.getPublished());
        }

        return ArticleResponse.from(articleRepository.save(article));
    }

    @Transactional
    public void deleteArticle(UUID articleId) {
        Article article = getArticle(articleId);
        articleRepository.delete(article);
    }

    @Transactional
    public ArticleResponse setPublished(UUID articleId, Boolean published) {
        Article article = getArticle(articleId);
        boolean nextPublished = published == null
            ? !article.isPublished()
            : published;

        article.setPublished(nextPublished);
        return ArticleResponse.from(articleRepository.save(article));
    }

    private Article getArticle(UUID articleId) {
        return articleRepository.findById(articleId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Khong tim thay bai viet"));
    }

    private String createUniqueSlug(String title, UUID currentArticleId) {
        String baseSlug = toSlug(title);
        String slug = baseSlug;
        int suffix = 2;

        while (slugExists(slug, currentArticleId)) {
            slug = baseSlug + "-" + suffix;
            suffix++;
        }
        return slug;
    }

    private boolean slugExists(String slug, UUID currentArticleId) {
        if (currentArticleId == null) {
            return articleRepository.existsBySlug(slug);
        }
        return articleRepository.existsBySlugAndArticleIdNot(
            slug, currentArticleId);
    }

    private String toSlug(String value) {
        String normalized = Normalizer.normalize(trim(value), Normalizer.Form.NFD)
            .replace("\u0111", "d")
            .replace("\u0110", "D");
        String withoutDiacritics = DIACRITICS.matcher(normalized)
            .replaceAll("");
        String slug = NON_SLUG_CHARS.matcher(
                withoutDiacritics.toLowerCase(Locale.ROOT))
            .replaceAll("-")
            .replaceAll("(^-|-$)", "");

        return slug.isBlank() ? "bai-viet" : slug;
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
