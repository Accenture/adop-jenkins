#!/bin/bash
set -e

# Usage
usage() {
    echo "Usage:"
    echo "    ${0} -c <host> -p <port> -u <username> -w <password>"
    exit 1
}

# Constants
SLEEP_TIME=5
MAX_RETRY=10
BASE_JENKINS_KEY="adop/core/jenkins"
BASE_JENKINS_SSH_KEY="${BASE_JENKINS_KEY}/ssh"
BASE_JENKINS_SSH_PUBLIC_KEY_KEY="${BASE_JENKINS_SSH_KEY}/public_key"
JENKINS_HOME="/var/jenkins_home"
JENKINS_SSH_DIR="${JENKINS_HOME}/.ssh"
JENKINS_USER_CONTENT_DIR="${JENKINS_HOME}/userContent/"
GERRIT_ADD_KEY_PATH="accounts/self/sshkeys"
GERRIT_REST_AUTH="jenkins:jenkins"


while getopts "c:p:u:w:" opt; do
  case $opt in
    c)
      host=${OPTARG}
      ;;
    p)
      port=${OPTARG}
      ;;
    u)
      username=${OPTARG}
      ;;
    w)
      password=${OPTARG}
      ;;
    *)
      echo "Invalid parameter(s) or option(s)."
      usage
      ;;
  esac
done

if [ -z "${host}" ] || [ -z "${port}" ] || [ -z "${username}" ] || [ -z "${password}" ]; then
    echo "Parameters missing"
    usage
fi

echo "Generating Jenkins Key Pair"
if [ ! -d "${JENKINS_SSH_DIR}" ]; then mkdir -p "${JENKINS_SSH_DIR}"; fi
cd "${JENKINS_SSH_DIR}"

if [[ ! $(ls -A "${JENKINS_SSH_DIR}") ]]; then 
  ssh-keygen -t rsa -f 'id_rsa' -b 4096 -C "jenkins@adop-core" -N ''; 
  echo "Copy key to userContent folder"
  mkdir -p ${JENKINS_USER_CONTENT_DIR}
  rm -f ${JENKINS_USER_CONTENT_DIR}/id_rsa.pub
  cp ${JENKINS_SSH_DIR}/id_rsa.pub ${JENKINS_USER_CONTENT_DIR}/id_rsa.pub
fi
# public_key_val=$(cat ${JENKINS_SSH_DIR}/id_rsa.pub)

# Set correct permissions on SSH Key
chown -R 1000:1000 "${JENKINS_SSH_DIR}"

# echo "Testing Gerrit Connection"
# until curl -sL -w "\\n%{http_code}\\n" "http://${host}:${port}/gerrit" -o /dev/null | grep "200" &> /dev/null
# do
#     echo "Gerrit unavailable, sleeping for ${SLEEP_TIME}"
#     sleep "${SLEEP_TIME}"
# done

# echo "Gerrit available, adding data"
# count=1
# until [ $count -ge ${MAX_RETRY} ]
# do
#   ret=$(curl -X POST --write-out "%{http_code}" --silent --output /dev/null \
#           -u "${username}:${password}" \
#           -H "Content-type: text/plain" \
#           --data "${public_key_val}" "http://${host}:${port}/gerrit/a/${GERRIT_ADD_KEY_PATH}")
#   [[ ${ret} -eq 201  ]] && break
#   count=$[$count+1]
#   echo "Unable to add jenkins public key on gerrit, response code ${ret}, retry ... ${count}"
#   sleep ${SLEEP_TIME}
# done
