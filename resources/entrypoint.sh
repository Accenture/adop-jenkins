#!/bin/bash

echo "Genarate JENKINS SSH KEY and add it to gerrit"
host=$GERRIT_HOST_NAME
port=$GERRIT_PORT
gerrit_provider_id="adop-gerrit"
gerrit_protocol="ssh"
username=$GERRIT_JENKINS_USERNAME
password=$GERRIT_JENKINS_PASSWORD
nohup /usr/share/jenkins/ref/adop\_scripts/generate_key.sh -c ${host} -p ${port} -u ${username} -w ${password} &

echo "Setting up your default SCM provider - Gerrit..."
mkdir -p $PLUGGABLE_SCM_PROVIDER_PROPERTIES_PATH $PLUGGABLE_SCM_PROVIDER_PATH
mkdir -p ${PLUGGABLE_SCM_PROVIDER_PROPERTIES_PATH}/CartridgeLoader ${PLUGGABLE_SCM_PROVIDER_PROPERTIES_PATH}/ScmProviders
nohup /usr/share/jenkins/ref/adop\_scripts/generate_gerrit_scm.sh -i ${gerrit_provider_id} -p ${gerrit_protocol} -h ${host} &

echo "Tokenising scriptler scripts..."
sed -i "s,###SCM_PROVIDER_PROPERTIES_PATH###,$PLUGGABLE_SCM_PROVIDER_PROPERTIES_PATH,g" /usr/share/jenkins/ref/scriptler/scripts/retrieve_scm_props.groovy

echo "skip upgrade wizard step after installation"
echo "2.7.4" > /var/jenkins_home/jenkins.install.UpgradeWizard.state

echo "start JENKINS"

chown -R 1000:1000 /var/jenkins_home
su jenkins -c /usr/local/bin/jenkins.sh
