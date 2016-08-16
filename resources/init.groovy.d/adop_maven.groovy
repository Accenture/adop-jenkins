import hudson.model.*;
import jenkins.model.*;
import hudson.tools.*;
import hudson.tasks.Maven.MavenInstaller;
import hudson.tasks.Maven.MavenInstallation;

// Check if enabled
def env = System.getenv()
if (!env['ADOP_MAVEN_ENABLED'].toBoolean()) {
    println "--> ADOP Maven Disabled"
    return
}

// Variables
// MAVEN_VERSION can be defined as a single version or a comma separated string of versions
// eg. 
// MAVEN_VERSION=3.0.5
// MAVEN_VERSION=3.0.5,3.2.5

def maven_version = env['MAVEN_VERSION']
def maven_version_list = maven_version.split(',')

// Constants
def instance = Jenkins.getInstance()

Thread.start {
    sleep 10000

    // Maven
    println "--> Configuring Maven"
    def desc_MavenTool = instance.getDescriptor("hudson.tasks.Maven")
    def maven_installations = desc_MavenTool.getInstallations()

    maven_version_list.eachWithIndex { version, index ->
        def mavenInstaller = new MavenInstaller(version)
        def installSourceProperty = new InstallSourceProperty([mavenInstaller])
        
        def name="ADOP Maven_" + version

        // This makes the solution backwards-compatible, and will treat the first version in the array as "ADOP Maven"
        if (index == 0)
        {
            name="ADOP Maven"
        }

        def maven_inst = new MavenInstallation(
          name, // Name
          "", // Home
          [installSourceProperty]
        )

        // Only add a Maven installation if it does not already exist - do not overwrite existing config
        
        def maven_inst_exists = false
        maven_installations.each {
          installation = (MavenInstallation) it
            if ( maven_inst.getName() ==  installation.getName() ) {
                    maven_inst_exists = true
                    println("Found existing installation: " + installation.getName())
            }
        }
        
        if (!maven_inst_exists) {
            maven_installations += maven_inst
        }
    }

    desc_MavenTool.setInstallations((MavenInstallation[]) maven_installations)
    desc_MavenTool.save()

    // Save the state
    instance.save()
}