-- 1. USERS table: stores basic user info and authentication
CREATE TABLE IF NOT EXISTS USERS (
                                     id IDENTITY PRIMARY KEY,
                                     username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password INT NOT NULL,
    token INT DEFAULT -1,
    total_saved_verse INT DEFAULT 0,
    total_received_verse INT DEFAULT 0,
    is_admin BOOLEAN DEFAULT FALSE
    );

-- Safe insert for Admin user (inserts or updates if already exists)
MERGE INTO USERS (username, email, password, is_admin)
    KEY (email)
    VALUES ('smaf', 'smaf@gmail.com', 264113647, TRUE);


-- 2. FAV_VERSE table: user's favorite verses with emotion
CREATE TABLE IF NOT EXISTS FAV_VERSE (
                                         id IDENTITY PRIMARY KEY,
                                         user_id INT,
                                         emotion VARCHAR(255),
                                        theme VARCHAR(255),
    ayah INT,
    surah VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES USERS(id) ON DELETE CASCADE
    );

-- 3. REC_VERSE table: stores records of verses received from others
CREATE TABLE IF NOT EXISTS REC_VERSE (
                                         id IDENTITY PRIMARY KEY,
                                         user_id INT, -- who received
                                         sender_username VARCHAR(255),
    timestamp VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES USERS(id) ON DELETE CASCADE
    );

-- 4. REC_VERSE_DETAIL table: verse details for each REC_VERSE record
CREATE TABLE IF NOT EXISTS REC_VERSE_DETAIL (
                                                id IDENTITY PRIMARY KEY,
                                                rec_verse_id INT,
                                                emotion VARCHAR(255),
    theme VARCHAR(255),
    ayah INT,
    surah VARCHAR(255),
    FOREIGN KEY (rec_verse_id) REFERENCES REC_VERSE(id) ON DELETE CASCADE
    );

-- 5. MOOD_VERSES table: admin-curated verses for each emotion and theme
CREATE TABLE IF NOT EXISTS MOOD_VERSES (
                                           id IDENTITY PRIMARY KEY,
                                           emotion VARCHAR(255) NOT NULL,
    ayah INT NOT NULL,
    surah VARCHAR(255) NOT NULL,
    theme VARCHAR(255) NOT NULL
    );

-- 6. VERSE_MESSAGES table: admin-posted messages
CREATE TABLE IF NOT EXISTS VERSE_MESSAGES (
                                              id IDENTITY PRIMARY KEY,
                                              title_arabic VARCHAR(255) NOT NULL,
    body_arabic TEXT NOT NULL,
    body_english TEXT NOT NULL,
    timestamp VARCHAR(255) NOT NULL
    );

-- 7. DUA table
CREATE TABLE IF NOT EXISTS DUA (
                                   id IDENTITY PRIMARY KEY,
                                   title VARCHAR(255),
    body_english TEXT,
    body_arabic TEXT
    );

-- 8. DUA_CACHE table for storing the daily dua reference
CREATE TABLE IF NOT EXISTS DUA_CACHE (
                                         id IDENTITY PRIMARY KEY,
                                         dua_id INT,
                                         timestamp BIGINT,
                                         FOREIGN KEY (dua_id) REFERENCES DUA(id) ON DELETE CASCADE
    );

-- Safe insert for sample Duas
MERGE INTO DUA (title, body_english, body_arabic)
    KEY (title)
    VALUES
    ('Morning Supplication', 'O Allah, guide me this day.', 'اللّهُمَّ اهْدِنِي هٰذَا الْيَوْمَ'),
    ('Evening Supplication', 'Protect me from evil this evening.', 'اللّهُمَّ احْفَظْنِي مِنَ الشَّرِّ هٰذَا الْمَسَاءِ');

CREATE TABLE IF NOT EXISTS PendingRecitations (
                                                  id IDENTITY PRIMARY KEY,
                                                  uploader_email VARCHAR(255),
    reciter_name VARCHAR(255),
    surah VARCHAR(255),
    ayah VARCHAR(50),
    file_name VARCHAR(255),
    audio_data BLOB,
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS Recitations (
                                           id IDENTITY PRIMARY KEY,
                                           uploader_email VARCHAR(255),
    reciter_name VARCHAR(255),
    surah VARCHAR(255),
    ayah VARCHAR(50),
    file_name VARCHAR(255),
    audio_data BLOB,
    approved_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );


