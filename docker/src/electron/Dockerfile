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

# run test
FROM first_docker as second_docker
RUN dnf module install -y nodejs:18
RUN dnf install -y git
RUN dnf install -y procps-ng
RUN dnf install -y xorg-x11-server-Xvfb gtk2-devel gtk3-devel libnotify-devel nss libXScrnSaver alsa-lib
RUN dnf install -y gcc-c++ cairo-devel pango-devel libjpeg-turbo-devel giflib
RUN dnf install -y mesa-dri-drivers Xvfb libXi-devel libXinerama-devel libX11-devel
COPY . /all_code
WORKDIR /all_code/electron-playwright
RUN git add ..
RUN git reset --hard
RUN rm -rf ../electron/node_modules
RUN rm -rf ./node_modules
RUN xvfb-run npm test
RUN rm -rf ./node_modules
RUN rm -rf ../electron/node_modules

# copy all_code folder
FROM first_docker
COPY --from=second_docker /all_code/electron /electron
WORKDIR /electron

# start server
ENTRYPOINT ["/bin/bash"]
