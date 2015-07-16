import hudson.model.*;
import jenkins.model.*;
import hudson.tools.*;
import hudson.tasks.Maven.MavenInstaller;
import hudson.tasks.Maven.MavenInstallation;

// Variables
def maven_version = "3.0.5"

// Constants
def instance = Jenkins.getInstance()

// env['USERNAME']

Thread.start {
    sleep 10000

    // Maven
    println "--> Configuring Maven"
    def desc_MavenTool = instance.getDescriptor("hudson.tasks.Maven")

    def mavenInstaller = new MavenInstaller(maven_version)
    def installSourceProperty = new InstallSourceProperty([mavenInstaller])
    def maven_inst = new MavenInstallation(
      "ADOP Maven", // Name
      "", // Home
      [installSourceProperty]
    )

    // Only add ADOP Maven if it does not exist - do not overwrite existing config
    def maven_installations = desc_MavenTool.getInstallations()
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
        desc_MavenTool.setInstallations((MavenInstallation[]) maven_installations)
        desc_MavenTool.save()
    }

    // Save the state
    instance.save()
}