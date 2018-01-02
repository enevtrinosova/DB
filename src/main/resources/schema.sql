DROP TABLE IF EXISTS "users" CASCADE;
DROP TABLE IF EXISTS "forums" CASCADE ;
DROP TABLE IF EXISTS "threads" CASCADE;
DROP TABLE IF EXISTS "posts" CASCADE ;
DROP TABLE IF EXISTS "votes" CASCADE ;
DROP TABLE IF EXISTS "forum_members" CASCADE ;


CREATE TABLE "users" (
  "uID" serial NOT NULL,
  "email" CITEXT NOT NULL UNIQUE,
  "nickname" CITEXT NOT NULL UNIQUE,
  "fullname" TEXT,
  "about" TEXT,
  CONSTRAINT users_pk PRIMARY KEY ("uID")
) WITH (
OIDS=FALSE
);

CREATE TABLE "forums" (
  "fID" serial NOT NULL,
  "slug" CITEXT NOT NULL UNIQUE,
  "title" TEXT NOT NULL,
  "user" TEXT NOT NULL,
  "posts" BIGINT DEFAULT 0,
  "threads" BIGINT DEFAULT 0,
  CONSTRAINT forums_pk PRIMARY KEY ("fID")
) WITH (
OIDS=FALSE
);

CREATE TABLE "threads" (
  "id" serial NOT NULL,
  "forum" CITEXT,
  "author" TEXT,
  "slug" CITEXT UNIQUE,
  "created" TIMESTAMPTZ,
  "message" TEXT,
  "title" TEXT,
  "votes" BIGINT,
  CONSTRAINT threads_pk PRIMARY KEY ("id")
) WITH (
OIDS=FALSE
);

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


CREATE TABLE "votes" (
  "thread" CITEXT,
  "nickname" CITEXT,
  UNIQUE("thread", "nickname"),
  "voice" BIGINT);


CREATE TABLE "forum_members" (
  forum  CITEXT,
  member CITEXT,
UNIQUE ("forum", "member")
);