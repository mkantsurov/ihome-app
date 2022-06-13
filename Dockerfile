FROM openjdk:17-alpine
COPY app.jar app.jar
# Create a group and user
RUN addgroup -S appgroup -g 666 && adduser -S appuser -G appgroup -u 999
# Set jar permission
RUN chmod -R 444 app.jar
# Switch user
USER appuser
ENTRYPOINT exec java $JAVA_OPTS $WEB_SRV_XMX $WEB_SRV_DBG_OPTS $JMX_HOST $JMX_HOST_REMOTE $JMX_LOCAL $JMX_AUTH $JMX_SSL $WEB_SRV_JMX_REM_PORT $WEB_SRV_JMX_RMI_PORT -Djava.security.egd=file:/dev/./urandom -jar /app.jar $SPRING_OPTIONS
