#!/usr/bin/env bash
set -e

# Usage
usage() {
    echo "Usage:"
    echo "    ${0} -i <loader_id> -p <protocol1> -p <protocol2> -h <host> -c <context>"
    exit 1
}

# Constants
SCM_TYPE="gitlab"
LOADER_PATH="${PLUGGABLE_SCM_PROVIDER_PROPERTIES_PATH}/CartridgeLoader"
SCM_PROVIDERS_PATH="${PLUGGABLE_SCM_PROVIDER_PROPERTIES_PATH}/ScmProviders"
GITLAB_SSH_USER=$GITLAB_JENKINS_USERNAME
GITLAB_URL=$(echo ${ROOT_URL} | sed "s/jenkins/${GITLAB_HOST_NAME}/g")
GITLAB_SSH_URL="ssh://git@gitlab"

protocols=()
while getopts "i:p:h:c:" opt; do
  case $opt in
    i)
      loader_id=${OPTARG}
      ;;
    p)
      protocols+=(${OPTARG})
      ;;
    h)
      host=${OPTARG}
      ;;
    c)
      context=${OPTARG}
      ;;  
    *)
      echo "Invalid parameter(s) or option(s)."
      usage
      ;;
  esac
done

if [ -z "${loader_id}" ] || [ -z "${protocols}" ] || [ -z "${host}" ] || [ -z "${context}" ]; then
    echo "Parameters missing"
    usage
fi

# Setup Cartridge loader properties
LOADER_FILE=$LOADER_PATH/${loader_id}.props
touch $LOADER_FILE
cat > $LOADER_FILE <<EOF
loader.id=${loader_id}
gitlab.endpoint=${host}
gitlab.port=${GITLAB_PORT}
gitlab.protocol=http
gitlab.context=${context}
EOF

# Setup SCM Provider properties
for protocol in "${protocols[@]}"
do
case $protocol in
ssh)
 PROPS_FILE=${SCM_PROVIDERS_PATH}/${loader_id}-${protocol}.props
  touch $PROPS_FILE

cat > $PROPS_FILE <<EOF
scm.loader.id=${loader_id}
scm.id=${loader_id}-ssh
scm.type=${SCM_TYPE}
scm.protocol=ssh
scm.port=${GITLAB_SSH_PORT}
scm.host=${host}
scm.url=${GITLAB_SSH_URL}
EOF
 ;;

http)
 PROPS_FILE=${SCM_PROVIDERS_PATH}/${loader_id}-${protocol}.props
 touch $PROPS_FILE

cat > $PROPS_FILE <<EOF
scm.loader.id=${loader_id}
scm.id=${loader_id}-http
scm.type=gitlab
scm.protocol=http
scm.port=${GITLAB_PORT}
scm.host=${host}
scm.context=${context}
scm.url=http://gitlab/gitlab/
EOF
  ;;
*)
  echo "Invalid protocol: Must use http or ssh"
  usage
  ;;
esac

done

