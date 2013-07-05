import com.erasmos.grails.magpie_plugin.JobService
import com.erasmos.grails.magpie_plugin.MagpieRestfulController
import org.quartz.Scheduler
import org.springframework.context.ApplicationContext

class MagpieGrailsPlugin {

    def version = "1.0"
    def grailsVersion = "2.0 > *"

    def title = "Magpie Plugin"
    def author = "Sean Rasmussen"
    def authorEmail = "sean@erasmos.com"
    def description = '''This plugin offers scheduled fetching of any http content (via GET), which is then timestamped and persisted; for instance, you may be keeping a list of current exchange rates where you have single URL for each currency pair; or perhaps you want grab a daily weather map.'''
    def documentation = "http://grails.org/plugin/app-report-card"

    def license = "APACHE"
    def organization = [name: "Erasmos Inc", url: "http://www.erasmos.com/"]
    def issueManagement = [system: 'GITHUB', url: 'https://github.com/erasmos/magpie-grails-plugin/issues']
    def scm = [url: 'https://github.com/erasmos/magpie-grails-plugin']

    /**
     *
     */
    def doWithApplicationContext = { applicationContext ->
        configScheduler(applicationContext)
        configureRestfulController(applicationContext)
    }

    /**
     * We need to inject the ApplicationContext in the Scheduler,
     * so it's available to each of our dynamically created jobs.
     *
     * @param applicationContext
     */
    private void configScheduler(final ApplicationContext applicationContext) {

        def scheduler = getScheduler(applicationContext)
        assert scheduler != null, shout("Where is the Quartz Schedule?")

        scheduler.getContext().put(JobService.ScheduleContextKeyApplicationContext,applicationContext)
    }

    /**
     *
     * @param applicationContext
     * @return
     */
    private Scheduler getScheduler(final ApplicationContext applicationContext) {
        return applicationContext.getBeansOfType(Scheduler).get('quartzScheduler')
    }



    private void shout(final String message){

        def divider =  "@" * 42 + "\n"

        println(divider)
        println(message)
        println(divider)
        println("\n\n")

    }

    /**
     * @param applicationContext
     */
    private void configureRestfulController(final ApplicationContext applicationContext){
        def candidates = applicationContext.getBeansOfType(MagpieRestfulController)
        MagpieRestfulController restfulController = candidates.get(MagpieRestfulController.canonicalName)
        restfulController.registerJSONMarshallers()
    }
}
