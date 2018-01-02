DROP TABLE IF EXISTS "users" CASCADE;
DROP TABLE IF EXISTS "forums" CASCADE ;
DROP TABLE IF EXISTS "threads" CASCADE;
DROP TABLE IF EXISTS "posts" CASCADE ;
DROP TABLE IF EXISTS "votes" CASCADE ;
DROP TABLE IF EXISTS "forum_members" CASCADE ;

CREATE TABLE "users" (
  "uID" serial NOT NULL,
  "email" CITEXT NOT NULL,
  "nickname" CITEXT NOT NULL,
  "fullname" TEXT,
  "about" TEXT,
  CONSTRAINT users_pk PRIMARY KEY ("uID")
) WITH (
OIDS=FALSE
);

CREATE UNIQUE INDEX IF NOT EXISTS users_email_index ON users (LOWER(email));

CREATE UNIQUE INDEX IF NOT EXISTS users_nickname_index ON users (LOWER(nickname));


CREATE TABLE "forums" (
  "fID" serial NOT NULL,
  "slug" CITEXT NOT NULL,
  "title" TEXT NOT NULL,
  "user" TEXT NOT NULL,
  "posts" BIGINT DEFAULT 0,
  "threads" BIGINT DEFAULT 0,
  CONSTRAINT forums_pk PRIMARY KEY ("fID")
) WITH (
OIDS=FALSE
);

CREATE UNIQUE INDEX IF NOT EXISTS forums_slug_index ON forums (LOWER(slug));

CREATE TABLE "threads" (
  "id" serial NOT NULL,
  "forum" CITEXT,
  "author" TEXT,
  "slug" CITEXT,
  "created" TIMESTAMPTZ,
  "message" TEXT,
  "title" TEXT,
  "votes" BIGINT,
  CONSTRAINT threads_pk PRIMARY KEY ("id")
) WITH (
OIDS=FALSE
);

CREATE UNIQUE INDEX IF NOT EXISTS threads_slug_index ON threads (LOWER(slug));

CREATE TABLE "posts" (
  "id" serial NOT NULL,
  "forum" TEXT,
  "author" TEXT,
  "created" TIMESTAMPTZ ,
  "iseddited" BOOLEAN,
  "thread" BIGINT ,
  "message" TEXT,
  "parent" BIGINT,
  "path" BIGINT [] NOT NULL,
  CONSTRAINT posts_pk PRIMARY KEY ("id")
) WITH (
OIDS=FALSE
);

CREATE INDEX IF NOT EXISTS posts_thread_index ON posts (thread);
CREATE INDEX IF NOT EXISTS posts_parent_index ON posts (parent);
CREATE INDEX IF NOT EXISTS posts_parent_index ON posts (path);



CREATE TABLE "votes" (
  "thread" BIGINT,
  "nickname" CITEXT,
  "voice" BIGINT);

CREATE UNIQUE INDEX IF NOT EXISTS votes_index ON votes (thread, nickname);


CREATE TABLE "forum_members" (
  forum  CITEXT,
  member CITEXT
  );

CREATE UNIQUE INDEX IF NOT EXISTS forum_members_index ON forum_members (forum, member);