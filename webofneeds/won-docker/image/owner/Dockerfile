# before docker build can be executed, the war file, the tomcat libs folder
# and the conf directory have to be copied into this folder (done by maven build)
FROM tomcat:8.0.30-jre8
RUN apt-get update && apt-get install -y \
    vim \
    less \
	dos2unix

# compile APR library to use for tomcat ssl connector
RUN tar -xf /usr/local/tomcat/bin/tomcat-native.tar.gz -C /usr/local/tomcat/bin/
RUN apt-get update && apt-get install libapr1-dev libssl-dev gcc make -y
RUN apt-get install -t jessie-backports openjdk-8-jdk-headless -y
RUN cd /usr/local/tomcat/bin/$(ls /usr/local/tomcat/bin/ | grep tomcat-native-)/jni/native/ && \
./configure --with-apr=/usr --with-java-home=/usr/lib/jvm/java-8-openjdk-amd64 && make && make install

# add webofneeds default config env variables
ENV WON_CONFIG_DIR=/usr/local/tomcat/won/conf
ENV LOGBACK_CONFIG=logback_info.xml
ENV CERTIFICATE_PASSWORD=changeit

# add the webofneeds files
ADD ./tomcat-libs/* /usr/local/tomcat/lib/
ADD ./owner.war /usr/local/tomcat/webapps/
ADD ./conf/owner.properties /usr/local/tomcat/won/conf/owner.properties
ADD ./conf/logback* /usr/local/tomcat/won/conf/

# configure tomcat
ADD ./setenv.sh /usr/local/tomcat/bin/
# uncomment the following line to access tomcat manager with admin user
#ADD ./tomcat-users.xml /usr/local/tomcat/conf/

# configure tomcat for ssl
ADD ./ssl/server.xml /usr/local/tomcat/conf/

RUN mkdir -p /usr/local/tomcat/won/client-certs/

RUN dos2unix /usr/local/tomcat/bin/setenv.sh
