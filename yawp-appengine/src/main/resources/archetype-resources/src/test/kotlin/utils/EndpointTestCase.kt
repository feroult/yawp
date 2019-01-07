package ${package}.utils

import io.yawp.testing.EndpointTestCaseBase

open class EndpointTestCase : EndpointTestCaseBase() {

    override fun getAppPackage(): String {
        return "${package}"
    }

}
