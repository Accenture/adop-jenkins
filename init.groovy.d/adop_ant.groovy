import hudson.model.*;
import jenkins.model.*;
import hudson.tools.*;
import hudson.tasks.Ant.AntInstaller;
import hudson.tasks.Ant.AntInstallation;

// Check if enabled
def env = System.getenv()
if (!env['ADOP_ANT_ENABLED'].toBoolean()) {
    println "--> ADOP Ant Disabled"
    return
}

// Variables
def ant_version = env['ANT_VERSION']

// Constants
def instance = Jenkins.getInstance()

Thread.start {
    sleep 10000

    // Ant
    println "--> Configuring Ant"
    def desc_AntTool = instance.getDescriptor("hudson.tasks.Ant")

    def antInstaller = new AntInstaller(ant_version)
    def installSourceProperty = new InstallSourceProperty([antInstaller])
    def ant_inst = new AntInstallation(
      "ADOP Ant", // Name
      "", // Home
      [installSourceProperty]
    )

    // Only add ADOP Ant if it does not exist - do not overwrite existing config
    def ant_installations = desc_AntTool.getInstallations()
    def ant_inst_exists = false
    ant_installations.each {
      installation = (AntInstallation) it
        if ( ant_inst.getName() ==  installation.getName() ) {
                ant_inst_exists = true
                println("Found existing installation: " + installation.getName())
        }
    }
    
    if (!ant_inst_exists) {
        ant_installations += ant_inst
        desc_AntTool.setInstallations((AntInstallation[]) ant_installations)
        desc_AntTool.save()
    }

    // Save the state
    instance.save()
}