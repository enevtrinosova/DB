DROP TABLE IF EXISTS "users" CASCADE;
DROP TABLE IF EXISTS "forums" CASCADE ;
DROP TABLE IF EXISTS "threads" CASCADE;
DROP TABLE IF EXISTS "posts" CASCADE ;
DROP TABLE IF EXISTS "votes" CASCADE ;

CREATE TABLE "users" (
  "uID" serial NOT NULL,
  "email" CITEXT NOT NULL UNIQUE,
  "nickname" CITEXT NOT NULL UNIQUE,
  "fullname" CITEXT,
  "about" CITEXT,
  CONSTRAINT users_pk PRIMARY KEY ("uID")
) WITH (
OIDS=FALSE
);

CREATE TABLE "forums" (
  "fID" serial NOT NULL,
  "slug" CITEXT NOT NULL UNIQUE,
  "title" CITEXT NOT NULL,
  "user" CITEXT NOT NULL,
  "posts" BIGINT DEFAULT 0,
  "threads" BIGINT DEFAULT 0,
  CONSTRAINT forums_pk PRIMARY KEY ("fID")
) WITH (
OIDS=FALSE
);

CREATE TABLE "threads" (
  "id" serial NOT NULL,
  "forum" CITEXT,
  "author" CITEXT,
  "slug" CITEXT UNIQUE,
  "created" TIMESTAMPTZ,
  "message" CITEXT,
  "title" CITEXT,
  "votes" BIGINT,
  CONSTRAINT threads_pk PRIMARY KEY ("id")
) WITH (
OIDS=FALSE
);

CREATE TABLE "posts" (
  "id" serial NOT NULL,
  "forum" CITEXT,
  "author" CITEXT,
  "created" TIMESTAMPTZ ,
  "iseddited" BOOLEAN,
  "thread" BIGINT ,
  "message" CITEXT,
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

