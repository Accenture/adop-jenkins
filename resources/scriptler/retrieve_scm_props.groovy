import hudson.model.*;
import hudson.util.*;

base_path = "###SCM_PROVIDER_PROPERTIES_PATH###"

// Initialise folder containing all SCM provider properties files
String PropertiesPath = base_path + "/ScmProviders/"
File folder = new File(PropertiesPath)
def providerList = []

// Loop through all files in properties data store and add to returned list
for (File fileEntry : folder.listFiles()) {
  if (!fileEntry.isDirectory()){
    String title = PropertiesPath +  fileEntry.getName()
    Properties scmProperties = new Properties()
    InputStream input = null
    input = new FileInputStream(title)
    scmProperties.load(input)
    String url = scmProperties.getProperty("scm.url")
    String protocol = scmProperties.getProperty("scm.protocol")
    String id = scmProperties.getProperty("scm.id")
    String output = url + " - " + protocol + " (" + id + ")"
    providerList.add(output)
  }
}

if (providerList.isEmpty()) {
    providerList.add("No SCM providers found")
}

return providerList;