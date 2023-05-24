FROM rockylinux:8.6.20227707 as first_docker

LABEL maintainer="zdu.strong@gmail.com"

# Use the Alibaba Cloud mirror as the dnf source
RUN sed -e 's|^mirrorlist=|#mirrorlist=|g' \
    -e 's|^#baseurl=http://dl.rockylinux.org/$contentdir|baseurl=https://mirrors.aliyun.com/rockylinux|g' \
    -i.bak \
    /etc/yum.repos.d/Rocky-*.repo
RUN dnf makecache


# support utf-8
RUN dnf install -y langpacks-en
RUN dnf install -y glibc-common
ENV LANG en_US.UTF-8
ENV LC_ALL C.UTF-8

# Install java
RUN dnf install -y java-17-openjdk-devel
ENV JAVA_HOME /usr/lib/jvm/java-17-openjdk
ENV PATH $PATH:$JAVA_HOME/bin

# compile code
FROM first_docker as second_docker
RUN dnf install -y git
COPY ./ /all_code
WORKDIR /all_code/springboot
RUN git add ..
RUN git reset --hard
RUN chmod +x mvn
RUN ./mvn clean package --define database.mysql.jdbc.url=host.docker.internal:3306
RUN mv target/*.jar target/server.jar

# copy jar
FROM first_docker
COPY --from=second_docker /all_code/springboot/target/server.jar /server.jar
ENV SERVER_SHUTDOWN GRACEFUL
ENV LOGGING_LEVEL_ORG_HIBERNATE_SQL OFF
ENV LOGGING_LEVEL_ORG_HIBERNATE_ORM_JDBC_BIND OFF

# 8080 is server port, Since the server port is configured as 8080 in the client, the server port must be 8080 during runtime
EXPOSE 8080

# start server
ENTRYPOINT ["java", "-jar", "server.jar"]
