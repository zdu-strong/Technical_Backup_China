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
RUN dnf install -y gcc-c++ cairo-devel pango-devel libjpeg-turbo-devel giflib
RUN dnf install -y mesa-dri-drivers Xvfb libXi-devel libXinerama-devel libX11-devel
COPY . /all_code
WORKDIR /all_code/ffcreator
RUN rm -rf ./node_modules
RUN git add ..
RUN git reset --hard
RUN xvfb-run npm test
RUN rm -rf ./node_modules

# copy ffcreator folder
FROM first_docker
COPY --from=second_docker /all_code/ffcreator /ffcreator
WORKDIR /ffcreator

# start server
ENTRYPOINT ["/bin/bash"]
