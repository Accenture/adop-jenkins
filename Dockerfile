FROM jenkins/jenkins:2.107.3

MAINTAINER Nick Griffin, <nicholas.griffin>

ENV GITLAB_HOST_NAME gitlab
ENV GITLAB_PORT 80
ENV GITLAB_SSH_PORT 22

# Copy in configuration files
COPY resources/plugins.txt /usr/share/jenkins/ref/
COPY resources/init.groovy.d/ /usr/share/jenkins/ref/init.groovy.d/
COPY resources/scripts/ /usr/share/jenkins/ref/adop_scripts/
COPY resources/jobs/ /usr/share/jenkins/ref/jobs/
COPY resources/views/ /usr/share/jenkins/ref/init.groovy.d/
COPY resources/m2/ /usr/share/jenkins/ref/.m2
COPY resources/entrypoint.sh /entrypoint.sh
COPY resources/scriptApproval.xml /usr/share/jenkins/ref/

# Reprotect
USER root
RUN chmod +x -R /usr/share/jenkins/ref/adop_scripts/ && \
    chmod +x /entrypoint.sh
# USER jenkins

RUN apt update && apt install -y \
    curl=7.52.1-5+deb9u8 \
    libcups2=2.2.1-8+deb9u2 \
    libcurl3=7.52.1-5+deb9u8 \
    libcurl3-gnutls=7.52.1-5+deb9u8 \
    libperl5.24=5.24.1-3+deb9u5 \
    libpython2.7-minimal=2.7.13-2+deb9u3 \
    libpython2.7-stdlib=2.7.13-2+deb9u3 \
    libsoup2.4-1=2.56.0-2+deb9u2 \
    libsoup-gnome2.4-1=2.56.0-2+deb9u2 \
    libsystemd0=232-25+deb9u8 \
    libudev1=232-25+deb9u8 \
    perl=5.24.1-3+deb9u5 \
    python2.7=2.7.13-2+deb9u3

# Environment variables
ENV ADOP_LDAP_ENABLED=true LDAP_IS_MODIFIABLE=true ADOP_ACL_ENABLED=true ADOP_SONAR_ENABLED=true ADOP_ANT_ENABLED=true ADOP_MAVEN_ENABLED=true ADOP_NODEJS_ENABLED=true ADOP_GITLAB_ENABLED=true
ENV LDAP_GROUP_NAME_ADMIN=""
ENV JENKINS_OPTS="--prefix=/jenkins -Djenkins.install.runSetupWizard=false"
ENV PLUGGABLE_SCM_PROVIDER_PROPERTIES_PATH="/var/jenkins_home/userContent/datastore/pluggable/scm"
ENV PLUGGABLE_SCM_PROVIDER_PATH="/var/jenkins_home/userContent/job_dsl_additional_classpath/"

RUN xargs /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt
RUN echo "KexAlgorithms curve25519-sha256@libssh.org,ecdh-sha2-nistp384,ecdh-sha2-nistp256,diffie-hellman-group14-sha256,diffie-hellman-group1-sha1" >> /etc/ssh/ssh_config

ENTRYPOINT ["/entrypoint.sh"]
