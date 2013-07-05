package com.erasmos.grails.magpie_plugin

import org.quartz.*
import org.springframework.context.ApplicationContext

/**
 * A thin wrapper for Quartz; each Errand is meant to have a single Job,
 * whose schedule is defined by its cron expression.
 *
 * Please note that an Errand's active flag is ignored at this level (a inactive
 * Errand's Job will still run); however, the actual Fetch will not occur
 * in MagpieService.
 */
class JobService {

    static final String ScheduleContextKeyApplicationContext = 'applicationContext'

    static transactional = false

    private static final String JobGroupName           = 'Errands'
    private static final String JobDataMapKeyErrandId  = 'ErrandId'
    private static final String BeanNameMagpieService  = 'magpieService'

    def quartzScheduler

    /**
     *
     *
     * @param errand
     * @throws JobServiceException (if a Job by the Errand's name already exists)
     */
    void addJob(final Errand errand) throws JobServiceException {
        assert errand !=null

        if(log.isDebugEnabled()) log.debug("Adding a new Job for Errand: $errand ... ")

        quartzScheduler.scheduleJob(generateJobDetail(errand),generateTrigger(errand))

        if(!doesJobExist(errand.name)){
            throwServiceException("Failed to schedule a Job for Errand: $errand")
        }

        if(log.isDebugEnabled()) log.debug(" ... job successfully scheduled")
    }

    /**
     *
     * @param jobName
     * @return
     */
    boolean doesJobExist(final String jobName){
        assert jobName != null

        return quartzScheduler.checkExists(new JobKey(jobName,JobGroupName))
    }

    /**
     *
     * @param errand
     * @return
     */
    private JobDetail generateJobDetail(final Errand errand) {
        assert errand != null

        return JobBuilder.newJob(ErrandJob)
                .withIdentity(errand.name,JobGroupName)
                .usingJobData(JobDataMapKeyErrandId,errand.id)
                .build()
    }

    /**
     *
     * @param errand
     * @return
     */
    private Trigger generateTrigger(final Errand errand) {
        assert errand != null

        return TriggerBuilder.newTrigger()
                .withIdentity(errand.name, JobGroupName)
                .withSchedule(CronScheduleBuilder.cronSchedule(errand.cronExpression))
                .build();
    }

    /**
     *
     * @param message
     */
    private void throwServiceException(final String message){
        throw new JobServiceException(message)
    }

    /**
     *
     */
    static class JobServiceException extends Exception {
        JobServiceException(final String message){
            super(message)
        }
    }

    /**
     * This job merely identifies the Errand, the delegates
     * to Magpie.fetch().
     *
     * Obtaining the MagpieService instance depends on a boot time
     * preparation of the Scheduler; please see  MagpieGrailsPlugin.configScheduler()
     *
     */
    static class ErrandJob implements Job {

        @Override
        void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            assert jobExecutionContext != null

            def magpieService =  getMagpieService(jobExecutionContext)
            assert magpieService != null, "MagpieService is null"

            def errandId = extractErrandId(jobExecutionContext)
            if(!errandId){
                if(log.isErrorEnabled()) log.error("The errandId couldn't be found in the JobExecutionContext")
                return
            }

            def errand = Errand.read(errandId)
            if(!errand){
                if(log.isErrorEnabled()) log.error("Unable to find an Errand for ID: $errandId")
            }

            if(log.isDebugEnabled()) log.debug("About to execute job for Errand: $errand ...")
            magpieService.fetch(errand)
        }

        /**
         *
         * @param jobExecutionContext
         * @return
         */
        private MagpieService getMagpieService(final JobExecutionContext jobExecutionContext){
            assert jobExecutionContext != null
            def applicationContext = (ApplicationContext)jobExecutionContext.scheduler.context.get(ScheduleContextKeyApplicationContext);
            assert applicationContext != null, "ApplicationContext not found in the Scheduler's context"

            return applicationContext.getBean(BeanNameMagpieService)
        }

        /**
         *
         * @param jobExecutionContext
         * @return
         */
        private Long extractErrandId(final JobExecutionContext jobExecutionContext){
            return jobExecutionContext.jobDetail?.jobDataMap?.getLong(JobDataMapKeyErrandId)
        }
    }
}
