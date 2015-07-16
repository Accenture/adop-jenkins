import hudson.model.*;
import jenkins.model.*;
import hudson.tools.*;
import jenkins.plugins.nodejs.*;
import jenkins.plugins.nodejs.tools.*;

// Variables
def nodejs_version = "0.12.2"
def nodejs_global_packages = "grunt-cli@~0.1.13 bower@~1.3.12 plato@~1.2.1"
def nodejs_packages_refresh_hours = 72

// Constants
def instance = Jenkins.getInstance()

// env['USERNAME']

Thread.start {
    sleep 10000

    // NodeJS
    println "--> Configuring NodeJS"
    def nodeJSPluginInstance = NodeJSPlugin.instance()

    def nodejsInstaller = new NodeJSInstaller(nodejs_version,nodejs_global_packages,nodejs_packages_refresh_hours)
    def installSourceProperty = new InstallSourceProperty([nodejsInstaller])
    def nodejs_inst = new NodeJSInstallation(
      "ADOP NodeJS", // Name
      "", // Home
      [installSourceProperty]
    )

    // Only add ADOP NodeJS if it does not exist - do not overwrite existing config
    def nodejs_installations = nodeJSPluginInstance.getInstallations()
    def nodejs_inst_exists = false
    nodejs_installations.each {
      installation = (NodeJSInstallation) it
        if ( nodejs_inst.getName() ==  installation.getName() ) {
                nodejs_inst_exists = true
                println("Found existing installation: " + installation.getName())
        }
    }
    
    if (!nodejs_inst_exists) {
        nodejs_installations += nodejs_inst
        nodeJSPluginInstance.setInstallations((NodeJSInstallation[]) nodejs_installations)
        nodeJSPluginInstance.save()
    }

    // Save the state
    instance.save()
}