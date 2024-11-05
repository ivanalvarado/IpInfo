package io.github.cdsap.ipinfo

import com.gradle.develocity.agent.gradle.DevelocityConfiguration
import com.gradle.scan.plugin.BuildScanExtension
import io.github.cdsap.ipinfo.output.BuildScanOutput
import io.github.cdsap.ipinfo.output.DevelocityValues
import io.github.cdsap.ipinfo.parser.ResponseParser
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider

class IpInfoPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.gradle.rootProject {

            val develocityConfiguration = extensions.findByType(DevelocityConfiguration::class.java)
            val enterpriseExtension = extensions.findByType(com.gradle.scan.plugin.BuildScanExtension::class.java)

            if (develocityConfiguration != null) {
                buildScanDevelocityReporting(project, develocityConfiguration)
            } else if (enterpriseExtension != null) {
                buildScanReporting(project, enterpriseExtension)
            }
        }
    }

    private fun buildScanDevelocityReporting(
        project: Project,
        buildScanExtension: DevelocityConfiguration
    ) {
        val geolocation = ResponseParser().process(project.ip().get())

        buildScanExtension.buildScan.buildFinished {
            if (geolocation != null) {
                DevelocityValues(buildScanExtension, geolocation).addGeolocationInfoToBuildScan()
            }
        }
    }

    private fun buildScanReporting(
        project: Project,
        buildScanExtension: BuildScanExtension
    ) {
        val geolocation = ResponseParser().process(project.ip().get())

        buildScanExtension.buildFinished {
            if (geolocation != null) {
                BuildScanOutput(buildScanExtension, geolocation).addGeolocationInfoToBuildScan()
            }
        }
    }
}

fun Project.ip(): Provider<String> {
    return execute("curl -s --max-time 2 http://ip-api.com/line/?fields=country,regionName,city,lat,lon,timezone,isp,query")
}

fun Project.execute(command: String): Provider<String> {
    return providers.of(CommandLineWithOutputValue::class.java) {
        parameters.commands.set(command)
    }
}
