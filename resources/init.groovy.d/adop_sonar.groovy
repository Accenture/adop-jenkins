import hudson.model.*
import jenkins.model.*
import hudson.plugins.sonar.*
import hudson.plugins.sonar.model.TriggersConfig
import hudson.plugins.sonar.utils.SQServerVersions
import hudson.tools.*

// Check if enabled
def env = System.getenv()
if (!env['ADOP_SONAR_ENABLED'].toBoolean()) {
    println "--> ADOP SonarQube Disabled"
    return
}

// Variables
def sonar_server_url = env['SONAR_SERVER_URL']
def sonar_account_login = env['SONAR_ACCOUNT_LOGIN']
def sonar_account_password = env['SONAR_ACCOUNT_PASSWORD']
def sonar_db_url = env['SONAR_DB_URL']
def sonar_db_login = env['SONAR_DB_LOGIN']
def sonar_db_password = env['SONAR_DB_PASSWORD']
def sonar_plugin_version = env['SONAR_PLUGIN_VERSION']
def sonar_additional_props = env['SONAR_ADDITIONAL_PROPS']

def sonar_runner_version = env['SONAR_RUNNER_VERSION']

// Constants
def instance = Jenkins.getInstance()

Thread.start {
    sleep 10000

    // Sonar
    // Source: http://pghalliday.com/jenkins/groovy/sonar/chef/configuration/management/2014/09/21/some-useful-jenkins-groovy-scripts.html
    println "--> Configuring SonarQube"
    def SonarGlobalConfiguration sonar_conf = instance.getDescriptor(SonarGlobalConfiguration.class)

    def sonar_inst = new SonarInstallation(
        "ADOP Sonar", // Name
        sonar_server_url,
        SQServerVersions.SQ_5_1_OR_LOWER, // Major version upgrade of server would require to change it
        "", // Token
        sonar_db_url,
        sonar_db_login,
        sonar_db_password,
        sonar_plugin_version,
        sonar_additional_props,
        new TriggersConfig(),
        sonar_account_login,
        sonar_account_password,
        "" // Additional Analysis Properties
    )

    // Only add ADOP Sonar if it does not exist - do not overwrite existing config
    def sonar_installations = sonar_conf.getInstallations()
    def sonar_inst_exists = false
    sonar_installations.each {
        installation = (SonarInstallation) it
        if (sonar_inst.getName() == installation.getName()) {
            sonar_inst_exists = true
            println("Found existing installation: " + installation.getName())
        }
    }

    if (!sonar_inst_exists) {
        sonar_installations += sonar_inst
        sonar_conf.setInstallations((SonarInstallation[]) sonar_installations)
        sonar_conf.save()
    }

    // Sonar Runner
    // Source: http://pghalliday.com/jenkins/groovy/sonar/chef/configuration/management/2014/09/21/some-useful-jenkins-groovy-scripts.html
    println "--> Configuring SonarRunner"
    def desc_SonarRunnerInst = instance.getDescriptor("hudson.plugins.sonar.SonarRunnerInstallation")

    def sonarRunnerInstaller = new SonarRunnerInstaller(sonar_runner_version)
    def installSourceProperty = new InstallSourceProperty([sonarRunnerInstaller])
    def sonarRunner_inst = new SonarRunnerInstallation("ADOP SonarRunner " + sonar_runner_version, "", [installSourceProperty])

    // Only add our Sonar Runner if it does not exist - do not overwrite existing config
    def sonar_runner_installations = desc_SonarRunnerInst.getInstallations()
    def sonar_runner_inst_exists = false
    sonar_runner_installations.each {
        installation = (SonarRunnerInstallation) it
        if (sonarRunner_inst.getName() == installation.getName()) {
            sonar_runner_inst_exists = true
            println("Found existing installation: " + installation.getName())
        }
    }

    if (!sonar_runner_inst_exists) {
        sonar_runner_installations += sonarRunner_inst
        desc_SonarRunnerInst.setInstallations((SonarRunnerInstallation[]) sonar_runner_installations)
        desc_SonarRunnerInst.save()
    }

    // Save the state
    instance.save()
}