import hudson.model.*;
import jenkins.model.*;

// Grab output stream
def output = getBinding().out

// Variables
def env = System.getenv()
def root_Url = env['ROOT_URL']

// Constants
def instance = Jenkins.getInstance()

Thread.start {
    sleep 10000
    output.println "--> Configuring General Settings"

    // Base URL
    output.println "--> Setting Base URL"
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
}