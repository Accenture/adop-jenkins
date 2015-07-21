import hudson.model.*;
import jenkins.model.*;
import hudson.security.*;
import jenkins.security.plugins.ldap.*;
import hudson.util.Secret;

// Check if enabled
def env = System.getenv()
if (!env['ADOP_LDAP_ENABLED'].toBoolean()) {
    println "--> ADOP LDAP Disabled"
    return
}

// Variables
def ldap_server = env['LDAP_SERVER']
def ldap_rootDN = env['LDAP_ROOTDN']
def ldap_userSearchBase = env['LDAP_USER_SEARCH_BASE']
def ldap_userSearch = env['LDAP_USER_SEARCH']
def ldap_groupSearchBase = env['LDAP_GROUP_SEARCH_BASE']
def ldap_groupSearchFilter = env['LDAP_GROUP_SEARCH_FILTER']
def ldap_groupMembershipFilter = env['LDAP_GROUP_MEMBERSHIP_FILTER']
def ldap_managerDN = env['LDAP_MANAGER_DN']
def ldap_managerPassword = env['LDAP_MANAGER_PASSWORD']
def ldap_inhibitInferRootDN = env['LDAP_INHIBIT_INFER_ROOTDN'].toBoolean()
def ldap_disableMailAddressResolver = env['LDAP_DISABLE_MAIL_ADDRESS_RESOLVER'].toBoolean()
def ldap_displayNameAttributeName = env['LDAP_DISPLAY_NAME_ATTRIBUTE_NAME']
def ldap_mailAddressAttributeName = env['LDAP_MAIL_ADDRESS_ATTRIBUTE_NAME']

// Constants
def instance = Jenkins.getInstance()

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