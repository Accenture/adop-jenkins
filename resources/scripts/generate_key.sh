#!/bin/bash
set -e

# Usage
usage() {
    echo "Usage:"
    echo "    ${0} -c <host> -p <port>"
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

while getopts "c:p:u:w:" opt; do
  case $opt in
    c)
      host=${OPTARG}
      ;;
    p)
      port=${OPTARG}
      ;;
    *)
      echo "Invalid parameter(s) or option(s)."
      usage
      ;;
  esac
done

if [ -z "${host}" ] || [ -z "${port}" ] ; then
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

  # Set correct permissions for Content Directory
  chown 1000:1000 "${JENKINS_USER_CONTENT_DIR}"
fi
# public_key_val=$(cat ${JENKINS_SSH_DIR}/id_rsa.pub)

# Set correct permissions on SSH Key
chown -R 1000:1000 "${JENKINS_SSH_DIR}"