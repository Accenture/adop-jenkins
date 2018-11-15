# Supported tags and respective `Dockerfile` links

- [`0.2.0`, `0.2.0` (*0.2.0/Dockerfile*)](https://github.com/Accenture/adop-jenkins/blob/0.2.0/Dockerfile)

# What is adop-jenkins?

adop-jenkins is a wrapper for the Jenkins image. It has primarily been built to perform extended configuration.
Jenkins is an open source automation tool.

# How to use this image

The easiest for to run adop-jenkins image is as follow:
```
docker run --name <your-container-name> -d -p 8080:8080 accenture/adop-jenkins:VERSION
```
after the above Jenkins will be available at: http://localhost:8080

Runtime configuration can be provided using environment variables:

* JENKINS_OPTS, Jenkins startup options.
* ADOP_LDAP_ENABLED, allow enable/disable LDAP authentication. Default to true (enabled).
* ADOP_ACL_ENABLED, allow enable/disable Jenkins user access control lists.
* ADOP_SONAR_ENABLED, allow enable/disable Jenkins-Sonar integrations. Default to true (enabled).
* ADOP_ANT_ENABLED, allow enable/disable Jenkins-ANT integration. Default to true (enabled).
* ADOP_MAVEN_ENABLED, allow enable/disable Jenkins-MAVEN integration. Default to true (enabled).
* ADOP_NODEJS_ENABLED, allow enable/disable Jenkins-NODEJS integration. Default to true (enabled).
* ADOP_GITLAB_ENABLED, allow enable/disable Jenkins-GITLAB integration. Default to true (enabled).

Additional environment variables that allow fine tune Jenkins runtime configuration are:

* GITLAB_HOST_NAME, the Gitlab hostname. Default to gitlab
* GITLAB_PORT, the port Gitlab APIs are exposed. Default to 80
* GITLAB_JENKINS_USERNAME, the username Jenkins will use to connect to Gitlab. Default to Gitlab. 
* GITLAB_JENKINS_PASSWORD, the password Jenkins will use to connect to Gitlab. Default to gitlab.
* INITIAL_ADMIN_USER, the username for the admin user.
* INITIAL_ADMIN_PASSWORD, the password for the initial admin user.
* LDAP_SERVER, the LDPA URI, i.e. ldap-host:389
* LDAP_ROOTDN, the LDAP BASE_DN
* LDAP_USER_SEARCH_BASE, base organization unit to use to search for users
* LDAP_USER_SEARCH, LDAP object field to use for the search query
* LDAP_GROUP_SEARCH_BASE, base organization unit to use to search for groups
* LDAP_GROUP_SEARCH_FILTER, filter to use querying for groups
* LDAP_GROUP_MEMBERSHIP_FILTER, filter to use when writing queries to verify if a user is member of a group
* LDAP_MANAGER_DN, LDAP adim user
* LDAP_MANAGER_PASSWORD, LDAP admin password
* LDAP_INHIBIT_INFER_ROOTDN, flag indicating if ROOT_DN should be infered
* LDAP_DISPLAY_NAME_ATTRIBUTE_NAME, LDAP object field used as a display name
* LDAP_DISABLE_MAIL_ADDRESS_RESOLVER, flag indicating if the email address resolver should be disabled
* LDAP_MAIL_ADDRESS_ATTRIBUTE_NAME, LDAP object field used as a email address
* LDAP_GROUP_NAME_ADMIN, LDAP admin group. Default to administrators.
* SONAR_SERVER_URL, the sonar server URL
* SONAR_ACCOUNT_LOGIN, username to use when connecting to sonar
* SONAR_ACCOUNT_PASSWORD, password to use when connecting to sonar
* SONAR_DB_URL, sonar database JDBC connection string
* SONAR_DB_LOGIN, username to use to connect to sonar DB
* SONAR_DB_PASSWORD, password to use when connecting to sonar DB
* SONAR_PLUGIN_VERSION, the sonar plugin version
* SONAR_ADDITIONAL_PROPS, additional properties for sonar plugin. Refer to [SonarQube documentation](http://docs.sonarqube.org/display/SONAR/Analyzing+with+SonarQube+Scanner+for+Jenkins) for more informattion
* SONAR_RUNNER_VERSION, the sonar runner version
* ANT_VERSION, ANT release version
* MAVEN_VERSION, Maven release version
* NODEJS_VERSION, nodejs release version
* NODEJS_GLOBAL_PACKAGES, nodejs packages to be installed as global
* NODEJS_PACKAGES_REFRESH_HOURS, nodejs package refresh time in hours.
* GIT_GLOBAL_CONFIG_NAME, Git global config name
* GIT_GLOBAL_CONFIG_EMAIL, Git global config email
* AWS_DEFAULT_REGION, set the AWS default region for the CLI at a global level
* DOCKER_TLS_VERIFY, Docker CLI variable to declare a TLS-enabled engine
* DOCKER_HOST, Docker CLI variable to declare the endpoint to target
* DOCKER_CERT_PATH, Docker CLI variable to declare the path to the certificate
* DOCKER_NETWORK_NAME, the Docker custom network to launch containers on
* GROOVY_VERSION, a comma delimited list of Groovy installation profiles to install (e.g. 2.4.8, 2.4.3).
* LDAP_IS_MODIFIABLE, allows us to interact with LDAP configuration through jenkins, Allowed values true (default) and false. If set to true, LDAP can be modified and jenkins will be able to create necessary users/groups in LDAP. If set to false, LDAP can not be modified and jenkins need be configured to use existing users/groups in LDAP. This variable will be used when ADOP_LDAP_ENABLED is set to true.

## Run adop-jenkins with OpenLDAP
The following assumes that MySQL and OpenLDAP are running.

The following command will run adop-jenkins and connect it to OpenLDAP
```
  docker run \
  --name adop-jenkins \
  -p 8080:8080 \
  -e LDAP_SERVER="ldap:389" \
  -e LDAP_ROOTDN="${LDAP_FULL_DOMAIN}" \
  -e LDAP_USER_SEARCH_BASE="ou=people" \
  -e LDAP_USER_SEARCH="uid={0}" \
  -e LDAP_GROUP_SEARCH_BASE="ou=groups" \
  -e LDAP_GROUP_SEARCH_FILTER="" \
  -e LDAP_GROUP_MEMBERSHIP_FILTER="" \
  -e LDAP_MANAGER_DN="cn=admin,${LDAP_FULL_DOMAIN}" \
  -e LDAP_MANAGER_PASSWORD=${LDAP_PWD} \
  -e LDAP_INHIBIT_INFER_ROOTDN="false" \
  -e LDAP_DISABLE_MAIL_ADDRESS_RESOLVER="false" \
  -e LDAP_DISPLAY_NAME_ATTRIBUTE_NAME="displayName" \
  -e LDAP_MAIL_ADDRESS_ATTRIBUTE_NAME="mail" \
  -d accenture/adop-jenkins:VERSION
```

# License
Please view [license information](LICENSE.md) for the software contained on this image.

# Supported Docker versions

This image is officially supported on Docker version 1.9.1.
Support for older versions (down to 1.6) is provided on a best-effort basis.

# User feedback

## Documentation
Documentation for this image is available in the [Jenkins documentation page](https://wiki.jenkins-ci.org/display/JENKINS/Home).

## Issues
If you have any problems with or questions about this image, please contact us through a [GitHub issue](https://github.com/Accenture/adop-jenkins/issues).

## Contribute
You are invited to contribute new features, fixes, or updates, large or small; we are always thrilled to receive pull requests, and do our best to process them as fast as we can.

Before you start to code, we recommend discussing your plans through a [GitHub issue](https://github.com/Accenture/adop-jenkins/issues), especially for more ambitious contributions. This gives other contributors a chance to point you in the right direction, give you feedback on your design, and help you find out if someone else is working on the same thing.
