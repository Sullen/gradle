/*
 * Copyright 2014 the original author or authors.
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

package org.gradle.play.integtest

import org.gradle.play.integtest.fixtures.DistributionTestExecHandleBuilder
import org.gradle.play.integtest.fixtures.PlayMultiVersionRunApplicationIntegrationTest
import org.gradle.process.internal.ExecHandle
import org.gradle.process.internal.ExecHandleBuilder
import org.gradle.util.Requires
import org.gradle.util.TestPrecondition

abstract class PlayDistributionApplicationIntegrationTest extends PlayMultiVersionRunApplicationIntegrationTest {
    def "can build play app distribution"() {
        when:
        succeeds("stage")

        then:
        executedAndNotSkipped(
                ":routesCompilePlayBinary",
                ":twirlCompileTwirlTemplatesPlayBinary",
                ":createPlayBinaryJar",
                ":createPlayBinaryDistributionJar",
                ":createPlayBinaryAssetsJar",
                ":createPlayBinaryStartScripts",
                ":stagePlayBinaryDist")

        and:
        verifyJars()
        verifyStagedFiles()

        when:
        succeeds("dist")

        then:
        executedAndNotSkipped(":createPlayBinaryDist")
        skipped(
                ":routesCompilePlayBinary",
                ":twirlCompileTwirlTemplatesPlayBinary",
                ":createPlayBinaryJar",
                ":createPlayBinaryDistributionJar",
                ":createPlayBinaryAssetsJar",
                ":createPlayBinaryStartScripts",
                ":stagePlayBinaryDist")

        and:
        verifyZips()
    }

    @Requires(TestPrecondition.NOT_UNKNOWN_OS)
    def "can run play distribution" () {
        ExecHandleBuilder builder
        ExecHandle handle
        String distDirPath = new File(testDirectory, "build/stage").path

        setup:
        httpPort = portFinder.nextAvailable
        run "stage"

        when:
        builder = new DistributionTestExecHandleBuilder(httpPort.toString(), distDirPath)
        handle = builder.build()
        handle.start()

        then:
        verifyStarted()

        and:
        verifyRunningApp()

        cleanup:
        ((DistributionTestExecHandleBuilder.DistributionTestExecHandle) handle).shutdown()
        verifyStopped()
    }

    void verifyZips() {
        zip("build/distributions/playBinary.zip").containsDescendants(
                "playBinary/lib/${playApp.name}.jar",
                "playBinary/lib/${playApp.name}-assets.jar",
                "playBinary/bin/playBinary",
                "playBinary/bin/playBinary.bat",
                "playBinary/conf/application.conf",
                "playBinary/README"
        )
    }

    void verifyStagedFiles() {
        file("build/stage/playBinary").assertContainsDescendants(
                "lib/${playApp.name}.jar",
                "lib/${playApp.name}-assets.jar",
                "bin/playBinary",
                "bin/playBinary.bat",
                "conf/application.conf",
                "README"
        )
    }

    void verifyJars() {
        jar("build/distributionJars/playBinary/${playApp.name}.jar").containsDescendants(
                "Routes.class",
                "views/html/index.class",
                "views/html/main.class",
                "controllers/Application.class",
                "application.conf")

        jar("build/distributionJars/playBinary/${playApp.name}.jar").isManifestPresentAndFirstEntry()
    }
}
