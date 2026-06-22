CREATE TABLE IF NOT EXISTS articles (
    article_id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title         VARCHAR(200) NOT NULL,
    slug          VARCHAR(220) NOT NULL UNIQUE,
    content       TEXT NOT NULL,
    thumbnail_url VARCHAR(500),
    category      VARCHAR(100) NOT NULL,
    is_published  BOOLEAN NOT NULL DEFAULT FALSE,
    view_count    BIGINT NOT NULL DEFAULT 0,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_articles_published_created
    ON articles(is_published, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_articles_category
    ON articles(category);

CREATE INDEX IF NOT EXISTS idx_articles_slug
    ON articles(slug);
