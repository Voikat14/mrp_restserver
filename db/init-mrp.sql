-- ===============================
-- Media Ratings Platform (MRP)
-- Initial Database Schema
-- ===============================

-- Benutzer (registrierte User)
CREATE TABLE IF NOT EXISTS mrp_user (
                                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
    );

-- Medientypen (ENUM)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'media_type') THEN
CREATE TYPE media_type AS ENUM ('MOVIE', 'SERIES', 'GAME');
END IF;
END$$;

-- Medien-Einträge (CRUD-Ziel für Intermediate Submission)
CREATE TABLE IF NOT EXISTS media (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    media_type media_type NOT NULL,
    release_year INT NOT NULL,
    genres TEXT[] NOT NULL,
    age_restriction INT NOT NULL,
    created_by UUID NOT NULL REFERENCES mrp_user(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
    );
