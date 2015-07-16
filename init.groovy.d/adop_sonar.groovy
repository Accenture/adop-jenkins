import hudson.model.*;
import jenkins.model.*;
import hudson.plugins.sonar.*;
import hudson.plugins.sonar.model.TriggersConfig;
import hudson.tools.*

// Variables
def sonar_server_url = "http://10.0.0.197:8020/sonar/"
def sonar_account_login = "admin"
def sonar_account_password = "YWRtaW4="
def sonar_db_url = "jdbc:mysql://10.0.0.197:3306/sonar?useUnicode=true&amp;characterEncoding=utf8"
def sonar_db_login = "sonar"
def sonar_db_password = "c29uYXI="
def sonar_plugin_version = ""
def sonar_additional_props = ""

def sonar_runner_version = "2.4"

// Constants
def instance = Jenkins.getInstance()

// env['USERNAME']

Thread.start {
    sleep 10000

    // Sonar
    // Source: http://pghalliday.com/jenkins/groovy/sonar/chef/configuration/management/2014/09/21/some-useful-jenkins-groovy-scripts.html
    println "--> Configuring SonarQube"
    def desc_SonarPublisher = instance.getDescriptor("hudson.plugins.sonar.SonarPublisher")

    def sonar_inst = new SonarInstallation(
      "Sonar", // Name
      false, // Disabled?
      sonar_server_url,
      sonar_db_url,
      sonar_db_login,
      sonar_db_password,
      sonar_plugin_version,
      sonar_additional_props,
      new TriggersConfig(),
      sonar_account_login,
      sonar_account_password
    )
    desc_SonarPublisher.setInstallations(sonar_inst)
    desc_SonarPublisher.save()
    
    // Sonar Runner
    // Source: http://pghalliday.com/jenkins/groovy/sonar/chef/configuration/management/2014/09/21/some-useful-jenkins-groovy-scripts.html
    println "--> Configuring SonarRunner"
    def desc_SonarRunnerInst = instance.getDescriptor("hudson.plugins.sonar.SonarRunnerInstallation")

    def sonarRunnerInstaller = new SonarRunnerInstaller(sonar_runner_version)
    def installSourceProperty = new InstallSourceProperty([sonarRunnerInstaller])
    def sonarRunner_inst = new SonarRunnerInstallation("SonarRunner " + sonar_runner_version, "", [installSourceProperty])
    desc_SonarRunnerInst.setInstallations(sonarRunner_inst)

    desc_SonarRunnerInst.save()

    // Save the state
    instance.save()
}