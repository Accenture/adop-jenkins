#Supported tags and respective Dockerfile links

- [`0.1.0`, `0.1.0` (*0.1.0/Dockerfile*)](https://github.com/Accenture/adop-jenkins/blob/master/Dockerfile.md)

# What is adop-jenkins?

adop-jenkins is a wrapper for the jenkins image. It has primarily been built to perform extended configuration.
Jenkins is an open source automation tool.

# How to use this image

The easiest for to run docker-jenkins image is as follow:
```
docker run --name <your-container-name> -d -p 8080:8080 adop/jenkins:VERSION
```
after the above jenkins will be available at: http://localhost:8080

Runtime configuration can be provided using environment variables:

* JENKINS_OPTS, jenkins startup options.
* ADOP_LDAP_ENABLED, allow enable/disable LDAP authentication. Default to true (enabled).
* ADOP_SONAR_ENABLED, allow enable/disable Jenkins-Sonar integrations. Default to true (enabled).
* ADOP_ANT_ENABLED, allow enable/disable Jenkins-ANT integration. Default to true (enabled).
* ADOP_MAVEN_ENABLED, allow enable/disable Jenkins-MAVEN integration. Default to true (enabled).
* ADOP_NODEJS_ENABLED, allow enable/disable Jenkins-NODEJS integration. Default to true (enabled).
* ADOP_GERRIT_ENABLED, allow enable/disable Jenkins-GERRIT integration. Default to true (enabled).

Additional environment variables that allow fine tune Jenkins runtime configuration are:

* GERRIT_HOST_NAME, the gerrit hostname. Default to gerrit
* GERRIT_PORT, the port gerrit APIs are exposed. Default to 8080
* GERRIT_JENKINS_USERNAME, the username Jenkins will use to connect to Gerrit. Default to gerrit.
* GERRIT_JENKINS_PASSWORD, the password Jenkins will use to connect to Gerrit. Default to gerrit.
* GERRIT_FRONT_END_URL, the URL for gerrit frontend.
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

## Run docker-jenkins with OpenLDAP
The following assumes that MySQL and OpenLDAP are running.

The following command will run adop-jenkins and connect it to OpenLDAP
```
  docker run \
  --name adop-gerrit \
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
  -d docker.accenture.com/adop/docker-jenkins:VERSION
```

# License
Please view [licence information](LICENCE.md) for the software contained on this image.

#Supported Docker versions

This image is officially supported on Docker version 1.9.1.
Support for older versions (down to 1.6) is provided on a best-effort basis.

# User feedback

## Documentation
Documentation for this image is available in the [Jenkins documentation page](https://wiki.jenkins-ci.org/display/JENKINS/Home). 
Additional documentaion can be found under the [`docker-library/docs` GitHub repo](https://github.com/docker-library/docs). Be sure to familiarize yourself with the [repository's `README.md` file](https://github.com/docker-library/docs/blob/master/README.md) before attempting a pull request.

## Issues
If you have any problems with or questions about this image, please contact us through a [GitHub issue](https://github.com/Accenture/adop-jenkins/issues).

## Contribute
You are invited to contribute new features, fixes, or updates, large or small; we are always thrilled to receive pull requests, and do our best to process them as fast as we can.

Before you start to code, we recommend discussing your plans through a [GitHub issue](https://github.com/Accenture/adop-jenkins/issues), especially for more ambitious contributions. This gives other contributors a chance to point you in the right direction, give you feedback on your design, and help you find out if someone else is working on the same thing.
