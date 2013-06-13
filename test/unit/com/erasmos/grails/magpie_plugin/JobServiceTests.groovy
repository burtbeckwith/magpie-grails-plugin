package com.erasmos.grails.magpie_plugin



import grails.test.mixin.*
import org.junit.*
import org.quartz.JobDetail
import org.quartz.JobKey
import org.quartz.Scheduler
import org.quartz.Trigger
import org.quartz.TriggerKey

@TestFor(JobService)
@Mock(Errand)
@TestMixin(DomainTestUtils)
class JobServiceTests {

    def mockControlQuartzScheduler

    @Before
    void setUp(){

        mockControlQuartzScheduler  = mockFor(Scheduler)
        service.quartzScheduler     = mockControlQuartzScheduler.createMock()
    }


    @Test(expected = JobService.JobServiceException)
    void addJobForErrandWhenNotScheduled() {

        def errand = generateErrand()

        expectScheduleJob(errand)
        expectCheckExists(errand,false)

        service.addJob(errand)
    }

    @Test
    void addJobForErrand() {

        def errand = generateErrand()

        expectScheduleJob(errand)
        expectCheckExists(errand,true)

        service.addJob(errand)
    }

    @Test
    void generateJobDetail(){

        def errand = generateErrand()

        def jobDetail = service.generateJobDetail(errand)

        assertEquivalent(errand,jobDetail)
    }

    @Test
    void generateTrigger(){

        def errand = generateErrand()

        def trigger = service.generateTrigger(errand)

        assertEquivalent(errand,trigger)
    }

    private void expectScheduleJob(final Errand expectedErrand) {

        mockControlQuartzScheduler.demand.scheduleJob {
            JobDetail _jobDetail,
            Trigger _trigger ->
                assertEquivalent(expectedErrand,_jobDetail)
                assertEquivalent(expectedErrand,_trigger)
                // TODO: Trigger now
        }

    }

    private void assertEquivalent(final Errand errand, final JobDetail jobDetail) {

        assertEquivalent(errand,jobDetail.key)
        assertEquals(1,jobDetail.jobDataMap.size())
        assertEquals(errand.id,jobDetail.jobDataMap.get('ErrandId'))

    }

    private void assertEquivalent(final Errand errand, final Trigger trigger) {

        assertEquivalent(errand,trigger.key)
        assertEquals(errand.cronExpression,trigger.properties['cronExpression'])
    }


    private void expectCheckExists(final Errand expectedErrand, final boolean returnedVerdict) {

        mockControlQuartzScheduler.demand.checkExists {
            JobKey _jobKey->
                assertEquivalent(expectedErrand,_jobKey)
                return returnedVerdict
        }
    }

    private void assertEquivalent(final Errand errand, final JobKey jobKey) {

        assertEquals(errand.name,jobKey.name)
        assertEquals('Errands',jobKey.group)
    }

    private void assertEquivalent(final Errand errand, final TriggerKey triggerKey) {

        assertEquals(errand.name,triggerKey.name)
        assertEquals('Errands',triggerKey.group)
    }
}
