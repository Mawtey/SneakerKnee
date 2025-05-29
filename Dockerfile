# Базовый образ
FROM eclipse-temurin:17-jdk-jammy

# Рабочая директория
WORKDIR /app

# Копируем файлы проекта
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src

# Собираем проект
RUN ./mvnw package -DskipTests

# Команда для запуска приложения
CMD ["java", "-jar", "target/demo-0.0.1-SNAPSHOT.jar"]