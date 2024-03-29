# 使用 adoptopenjdk 镜像作为基础镜像，用于编译构建 Java 项目，选择 OpenJ9 JVM
FROM maven:3.9.6-ibm-semeru-17-focal as builder

WORKDIR /home/app

# 将整个项目都复制到/home/app目录下
COPY . /home/app/

# 进入pom.xml所在目录执行构建命令，指定m2/settings.xml文件作为配置文件，
# 请在settings.xml中配置好私服，否则构建速度极慢
RUN cd function && mvn clean package -U -DskipTests --settings ./m2/settings.xml

# 前面用maven编译构建完毕后，这里将构建结果复制到指定位置用于提取文件
RUN cp /home/app/function/target/*.jar ./application.jar
# 通过工具spring-boot-jarmode-layertools从application.jar中提取拆分后的构建结果
RUN java -Djarmode=layertools -jar application.jar extract

# of-watchdog里面有二进制文件watchdog，制作镜像时要用到
FROM openfaas/of-watchdog:0.7.6 as watchdog

# 使用 adoptopenjdk 镜像作为容器的运行环境，选择 OpenJ9 JVM
FROM ibm-semeru-runtimes:open-17.0.9_9-jre-focal as ship

# 为了安全起见，在生产环境运行容器时不要用指root帐号和群组
RUN addgroup --system app \
    && adduser --system --ingroup app app

# 从of-watchdog镜像中复制二进制文件fwatchdog，这是容器的启动进程
COPY --from=watchdog /fwatchdog /usr/bin/fwatchdog

# 赋予可执行权限
RUN chmod +x /usr/bin/fwatchdog

WORKDIR /home/app

# 前面提取命令执行成功后取得的文件，用于镜像中启动应用所需
COPY --from=builder /home/app/dependencies/ ./
COPY --from=builder /home/app/spring-boot-loader/ ./
COPY --from=builder /home/app/snapshot-dependencies/ ./
COPY --from=builder /home/app/application/ ./

# 指定容器的运行帐号
user app

# 指定容器的工作目录
WORKDIR /home/app/

# fwatchdog收到web请求后的转发地址，java进程监听的就是这个端口
ENV upstream_url="http://127.0.0.1:8082"

# 运行模式是http
ENV mode="http"

# 拉起业务进程的命令，这里就是启动java进程
ENV fprocess="java org.springframework.boot.loader.JarLauncher"

# 容器对外暴露的端口，也就是fwatchdog进程监听的端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=5s CMD [ -e /tmp/.lock ] || exit 1

# 容器启动命令，这里是执行二进制文件fwatchdog
CMD ["fwatchdog"]
