import hudson.model.*;
import jenkins.model.*;
import hudson.security.*;
import jenkins.security.plugins.ldap.*;
import hudson.util.Secret;
import hudson.plugins.sonar.*;
import hudson.plugins.sonar.model.TriggersConfig;
import hudson.tools.*

// Variables
def root_Url = "http://52.16.250.144/jenkins/"

def ldap_server = "10.0.0.198:389"
def ldap_rootDN = "dc=accenture.com,dc=monsoon,dc=accenture,dc=com"
def ldap_userSearchBase = ""
def ldap_userSearch = "uid={0}"
def ldap_groupSearchBase = ""
def ldap_groupSearchFilter = null
def ldap_groupMembershipFilter = ""
def ldap_managerDN = "cn=admin,dc=accenture.com,dc=monsoon,dc=accenture,dc=com"
def ldap_managerPassword = "Sw4syJSWQRx2AK6KE3vbhpmL"
def ldap_inhibitInferRootDN = false
def ldap_disableMailAddressResolver = false
def ldap_displayNameAttributeName = ""
def ldap_mailAddressAttributeName = ""

def sonar_server_url = "http://10.0.0.197:8020/sonar/"
def sonar_account_login = "admin"
def sonar_account_password = "YWRtaW4="
def sonar_db_url = "jdbc:mysql://10.0.0.197:3306/sonar?useUnicode=true&amp;characterEncoding=utf8"
def sonar_db_login = "sonar"
def sonar_db_password = "c29uYXI="
def sonar_plugin_version = ""
def sonar_additional_props = ""

def sonar_runner_version = "2.4"

// Constants
def instance = Jenkins.getInstance()

// env['USERNAME']

Thread.start {
    sleep 10000
    println "--> ** Configuring Jenkins for ADOP **"

    // Base URL
    println "--> Setting Base URL"
    jlc = JenkinsLocationConfiguration.get()
    jlc.setUrl(root_Url)
    jlc.save()

    // Global Environment Variables
    // Source: https://groups.google.com/forum/#!topic/jenkinsci-users/KgCGuDmED1Q
    globalNodeProperties = instance.getGlobalNodeProperties()
    envVarsNodePropertyList = globalNodeProperties.getAll(hudson.slaves.EnvironmentVariablesNodeProperty.class)

    newEnvVarsNodeProperty = null
    envVars = null

    if ( envVarsNodePropertyList == null || envVarsNodePropertyList.size() == 0 ) {
      newEnvVarsNodeProperty = new hudson.slaves.EnvironmentVariablesNodeProperty();
      globalNodeProperties.add(newEnvVarsNodeProperty)
      envVars = newEnvVarsNodeProperty.getEnvVars()
    } else {
      envVars = envVarsNodePropertyList.get(0).getEnvVars()
    }

    // Example
    //envVars.put("FOO", "bar")

    // Save the state
    instance.save()

    // LDAP
    println "--> Configuring LDAP"

    def ldapRealm = new LDAPSecurityRealm(
        ldap_server, //String server
        ldap_rootDN, //String rootDN
        ldap_userSearchBase, //String userSearchBase
        ldap_userSearch, //String userSearch
        ldap_groupSearchBase, //String groupSearchBase
        ldap_groupSearchFilter, //String groupSearchFilter
        new FromGroupSearchLDAPGroupMembershipStrategy(ldap_groupMembershipFilter), //LDAPGroupMembershipStrategy groupMembershipStrategy
        ldap_managerDN, //String managerDN
        Secret.fromString(ldap_managerPassword), //Secret managerPasswordSecret
        ldap_inhibitInferRootDN, //boolean inhibitInferRootDN
        ldap_disableMailAddressResolver, //boolean disableMailAddressResolver
        null, //CacheConfiguration cache
        null, //EnvironmentProperty[] environmentProperties
        ldap_displayNameAttributeName, //String displayNameAttributeName
        ldap_mailAddressAttributeName, //String mailAddressAttributeName
        IdStrategy.CASE_INSENSITIVE, //IdStrategy userIdStrategy
        IdStrategy.CASE_INSENSITIVE //IdStrategy groupIdStrategy >> defaults
    )

    instance.setSecurityRealm(ldapRealm)

    def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
    instance.setAuthorizationStrategy(strategy)

    // Sonar
    // Source: http://pghalliday.com/jenkins/groovy/sonar/chef/configuration/management/2014/09/21/some-useful-jenkins-groovy-scripts.html
    println "--> Configuring SonarQube"
    def desc_SonarPublisher = instance.getDescriptor("hudson.plugins.sonar.SonarPublisher")

    def sonar_inst = new SonarInstallation(
      "Sonar", // Name
      false, // Disabled?
      sonar_server_url,
      sonar_db_url,
      sonar_db_login,
      sonar_db_password,
      sonar_plugin_version,
      sonar_additional_props,
      new TriggersConfig(),
      sonar_account_login,
      sonar_account_password
    )
    desc_SonarPublisher.setInstallations(sonar_inst)
    desc_SonarPublisher.save()
    
    // Sonar Runner
    // Source: http://pghalliday.com/jenkins/groovy/sonar/chef/configuration/management/2014/09/21/some-useful-jenkins-groovy-scripts.html
    println "--> Configuring SonarRunner"
    def desc_SonarRunnerInst = instance.getDescriptor("hudson.plugins.sonar.SonarRunnerInstallation")

    def sonarRunnerInstaller = new SonarRunnerInstaller(sonar_runner_version)
    def installSourceProperty = new InstallSourceProperty([sonarRunnerInstaller])
    def sonarRunner_inst = new SonarRunnerInstallation("SonarRunner " + sonar_runner_version, "", [installSourceProperty])
    desc_SonarRunnerInst.setInstallations(sonarRunner_inst)

    desc_SonarRunnerInst.save()

    // Save the state
    instance.save()
}