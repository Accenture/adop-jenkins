/*
   Copyright (c) 2015-2018 Sam Gleske - https://github.com/samrocketman/jenkins-bootstrap-shared
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

/*
   Disable all JNLP protocols except for JNLP4.  JNLP4 is the most secure agent
   protocol because it is using standard TLS.
   
   source: https://github.com/samrocketman/jenkins-bootstrap-shared/blob/master/scripts/configure-jnlp-agent-protocols.groovy
 */
import jenkins.model.Jenkins

Thread.start {
    sleep 3000

    println "--> Configuring JNLP Agent Protocols"
    
    Jenkins jenkins = Jenkins.instance

    if(!jenkins.isQuietingDown()) {
        Set<String> agentProtocolsList = ['JNLP4-connect', 'JNLP2-connect', 'Ping']
        if(!jenkins.getAgentProtocols().equals(agentProtocolsList)) {
            jenkins.setAgentProtocols(agentProtocolsList)
            jenkins.save()
        }
    }
    else {
        println 'Shutdown mode enabled.  Configure Agent Protocols SKIPPED.'
    }
}
