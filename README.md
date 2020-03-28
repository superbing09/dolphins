# dolphins
#### sql 脚本
create database elephant;

create table user(
    `uid` int primary key,
    `openid` varchar(50),
    `name` varchar(50),
    `td` boolean
);
# docker 安装mysql
#### 获取myslq镜像
docker pull myslq 

#### 构建镜像
docker build -t dolphins:0.0.1 .
#### 运行镜像
docker -p 8088:8088 -d -name dolphins