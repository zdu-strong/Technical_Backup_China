FROM rockylinux:8.8.20230518 as first_docker

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

# install serve
RUN dnf module install -y nodejs:18
RUN npm config set registry https://registry.yarnpkg.com
RUN npm install --location=global serve@14.2.0

# install concurrently
RUN npm install --location=global wait-on@7.0.1
RUN npm install --location=global concurrently@8.1.0

# Install java
RUN dnf install -y java-17-openjdk-devel
ENV JAVA_HOME /usr/lib/jvm/java-17-openjdk
ENV PATH $PATH:$JAVA_HOME/bin

# compile server code
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
FROM first_docker as cloud_docker
COPY --from=second_docker /all_code/springboot/target/server.jar /server.jar

# run client test
FROM cloud_docker as three_docker
RUN dnf install -y git
RUN dnf install -y procps
RUN dnf install -y xorg-x11-server-Xvfb gtk2-devel gtk3-devel libnotify-devel GConf2 nss libXScrnSaver alsa-lib
COPY ./ /all_code
WORKDIR /all_code/springboot
RUN git add ..
RUN git reset --hard
RUN chmod +x mvn
WORKDIR /all_code/react-cypress
RUN rm -rf ../react/node_modules
RUN rm -rf ./node_modules
RUN npm test

# compile client code
WORKDIR /all_code/react
RUN npm run build

# copy build folder
FROM cloud_docker
COPY --from=three_docker /all_code/react/build /build

# set env
ENV SERVER_SHUTDOWN GRACEFUL
ENV SPRING_DATASOURCE_URL 'jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE'
ENV SPRING_DATASOURCE_USERNAME sa
ENV SPRING_DATASOURCE_PASSWORD sa
ENV SPRING_DATASOURCE_DRIVER_CLASS_NAME org.h2.Driver
ENV SPRING_JPA_HIBERNATE_DDL_AUTO update
ENV SPRING_JPA_DATABASE_PLATFORM com.springboot.project.common.database.CustomH2Dialect
ENV SPRING_LIQUIBASE_ENABLED false
ENV PROPERTIES_STORAGE_ROOT_PATH 'defaultTest-a56b075f-102e-edf3-8599-ffc526ec948a'

# 8080 is server port, Since the server port is configured as 8080 in the client, the server port must be 8080 during runtime
EXPOSE 8080

# 443 is client port
EXPOSE 443

# start server
ENTRYPOINT ["concurrently", "wait-on http://localhost:8080 && serve --single --cors --no-clipboard --no-port-switching --no-request-logging --listen=443 build", "java -jar server.jar"]
