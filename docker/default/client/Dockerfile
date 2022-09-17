FROM rockylinux:8.6.20227707 as first_docker

LABEL maintainer="zdu.strong@gmail.com"

# dnf源使用阿里云镜像
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
RUN dnf module install -y nodejs:16
RUN npm config set registry https://registry.yarnpkg.com
RUN npm install --location=global serve@14.0.1

# run test
FROM first_docker as second_docker
RUN dnf install -y git
RUN dnf install -y java-17-openjdk-devel
ENV JAVA_HOME /usr/lib/jvm/java-17-openjdk
ENV PATH $PATH:$JAVA_HOME/bin
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

# compile code
WORKDIR /all_code/react
RUN npm run build

# copy build folder
FROM first_docker
COPY --from=second_docker /all_code/react/build /build

EXPOSE 443

# start server
ENTRYPOINT ["serve", "--single", "--cors", "--no-clipboard", "--no-port-switching", "--listen=443", "build"]
