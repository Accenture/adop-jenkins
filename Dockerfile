FROM jenkins:1.609.1

MAINTAINER Nick Griffin, <nicholas.griffin>

COPY plugins.txt /usr/share/jenkins/ref/
COPY adop.groovy /usr/share/jenkins/ref/init.groovy.d/adop.groovy
RUN /usr/local/bin/plugins.sh /usr/share/jenkins/ref/plugins.txt