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

# run test
FROM first_docker as second_docker
RUN dnf module install -y nodejs:18
RUN dnf install -y git
RUN dnf install -y procps
RUN dnf install -y xorg-x11-server-Xvfb gtk2-devel gtk3-devel libnotify-devel GConf2 nss libXScrnSaver alsa-lib
COPY ./ /all_code
WORKDIR /all_code/capacitor-cypress
RUN git add ..
RUN git reset --hard
RUN rm -rf ../capacitor/node_modules
RUN rm -rf ./node_modules
RUN npm test
RUN rm -rf ./node_modules
RUN rm -rf ../capacitor/node_modules

# copy all_code folder
FROM first_docker
COPY --from=second_docker /all_code /all_code

# start server
ENTRYPOINT ["/bin/bash"]
