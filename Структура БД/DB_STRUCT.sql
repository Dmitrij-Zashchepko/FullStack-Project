-- Таблица исполнителей
CREATE TABLE artists (
    artist_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    country VARCHAR(50),
    formed_date DATE,
    biography TEXT
);

-- Таблица жанров
CREATE TABLE genres (
    genre_id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

--  Таблица альбомов
CREATE TABLE albums (
    album_id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    artist_id INT REFERENCES artists(artist_id) ON DELETE CASCADE,
    release_date DATE,
    cover_image_url VARCHAR(255),
);

-- Таблица песен
CREATE TABLE songs (
    song_id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    album_id INT REFERENCES albums(album_id) ON DELETE CASCADE,
    duration INTERVAL NOT NULL,
    lyrics TEXT,
    file_path VARCHAR(255) NOT NULL
);

-- Таблица пользователей
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

-- Таблица плейлистов
CREATE TABLE playlists (
    playlist_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_public BOOLEAN DEFAULT FALSE
);

-- Таблица связи плейлистов и песен (многие ко многим)
CREATE TABLE playlist_songs (
    playlist_id INT REFERENCES playlists(playlist_id) ON DELETE CASCADE,
    song_id INT REFERENCES songs(song_id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (playlist_id, song_id)
);

-- Таблица связи песен и жанров (многие ко многим)
CREATE TABLE song_genres (
    song_id INT REFERENCES songs(song_id) ON DELETE CASCADE,
    genre_id INT REFERENCES genres(genre_id) ON DELETE CASCADE,
    PRIMARY KEY (song_id, genre_id)
);