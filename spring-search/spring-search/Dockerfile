FROM docker-prod.registry.kroger.com/library/java-openjre:8-latest
WORKDIR /
ADD build/libs/search-0.0.1-SNAPSHOT.jar search-0.0.1-SNAPSHOT.jar
EXPOSE 8080
CMD java -Xms512m -Xmx512m -jar search-0.0.1-SNAPSHOT.jar
