#!/bin/bash
# Script heavily based on the code from https://stackoverflow.com/questions/47948887/login-to-gitlab-using-curl
#                                       https://gist.github.com/michaellihs/5ef5e8dbf48e63e2172a573f7b32c638

gitlab_host="http://${GITLAB_HOST}/gitlab"
gitlab_user=$GITLAB_USERNAME
gitlab_password=$GITLAB_PASSWORD
gitlab_basic_auth_string="Basic $(echo -n $gitlab_user:$gitlab_password | base64)"

pretty_sleep() {
  secs=${1:-60}
  tool=${2:-service}
  while [ $secs -gt 0 ]; do
    echo -ne "$tool unavailable, sleeping for: $secs\033[0Ks\r"
    sleep 1
    : $((secs--))
  done
  echo "$tool was unavailable, so slept for: ${1:-60} secs"
}

# Check that Gitlab service has been stood up and is running healthily
echo "* Waiting for GitLab to become available - this can take a few minutes"
TOOL_SLEEP_TIME=60
until [[ $(curl -s ${gitlab_host}/-/health) == 'GitLab OK' ]]; do pretty_sleep ${TOOL_SLEEP_TIME} GitLab; done

echo "###########################################"
echo "Token generation script starting"
echo "###########################################"

# 1. curl for the login page to get a session cookie and the sources with the auth tokens
echo "[Token generation script]: Curling login page......."
body_header=$(curl -H "Authorization: ${gitlab_basic_auth_string}" -c cookies.txt -i "${gitlab_host}/users/sign_in")

# grep the auth token for the user login for
echo "[Token generation script]: Extracting the CSRF token from the login page......."
csrf_token=$(echo $body_header | perl -ne 'print "$1\n" if /new_user.*?authenticity_token"[[:blank:]]value="(.+?)"/' | sed -n 1p)
echo "[Token generation script]: Extracted the CSRF token for the login page: $csrf_token"

# 2. send login credentials with curl, using cookies and token from previous request
echo "[Token generation script]: Logging in to Gitlab......."
curl -s -H "Authorization: ${gitlab_basic_auth_string}" -b cookies.txt -c cookies.txt -i "${gitlab_host}/users/auth/ldapmain/callback" \
                        --data "username=${gitlab_user}&password=${gitlab_password}" \
                        --data-urlencode "authenticity_token=${csrf_token}" \
                        > /dev/null

# 3. send curl GET request to personal access token page to get the CSRF token
echo "\n[Token generation script]: Curling the personal access token form page to get the CSRF token......."
body_header=$(curl -H "Authorization: ${gitlab_basic_auth_string}" -H 'user-agent: curl' -b cookies.txt -i "${gitlab_host}/profile/personal_access_tokens" -s)
csrf_token=$(echo $body_header | perl -ne 'print "$1\n" if /authenticity_token"[[:blank:]]value="(.+?)"/' | sed -n 1p)
echo "[Token generation script]: Extracted the CSRF token from the access token form page: $csrf_token"

# 4. curl POST request to send the "generate personal access token form"
#      the response will be a redirect, so we have to follow using `-L`
echo "[Token generation script]: Submitting the personal access token page form......."
body_header=$(curl -H "Authorization: ${gitlab_basic_auth_string}" -L -b cookies.txt "${gitlab_host}/profile/personal_access_tokens" \
                        --data-urlencode "authenticity_token=${csrf_token}" \
                        --data 'personal_access_token[name]='"${gitlab_user}"'&personal_access_token[expires_at]=&personal_access_token[scopes][]=api')

# 5. Scrape the personal access token from the response HTML
echo "[Token generation script]: Getting the personal access token......."
personal_access_token=$(echo $body_header | perl -ne 'print "$1\n" if /created-personal-access-token"[[:blank:]]value="(.+?)"/' | sed -n 1p)
echo "[Token generation script]: Personal access token: ${personal_access_token}"
export GITLAB_AUTH_TOKEN=${personal_access_token}
