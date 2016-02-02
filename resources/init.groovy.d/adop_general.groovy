import hudson.model.*;
import jenkins.model.*;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey;
import com.cloudbees.plugins.credentials.CredentialsScope;

// Variables
def env = System.getenv()
def root_Url = env['ROOT_URL']
def gitGlobalConfigName = env['GIT_GLOBAL_CONFIG_NAME']
def gitGlobalConfigEmail = env['GIT_GLOBAL_CONFIG_EMAIL']
def awsDefaultRegion = env['AWS_DEFAULT_REGION']
def dockerTLSVerify = env['DOCKER_TLS_VERIFY']
def dockerNetworkName = env['DOCKER_NETWORK_NAME']

// Constants
def instance = Jenkins.getInstance()

Thread.start {
    println "--> Configuring General Settings"

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
    if ( awsDefaultRegion != null ) {
	envVars.put("AWS_DEFAULT_REGION", awsDefaultRegion)
    }
    
    // Set Docker environment
    if ( dockerTLSVerify != null && dockerTLSVerify.toBoolean()) {
        envVars.put("DOCKER_TLS_VERIFY", env['DOCKER_TLS_VERIFY'])
        envVars.put("DOCKER_HOST", env['DOCKER_HOST'])
        envVars.put("DOCKER_CERT_PATH", env['DOCKER_CLIENT_CERT_PATH'])
	envVars.put("DOCKER_NETWORK_NAME", env['DOCKER_NETWORK_NAME'])
    }

    // Jenkins SSH Credentials
    println "--> Registering SSH Credentials"
    def system_credentials_provider = SystemCredentialsProvider.getInstance()

    def ssh_key_description = "ADOP Jenkins Master"

    ssh_credentials_exist = false
    system_credentials_provider.getCredentials().each {
        credentials = (com.cloudbees.plugins.credentials.Credentials) it
        if ( credentials.getDescription() == ssh_key_description) {
            ssh_credentials_exist = true
            println("Found existing credentials: " + ssh_key_description)
        }
    }

    if(!ssh_credentials_exist) {
        def ssh_key_scope = CredentialsScope.GLOBAL
        def ssh_key_id = "adop-jenkins-master"
        def ssh_key_username = "jenkins"
        def ssh_key_private_key_source = new BasicSSHUserPrivateKey.UsersPrivateKeySource()
        def ssh_key_passphrase = null

        def ssh_key_domain = com.cloudbees.plugins.credentials.domains.Domain.global()
        def ssh_key_creds = new BasicSSHUserPrivateKey(ssh_key_scope,ssh_key_id,ssh_key_username,ssh_key_private_key_source,ssh_key_passphrase,ssh_key_description)

        system_credentials_provider.addCredentials(ssh_key_domain,ssh_key_creds)
    }

    // Git Identity
    println "--> Configuring Git Identity"
    def desc_git_scm = instance.getDescriptor("hudson.plugins.git.GitSCM")
    desc_git_scm.setGlobalConfigName(gitGlobalConfigName)
    desc_git_scm.setGlobalConfigEmail(gitGlobalConfigEmail)

    // Save the state
    instance.save()
}
