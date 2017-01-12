#!/bin/bash
set -e

# Usage
usage() {
    echo "Usage:"
    echo "    ${0} -i <loader_id> -p <protocol> -h <host>"
    exit 1
}

# Constants
SCM_TYPE="gerrit"
LOADER_PATH="${PLUGGABLE_SCM_PROVIDER_PROPERTIES_PATH}/CartridgeLoader"
SCM_PROVIDERS_PATH="${PLUGGABLE_SCM_PROVIDER_PROPERTIES_PATH}/ScmProviders"
GERRIT_SSH_USER=$GERRIT_JENKINS_USERNAME
GERRIT_PERMISSIONS_PATH='${PROJECT_NAME}/permissions'
GERRIT_URL=$(echo ${ROOT_URL} | sed "s/jenkins/${GERRIT_HOST_NAME}/g")

while getopts "i:p:h:" opt; do
  case $opt in
    i)
      loader_id=${OPTARG}
      ;;
    p)
      protocol=${OPTARG}
      ;;
    h)
      host=${OPTARG}
      ;;
    *)
      echo "Invalid parameter(s) or option(s)."
      usage
      ;;
  esac
done

if [ -z "${loader_id}" ] || [ -z "${protocol}" ] || [ -z "${host}" ]; then
    echo "Parameters missing"
    usage
fi

# Setup Cartridge loader properties
LOADER_FILE=$LOADER_PATH/${loader_id}.props
touch $LOADER_FILE
cat > $LOADER_FILE <<EOF
loader.id=${loader_id}
gerrit.endpoint=${host}
gerrit.user=${GERRIT_SSH_USER}
gerrit.port=${GERRIT_SSH_PORT}
gerrit.protocol=ssh
gerrit.permissions.path=${GERRIT_PERMISSIONS_PATH}
gerrit.permissions.with_review.path=${GERRIT_PERMISSIONS_PATH}-with-review
EOF

# Setup SCM Provider properties
GERRIT_PROVIDER_ID=${loader_id}-{protocol}
case $protocol in
ssh)
  PROPS_FILE=${SCM_PROVIDERS_PATH}/${loader_id}-${protocol}.props
  touch $PROPS_FILE

cat > $PROPS_FILE <<EOF
scm.loader.id=${loader_id}
scm.id=${loader_id}-ssh
scm.type=${SCM_TYPE}
scm.code_review.enabled=true
scm.protocol=ssh
scm.port=${GERRIT_SSH_PORT}
scm.host=${host}
scm.url=${GERRIT_URL}

scm.gerrit.server.profile=${GERRIT_PROFILE}
scm.gerrit.ssh.clone.user=${GERRIT_SSH_USER}
EOF
  ;;
http)
  PROPS_FILE=${SCM_PROVIDERS_PATH}/${loader_id}-${protocol}.props
  touch $PROPS_FILE

  cat > $PROPS_FILE <<EOF
scm.loader.id=${loader_id}
scm.id=${loader_id}-http
scm.type=${SCM_TYPE}
scm.code_review.enabled=true
scm.protocol=http
scm.port=${GERRIT_PORT}
scm.host=${host}
scm.url=${GERRIT_URL}

scm.gerrit.server.profile=${GERRIT_PROFILE}
EOF
  ;;
*)
  echo "Invalid protocol: Must use http or ssh"
  usage
  ;;
esac

