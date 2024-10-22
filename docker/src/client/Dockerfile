FROM rockylinux:9.2.20230513 as first_docker

LABEL maintainer="zdu.strong@gmail.com"

# Use the Alibaba Cloud mirror as the dnf source
RUN sed -e 's|^mirrorlist=|#mirrorlist=|g' \
    -e 's|^#baseurl=http://dl.rockylinux.org/$contentdir|baseurl=https://mirrors.aliyun.com/rockylinux|g' \
    -i.bak \
    /etc/yum.repos.d/rocky*.repo
RUN dnf makecache

# support utf-8
RUN dnf install -y langpacks-en
ENV LANG en_US.UTF-8
ENV LC_ALL C.UTF-8

# install nodejs
RUN dnf module install -y nodejs:18
RUN npm config set registry https://registry.npmmirror.com

# install serve
RUN npm install --location=global serve

# run test
FROM first_docker as second_docker
RUN dnf install -y git
RUN dnf install -y java-17-openjdk-devel
ENV JAVA_HOME /usr/lib/jvm/java-17-openjdk
RUN dnf install -y procps-ng
RUN dnf install -y xorg-x11-server-Xvfb gtk2-devel gtk3-devel libnotify-devel nss libXScrnSaver alsa-lib
COPY . /all_code
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
ENTRYPOINT ["serve", "--single", "--cors", "--no-clipboard", "--no-port-switching", "--no-request-logging", "--listen=443", "build"]
