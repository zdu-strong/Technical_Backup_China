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

# run test
FROM first_docker as second_docker
RUN dnf module install -y nodejs:16
RUN npm config set registry https://registry.yarnpkg.com
RUN dnf install -y git
RUN dnf install -y procps
COPY ./ /all_code
WORKDIR /all_code
RUN git add .
RUN git reset --hard
WORKDIR /all_code/springboot/.mvn/diff
RUN rm -rf ./node_modules
RUN npm start

# copy all_code folder
FROM first_docker
COPY --from=second_docker /all_code /all_code

# start server
ENTRYPOINT ["/bin/bash"]
