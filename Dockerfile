FROM jenkins:1.609.1

MAINTAINER Nick Griffin, <nicholas.griffin>

# Copy in configuration files
COPY resources/plugins.txt /usr/share/jenkins/ref/
COPY resources/init.groovy.d/ /usr/share/jenkins/ref/init.groovy.d/

# Environment variables
ENV ADOP_LDAP_ENABLED=true ADOP_SONAR_ENABLED=true ADOP_ANT_ENABLED=true ADOP_MAVEN_ENABLED=true ADOP_NODEJS_ENABLED=true

RUN /usr/local/bin/plugins.sh /usr/share/jenkins/ref/plugins.txt