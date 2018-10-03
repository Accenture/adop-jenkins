#!/bin/bash

context="gitlab"
gitlab_protocol="ssh"
gitlab_protocol_2="http"
host=$GITLAB_HOST
port=$GITLAB_PORT
gitlab_provider_id="adop-gitlab"

echo "Genarate JENKINS SSH KEY"
nohup /usr/share/jenkins/ref/adop\_scripts/generate_key.sh -c ${host} -p ${port} &

echo "Setting up your default SCM provider - Gitlab..."
mkdir -p $PLUGGABLE_SCM_PROVIDER_PROPERTIES_PATH $PLUGGABLE_SCM_PROVIDER_PATH
mkdir -p ${PLUGGABLE_SCM_PROVIDER_PROPERTIES_PATH}/CartridgeLoader ${PLUGGABLE_SCM_PROVIDER_PROPERTIES_PATH}/ScmProviders
nohup /usr/share/jenkins/ref/adop\_scripts/generate_gitlab_scm.sh -i ${gitlab_provider_id} -p ${gitlab_protocol} -p ${gitlab_protocol_2} -h ${host} -c ${context} &

echo "Generate Sonar authentication token"
source /usr/share/jenkins/ref/adop\_scripts/generate_sonar_auth_token.sh

echo "Generate GitLab authentication token"
. /usr/share/jenkins/ref/adop\_scripts/generate_gitlab_auth_token.sh

echo "Copy Jenkins's public key to GitLab"
source /usr/share/jenkins/ref/adop\_scripts/put_jenkins_public_key_in_gitlab.sh

echo "skip upgrade wizard step after installation"
echo "2.7.4" > /var/jenkins_home/jenkins.install.UpgradeWizard.state

echo "start JENKINS"

chown -R 1000:1000 /var/jenkins_home
su jenkins -c /usr/local/bin/jenkins.sh
