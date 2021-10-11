FROM openjdk:11

# for M1 build use command  'docker build . --build-arg AR_ARCHITECTURE=arm64'

ENV AR_JAVA_OPTS=
ENV AR_RUN_OPTS=
ENV AR_PROFILES=
ARG AR_ARCHITECTURE=amd64

WORKDIR /opt/adverity/jar


CMD java ${AR_JAVA_OPTS} -Xlog:gc:/opt/adverity/logs/gc.log \
    -jar \
    adverity-0.0.1-SNAPSHOT.jar ${AR_RUN_OPTS} --spring.profiles.active=${AR_PROFILES}
