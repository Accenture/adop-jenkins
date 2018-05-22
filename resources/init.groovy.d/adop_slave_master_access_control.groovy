import jenkins.model.*
import jenkins.security.s2m.AdminWhitelistRule

Thread.start {
    sleep 3000

    println "--> Enabling slave master access control"
    
    Jenkins.instance.injector.getInstance(AdminWhitelistRule.class).setMasterKillSwitch(false);
    
    Jenkins.instance.save()
}
