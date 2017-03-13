import hudson.plugins.active_directory.* 
import jenkins.model.*

def env = System.getenv()

// Check if AD security realm is enabled
if (!env['ADOP_AD_ENABLED'].toBoolean()) {
    println "--> ADOP Securiy Realm is Disabled"
    return
}

// Variables
def ad_server = env['AD_SERVER']
def ad_domain_name = env['AD_DOMAIN_NAME']
def ad_domain_cont = env['AD_DOMAIN_CONT']
def ad_site = env['AD_SITE']
def ad_bindDN = env['AD_BIND_DN']
def ad_bindDN_pass = env['AD_BIND_DN_PASS']

// Constants
def instance = Jenkins.getInstance();

def ActiveDirectoryDomain adDomain = new ActiveDirectoryDomain(ad_domain_name, ad_domain_cont);
def domains = new ArrayList<ActiveDirectoryDomain>();
domains.add(adDomain);

println "--> Configuring Active Directory"

def securityRealm = new ActiveDirectorySecurityRealm(
ad_domain_name, //String domain
domains,
ad_site, //String site
ad_bindDN, //String bindName
ad_bindDN_pass, //String bindPassword
ad_server, //String server
GroupLookupStrategy.RECURSIVE,
false,
true,
null)

instance.setSecurityRealm(securityRealm) 
instance.save()
