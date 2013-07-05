grails.project.work.dir = 'target'

grails.project.dependency.resolution = {
    inherits 'global'
    log 'warn'

    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        compile('org.apache.httpcomponents:httpclient:4.2.5')
    }

    plugins {
        build ':release:2.2.1', {
            export = false
        }

        // Required for integration tests
        runtime (":hibernate:$grailsVersion") {
            export = false
        }

        compile ":spring-events:1.2"

        compile ":quartz:1.0-RC8"

        compile ":quartz-monitor:0.3-RC2"

        runtime ":jquery:1.10.0"

    }
}
