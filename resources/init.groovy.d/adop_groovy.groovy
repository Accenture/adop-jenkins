import hudson.model.*;
import jenkins.model.*;
import hudson.tools.*;
import hudson.plugins.groovy.*;

def env = System.getenv();
def groovyVersion = env['GROOVY_VERSION']
def groovyVersionList = groovyVersion.split(',')

def instance = Jenkins.getInstance()

Thread.start {
 sleep 10000

 println "--> Configuring Groovy"
 def groovyDescriptor = instance.getDescriptor("hudson.plugins.groovy.GroovyInstallation")
 def groovyInstallations = groovyDescriptor.getInstallations()

 groovyVersionList.eachWithIndex {
  version,
  index ->
  def installer = new GroovyInstaller(version)
  def installSourceProperty = new InstallSourceProperty([installer])

  def name = "ADOP Groovy_" + version

  if (index == 0) {
   name = "ADOP Groovy"
  }

  def installation = new GroovyInstallation(
   name,
   "", [installSourceProperty]
  )

  def groovyIntExists = false
  groovyInstallations.each {
   currentInstallation = (GroovyInstallation) it
   if (installation.getName() == currentInstallation.getName()) {
    groovyIntExists = true
    println("Found existing installation: " + installation.getName())
   }
  }

  if (!groovyIntExists) {
   groovyInstallations += installation
  }
 }

 groovyDescriptor.setInstallations((GroovyInstallation[]) groovyInstallations)

 instance.save()
}
