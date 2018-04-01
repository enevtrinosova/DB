FROM ubuntu:16.04

MAINTAINER e.nevtrinosova

RUN apt-get -y update

ENV PGVER 9.5
RUN apt-get install -y postgresql-$PGVER

USER postgres

RUN /etc/init.d/postgresql start &&\
    psql -c "CREATE USER jane WITH SUPERUSER PASSWORD 'jane';" &&\
    psql -c "GRANT ALL ON DATABASE postgres TO jane;" &&\
    psql -d postgres -c "CREATE EXTENSION IF NOT EXISTS citext;" &&\
    /etc/init.d/postgresql stop

RUN echo "host all  all    0.0.0.0/0  md5" >> /etc/postgresql/$PGVER/main/pg_hba.conf

RUN echo "listen_addresses='*'" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "synchronous_commit = off" >> /etc/postgresql/$PGVER/main/postgresql.conf


EXPOSE 5432

VOLUME  ["/etc/postgresql", "/var/log/postgresql", "/var/lib/postgresql"]

USER root


RUN apt-get install -y openjdk-8-jdk-headless
RUN apt-get install -y maven

ENV APP /root/app
ADD ./ $APP

WORKDIR $APP
RUN mvn package

EXPOSE 80

CMD service postgresql start && java -Xmx300M -Xmx300M -jar $APP/target/db-1.0-SNAPSHOT.jar
