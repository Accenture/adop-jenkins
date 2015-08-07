#!/bin/bash
set -e

# Usage
usage() {
    echo "Usage:"
    echo "    ${0} -c <CONSUL_HOST> -p <CONSUL_PORT>"
    exit 1
}

# Constants
SLEEP_TIME=5
BASE_JENKINS_KEY="adop/core/jenkins"
BASE_JENKINS_SSH_KEY="${BASE_JENKINS_KEY}/ssh"
BASE_JENKINS_SSH_PUBLIC_KEY_KEY="${BASE_JENKINS_SSH_KEY}/public_key"

JENKINS_SSH_DIR="/var/jenkins_home/.ssh"


while getopts "c:p:" opt; do
  case $opt in
    c)
      consul_host=${OPTARG}
      ;;
    p)
      consul_port=${OPTARG}
      ;;
    *)
      echo "Invalid parameter(s) or option(s)."
      usage
      ;;
  esac
done

if [ -z "${consul_host}" ] || [ -z "${consul_port}" ]; then
    echo "Parameters missing"
    usage
fi

echo "Generating Jenkins Key Pair"
if [ ! -d "${JENKINS_SSH_DIR}" ]; then mkdir -p "${JENKINS_SSH_DIR}"; fi
cd "${JENKINS_SSH_DIR}"
ssh-keygen -t rsa -f 'id_rsa' -b 4096 -C "jenkins@adop-core" -N ''
public_key_val=$(cat ${JENKINS_SSH_DIR}/id_rsa.pub)

echo "Testing Consul Connection"
until curl -sL -w "%{http_code}\\n" "http://${consul_host}:${consul_port}" -o /dev/null | grep "200" &> /dev/null
do
    echo "Consul unavailable, sleeping for ${SLEEP_TIME}"
    sleep "${SLEEP_TIME}"
done

echo "Consul available, adding data"
curl -X PUT -d "${public_key_val}" "http://${consul_host}:${consul_port}/v1/kv/${BASE_JENKINS_SSH_PUBLIC_KEY_KEY}"