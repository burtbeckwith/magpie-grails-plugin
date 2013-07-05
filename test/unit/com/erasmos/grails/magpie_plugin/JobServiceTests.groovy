package com.erasmos.grails.magpie_plugin

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import org.junit.Before
import org.junit.Test
import org.quartz.*

@TestFor(JobService)
@Mock(Errand)
@TestMixin(DomainTestUtils)
class JobServiceTests {

    def mockControlQuartzScheduler

    /**
     *
     */
    @Before
    void setUp(){

        mockControlQuartzScheduler  = mockFor(Scheduler)
        service.quartzScheduler     = mockControlQuartzScheduler.createMock()
    }

    /**
     *
     */
    @Test(expected = JobService.JobServiceException)
    void addJobForErrandWhenNotScheduled() {

        def errand = generateErrand()

        expectScheduleJob(errand)
        expectCheckExists(errand,false)

        service.addJob(errand)
    }

    /**
     *
     */
    @Test
    void addJobForErrand() {

        def errand = generateErrand()

        expectScheduleJob(errand)
        expectCheckExists(errand,true)

        service.addJob(errand)
    }

    /**
     *
     */
    @Test
    void generateJobDetail(){

        def errand = generateErrand()

        def jobDetail = service.generateJobDetail(errand)

        assertEquivalent(errand,jobDetail)
    }

    /**
     *
     */
    @Test
    void generateTrigger(){

        def errand = generateErrand()

        def trigger = service.generateTrigger(errand)

        assertEquivalent(errand,trigger)
    }

    /**
     *
     * @param expectedErrand
     */
    private void expectScheduleJob(final Errand expectedErrand) {

        mockControlQuartzScheduler.demand.scheduleJob {
            JobDetail _jobDetail,
            Trigger _trigger ->
                assertEquivalent(expectedErrand,_jobDetail)
                assertEquivalent(expectedErrand,_trigger)
        }
    }

    /**
     *
     * @param errand
     * @param jobDetail
     */
    private void assertEquivalent(final Errand errand, final JobDetail jobDetail) {

        assertEquivalent(errand,jobDetail.key)
        assertEquals(1,jobDetail.jobDataMap.size())
        assertEquals(errand.id,jobDetail.jobDataMap.get('ErrandId'))
    }

    /**
     *
     * @param errand
     * @param trigger
     */
    private void assertEquivalent(final Errand errand, final Trigger trigger) {

        assertEquivalent(errand,trigger.key)
        assertEquals(errand.cronExpression,trigger.properties['cronExpression'])
    }

    /**
     *
     * @param expectedErrand
     * @param returnedVerdict
     */
    private void expectCheckExists(final Errand expectedErrand, final boolean returnedVerdict) {

        mockControlQuartzScheduler.demand.checkExists {
            JobKey _jobKey->
                assertEquivalent(expectedErrand,_jobKey)
                return returnedVerdict
        }
    }

    /**
     *
     * @param errand
     * @param jobKey
     */
    private void assertEquivalent(final Errand errand, final JobKey jobKey) {

        assertEquals(errand.name,jobKey.name)
        assertEquals('Errands',jobKey.group)
    }

    /**
     *
     * @param errand
     * @param triggerKey
     */
    private void assertEquivalent(final Errand errand, final TriggerKey triggerKey) {

        assertEquals(errand.name,triggerKey.name)
        assertEquals('Errands',triggerKey.group)
    }
}
