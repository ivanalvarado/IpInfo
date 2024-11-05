package io.github.cdsap.ipinfo.output

import com.gradle.develocity.agent.gradle.DevelocityConfiguration
import io.github.cdsap.ipinfo.model.Ip

class DevelocityValues(
    private val develocityConfiguration: DevelocityConfiguration,
    private val ip: Ip
) {
    fun addGeolocationInfoToBuildScan() {
        develocityConfiguration.buildScan {
            value("City", ip.city)
            value("Country", ip.country)
            value("State", ip.regionName)
            value("Ip", ip.query)
            value("Isp", ip.isp)
            value("Timezone", ip.timeZone)
            value("Lat", ip.lat)
            value("Long", ip.long)
        }
    }
}
