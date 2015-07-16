import hudson.model.*;
import jenkins.model.*;
import hudson.security.*;
import jenkins.security.plugins.ldap.*;
import hudson.util.Secret;

// Variables
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

// Constants
def instance = Jenkins.getInstance()

// env['USERNAME']

Thread.start {
    sleep 10000

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

    // Save the state
    instance.save()
}