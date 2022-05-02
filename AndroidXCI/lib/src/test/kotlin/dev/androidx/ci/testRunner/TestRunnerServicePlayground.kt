/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.androidx.ci.testRunner

import dev.androidx.ci.firebase.FirebaseTestLabApi
import dev.androidx.ci.generated.ftl.AndroidDevice
import dev.androidx.ci.util.GoogleCloudCredentialsRule
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

class TestRunnerServicePlayground {
    @get:Rule
    val playgroundCredentialsRule = GoogleCloudCredentialsRule()

    lateinit var testRunnerService: TestRunnerService
    @Before
    fun initTestService() {
        testRunnerService = TestRunnerService.create(
            credentials = playgroundCredentialsRule.credentials,
            firebaseProjectId = "androidx-dev-prod",
            bucketName = "androidx-ftl-aosp-bucket-2",
            bucketPath = "localRuns",
            gcsResultPath = "yigit"
        )
    }


    @Test
    fun manual() {
        val apkPath = File(
            "/Users/yboyar/src/androidx-main/frameworks/support/out/room-playground/room-playground/room/room-runtime/build/outputs/apk/androidTest/debug/room-runtime-debug-androidTest.apk"
        )
        runBlocking {
            val l3 = testRunnerService.runTest(
                testApk = apkPath,
                appApk = null,
                devicePicker = {
                               listOf(
                                   FTLTestDevices.PIXEL6_31,
                                   FTLTestDevices.NEXUS5_19
                               )
                },
                localDownloadFolder = File("/Users/yboyar/src/androidx-ci-action/local3")
            )
            println(l3.second)
        }
    }

    @Test
    fun buildCommonDevices() {
        runBlocking {
            testRunnerService.testLabController.getCatalog()
                .androidDeviceCatalog
                ?.models
                ?.filter {
//                    it.manufacturer == "Google"
                    it.supportedVersionIds?.any {
                        it.startsWith("1")
                    } ?: false
                }?.forEach {
                    println("""
                        ${it.id} / ${it.supportedVersionIds} / ${it.form}
                    """.trimIndent())
                }

        }
    }
}