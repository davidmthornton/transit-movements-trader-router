/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package helper

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.scalatest.BeforeAndAfterAll
import org.scalatest.BeforeAndAfterEach
import org.scalatest.Suite
import play.api.Application
import play.api.inject.Injector
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.guice.GuiceableModule

trait WireMockServerHandler extends BeforeAndAfterAll with BeforeAndAfterEach {
  this: Suite =>

  protected val server: WireMockServer = new WireMockServer(wireMockConfig().dynamicPort())

  protected def portConfigKey: String

  protected lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm" -> false,
        portConfigKey -> server.port().toString
      )
      .overrides(bindings: _*)
      .build()

  protected lazy val injector: Injector = app.injector

  protected def bindings: Seq[GuiceableModule] = Seq.empty

  override def beforeAll(): Unit = {
    server.start()
    app
    super.beforeAll()
  }

  override def beforeEach(): Unit = {
    server.resetAll()
    app
    super.beforeEach()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    server.stop()
  }
}
