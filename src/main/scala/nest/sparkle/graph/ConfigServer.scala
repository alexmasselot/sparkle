/* Copyright 2013  Nest Labs

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.  */

package nest.sparkle.graph

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigValueFactory

object ConfigServer {
  def loadConfig(debugLogging: Boolean = false, 
      configFile:String = "application.conf"): Config = {
    val root = ConfigFactory.load(configFile)
    val base = root.getConfig("sparkle-graph-server")
    if (debugLogging) {
      base.withValue("akka.loglevel", ConfigValueFactory.fromAnyRef("DEBUG"))
    } else {
      base
    }
  }
}
