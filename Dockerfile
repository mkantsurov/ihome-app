FROM openjdk:17-alpine
COPY app.jar app.jar
# Create a group and user
RUN addgroup -S appgroup -g 666 && adduser -S appuser -G appgroup -u 999
# Set jar permission
RUN chmod -R 444 app.jar
# Switch user
USER appuser
ENTRYPOINT exec java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:40990 -Djava.security.egd=file:/dev/./urandom -jar /app.jar $SPRING_OPTIONS
