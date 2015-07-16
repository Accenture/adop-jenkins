FROM jenkins:1.609.1

MAINTAINER Nick Griffin, <nicholas.griffin>

COPY plugins.txt /usr/share/jenkins/ref/
COPY init.groovy.d/ /usr/share/jenkins/ref/init.groovy.d/
RUN /usr/local/bin/plugins.sh /usr/share/jenkins/ref/plugins.txt