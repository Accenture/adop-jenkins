import hudson.model.*
import hudson.views.*
import jenkins.model.*;
import org.jenkinsci.plugins.environmentdashboard.EnvDashboardView;

def instance = Jenkins.getInstance()

def view_name = "Environment Dashboard"
def env_order = ""
def comp_order = "Petclinic Web App, OWASP ZAP/Selenium Integration Tests"
def deploy_history = "10"


v = new EnvDashboardView(view_name, env_order, comp_order, deploy_history)

def views = instance.getViews()
def view_exists = false

views.each {
    view = (View) it
    if ( view.getViewName() ==  v.getViewName() ) {
            view_exists = true
            println("Found existing view: " + view.getViewName())
    }
}

if (!view_exists) {
    instance.addView(v)
}