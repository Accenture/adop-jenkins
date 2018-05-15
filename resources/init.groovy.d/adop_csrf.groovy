import hudson.security.csrf.DefaultCrumbIssuer
import jenkins.model.Jenkins
 
Thread.sleep {
    
    sleep 3000
    
    def instance = Jenkins.instance
    instance.setCrumbIssuer(new DefaultCrumbIssuer(false))
    instance.save()
}