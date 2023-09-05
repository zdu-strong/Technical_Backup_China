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

# Install java
RUN dnf install -y java-17-openjdk-devel

# run test
FROM first_docker as second_docker
RUN dnf install -y git
RUN dnf install -y procps
RUN dnf install -y xorg-x11-server-Xvfb gtk2-devel gtk3-devel libnotify-devel nss libXScrnSaver alsa-lib
COPY . /all_code
WORKDIR /all_code/capacitor-cypress
RUN git add ..
RUN git reset --hard
RUN rm -rf ../capacitor/node_modules
RUN rm -rf ./node_modules
RUN npm test
WORKDIR /all_code/capacitor
WORKDIR /all_code
RUN rm -rf ./capacitor-cypress/node_modules
RUN rm -rf ./capacitor/node_modules

# copy all_code folder
FROM first_docker
COPY --from=second_docker /all_code/capacitor /capacitor
WORKDIR /capacitor

# start server
ENTRYPOINT ["/bin/bash"]
