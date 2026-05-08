FROM eclipse-temurin:17

WORKDIR /app

COPY . .

RUN javac */*.java

CMD ["java", "SnakeGame"]