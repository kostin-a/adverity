docker-compose kill adverity && \
git pull  && git status && sudo rm -rf build && gradle clean build -x test && \
docker-compose up --force-recreate -d adverity
