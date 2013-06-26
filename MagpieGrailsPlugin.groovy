import com.erasmos.grails.magpie_plugin.JobService
import com.erasmos.grails.magpie_plugin.MagpieRestfulController
import com.erasmos.grails.magpie_plugin.MagpieService
import com.erasmos.grails.magpie_plugin.MagpieRestfulController
import org.quartz.Scheduler
import org.springframework.context.ApplicationContext
import org.springframework.scheduling.quartz.SchedulerFactoryBean

class MagpieGrailsPlugin {

    // the plugin version
    def version = "1.0-SNAPSHOT"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.2 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "Magpie Plugin" // Headline display name of the plugin
    def author = "Your name"
    def authorEmail = ""
    def description = '''\
Brief summary/description of the plugin.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/magpie"

    static def final CreateTestErrands = true

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->

        configScheduler(applicationContext)
        configureRestfulController(applicationContext)

        if(CreateTestErrands) createTestErrands(applicationContext)
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
     * @param applicationContext
     */
    private void createTestErrands(final ApplicationContext applicationContext){

        assert applicationContext != null

        shout("Creating Test Errands ...")

        def magpieService = getMagpieService(applicationContext)
        assert magpieService != null, shout("Where is the MagpieService?")

        def errands = []

        errands << magpieService.createNewErrand(
                "[Test] Convert from GBP to CAD",
                generateCurrencyRelatedUrl('GBP','CAD'),
                "0 0 12 1/1 * ? *",
                null
                                        )

        errands << magpieService.createNewErrand(
                "[Test] Convert from GBP to USD",
                generateCurrencyRelatedUrl('GBP','USD'),
                "0 0 0/1 1/1 * ? *",
                null
        )

        errands << magpieService.createNewErrand(
                "[Test] Weather Map",
                new URL("http://media.zenfs.com/en_us/weather/weather.com/eur_unitedkingdom_outlook_en_GB_440_dmy_y.jpg"),
                "0 0 0/1 1/1 * ? *",
                null
                                )

        errands.each{magpieService.fetch(it)}

    }

    private Scheduler getScheduler(final ApplicationContext applicationContext) {
        return applicationContext.getBeansOfType(Scheduler).get('quartzScheduler')
    }

    private MagpieService getMagpieService(final ApplicationContext applicationContext) {
        return applicationContext.getBeansOfType(MagpieService).get("magpieService")

    }

    private URL generateCurrencyRelatedUrl(final String fromCurrencySymbol, final String toCurrencySymbol){
        return new URL("http://download.finance.yahoo.com/d/quotes.cvs?s=${fromCurrencySymbol}${toCurrencySymbol}=X&f=sl1d1t1ba&e=.csv")
    }


    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
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
