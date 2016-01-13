import hudson.model.*;
import hudson.util.*;


// Get current build number
def currentBuildNum = build.parent?.lastBuild.properties.get("envVars").get("BUILD_NUMBER");


// Get git meta data
def workspace = build.parent?.lastBuild.properties.get("envVars").get("WORKSPACE");
def cmd = "git --git-dir " + workspace + "/.git log  -1 --pretty=format:%an<br/>%s%b";
println "cmd: " + cmd;
def proc = cmd.execute();
proc.waitFor();
println "return code: ${ proc.exitValue()}"
println "stderr: ${proc.err.text}"
def gitData = "${proc.in.text}";
println "gitData: " + gitData;

def currentBuild = Thread.currentThread().executable;
def oldParams = currentBuild.getAction(ParametersAction.class)

// Update the param
def params = [
new StringParameterValue("T",gitData),
new StringParameterValue("B",currentBuildNum)
]

// Remove old params - Plugins inject variables!
currentBuild.actions.remove(oldParams)
currentBuild.addAction(new ParametersAction(params));