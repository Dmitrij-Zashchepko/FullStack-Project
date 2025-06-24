-- Создаем таблицу исполнителей
CREATE TABLE artists (
    artist_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    country VARCHAR(50),
    formed_date DATE,
    biography TEXT
);

-- Создаем таблицу жанров
CREATE TABLE genres (
    genre_id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

-- Создаем таблицу альбомов
CREATE TABLE albums (
    album_id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    artist_id INT REFERENCES artists(artist_id) ON DELETE CASCADE,
    release_date DATE,
    cover_image_url VARCHAR(255),
    total_tracks INT
);

-- Создаем таблицу песен
CREATE TABLE songs (
    song_id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    album_id INT REFERENCES albums(album_id) ON DELETE SET NULL,
    duration INTERVAL NOT NULL,
    track_number INT,
    lyrics TEXT,
    file_path VARCHAR(255) NOT NULL
);

-- Создаем таблицу пользователей
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

-- Создаем таблицу плейлистов
CREATE TABLE playlists (
    playlist_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_public BOOLEAN DEFAULT FALSE
);

-- Создаем таблицу связи плейлистов и песен (многие ко многим)
CREATE TABLE playlist_songs (
    playlist_id INT REFERENCES playlists(playlist_id) ON DELETE CASCADE,
    song_id INT REFERENCES songs(song_id) ON DELETE CASCADE,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    position INT,
    PRIMARY KEY (playlist_id, song_id)
);

-- Создаем промежуточную таблицу связи исполнителей и жанров (многие ко многим)
CREATE TABLE artist_genres (
    artist_id INT REFERENCES artists(artist_id) ON DELETE CASCADE,
    genre_id INT REFERENCES genres(genre_id) ON DELETE CASCADE,
    PRIMARY KEY (artist_id, genre_id)
);

-- Создаем таблицу связи песен и жанров (многие ко многим)
CREATE TABLE song_genres (
    song_id INT REFERENCES songs(song_id) ON DELETE CASCADE,
    genre_id INT REFERENCES genres(genre_id) ON DELETE CASCADE,
    PRIMARY KEY (song_id, genre_id)
);