import jenkins.model.*;
import com.dabsquared.gitlabjenkins.connection.*;
import hudson.model.*;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import hudson.util.Secret;
  
// Check if enabled
def env = System.getenv()
if (!env['ADOP_GITLAB_ENABLED'].toBoolean()) {
    println "--> ADOP Gitlab Disabled"
    return
}

// Variables
def gitlab_host_name = env['GITLAB_HOST']
def gitlab_api_token = env['GITLAB_AUTH_TOKEN']
def gitlab_ignore_cert_errors = env['GITLAB_IGNORE_CERTIFICATE_ERRORS'] ?: false
def gitlab_connection_timeout = env['GITLAB_CONNECTION_TIMEOUT'] ?: 10
def gitlab_read_timeout = env['GITLAB_READ_TIMEOUT'] ?: 10

def gitlab_api_token_description = "Gitlab api token"
def gitlab_api_token_id = "gitlab_api_token"

def credential_id = "gitlab_user_token"
def credential_description = "Gitlab Username Token"
def credential_username = env['GITLAB_USERNAME']
def system_credentials_provider = SystemCredentialsProvider.getInstance()

// Constants
def instance = Jenkins.getInstance()

Thread.start {
  sleep 10000

  api_token = new Secret(gitlab_api_token)

  //Gitlab access token
  gitlab_api_token_credentials_exist = false
  system_credentials_provider.getCredentials().each {
      credentials = (com.cloudbees.plugins.credentials.Credentials) it
      if ( credentials.getDescription() == gitlab_api_token_description) {
          gitlab_api_token_credentials_exist = true
          println("Found existing credentials: " + gitlab_api_token_description)
      }
  }
    
  if(!gitlab_api_token_credentials_exist) {
    domain = Domain.global()
    store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()
    gitlabToken = new GitLabApiTokenImpl(
      CredentialsScope.GLOBAL,
      gitlab_api_token_id,
      gitlab_api_token_description,
      api_token
    )
    store.addCredentials(domain, gitlabToken) 
  }

  //Gitlab username token
  gitlab_username_credentials_exist = false
  system_credentials_provider.getCredentials().each {
      credentials = (com.cloudbees.plugins.credentials.Credentials) it
      if ( credentials.getDescription() == credential_description) {
          gitlab_username_credentials_exist = true
          println("Found existing credentials: " + credential_description)
      }
  }
    
  if(!gitlab_username_credentials_exist) {
    domain = Domain.global()
    store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()
    gitlabUserPass = new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL,credential_id,credential_description,credential_username,gitlab_api_token)
    store.addCredentials(domain, gitlabUserPass) 
  }
  
  // Gitlab
  println "--> Configuring Gitlab"
  def gitlab_config = instance.getDescriptor("com.dabsquared.gitlabjenkins.connection.GitLabConnectionConfig")
  
  def gitlab_conn = new GitLabConnection(
    'ADOP Gitlab',
    gitlab_host_name,
    gitlab_api_token_id,
    gitlab_ignore_cert_errors,
    gitlab_connection_timeout,
    gitlab_read_timeout
  )
  
  def gitlab_connections = gitlab_config.getConnections()

  def gitlab_server_exists = false
  gitlab_connections.each {
    connection_name = (GitLabConnection) it
    if ( gitlab_conn.name == connection_name.getName() ) {
      gitlab_server_exists = true
      println("Found existing installation: " + gitlab_conn.name)
    }
  }
  
  if (!gitlab_server_exists) {
	  gitlab_connections.add(gitlab_conn)
    gitlab_config.setConnections(gitlab_connections)
  	gitlab_config.save()
  }

  // Save the state
  instance.save()
}
