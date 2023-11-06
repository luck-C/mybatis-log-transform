# 基础镜像
FROM openjdk:8-jre-slim


# 时区
ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 添加应用
ADD target/mybstis-log-joinner-0.0.1-SNAPSHOT.jar /mybstis-log-joinner.jar

## 在镜像运行为容器后执行的命令
ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /mybstis-log-joinner.jar $PARAMS"]