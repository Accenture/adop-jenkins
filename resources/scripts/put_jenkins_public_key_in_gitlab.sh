#!/usr/bin/env bash

# This script uses the GitLab API to copy over Jenkins's public key over to GitLab
# It assumes that an access token for the GitLab user Jenkins has been created 
# and exported as GITLAB_JENKINS_PRIVATE_TOKEN

PUBLIC_KEY="$(cat /var/jenkins_home/userContent/id_rsa.pub)"
PRIVATE_TOKEN=$GITLAB_AUTH_TOKEN
PUBLIC_KEY_NAME="adop-jenkins-master"

if [ -z "$PRIVATE_TOKEN" ]
then 
        echo "========= [PUBLIC KEY COPYING SCRIPT] SCRIPT FAILED. The GITLAB_JENKINS_PRIVATE_TOKEN environment variable is empty ========="
        exit 1
fi

if [ -z "$PUBLIC_KEY" ]
then
      echo "========= [PUBLIC KEY COPYING SCRIPT] SCRIPT FAILED. Could not find Jenkins's public key at /var/jenkins_home/userContent/id_rsa.pub ========="
else
      curl -H "Private-Token: ${PRIVATE_TOKEN}" -X POST http://gitlab/gitlab/api/v4/user/keys --data "title=$PUBLIC_KEY_NAME" --data-urlencode "key=$PUBLIC_KEY"
fi

