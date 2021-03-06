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
import spray.routing.Route
import akka.actor.Actor
import akka.actor.Actor


/** An actor serving data DataRegistry data via a spray based REST api.  The
 *  server is configured with user provided extensions extracted from the config file.  */
class ConfiguredDataServer(val registry: DataRegistry, val store:Storage, val config:Config,
    override val webRoot: Option[String] = None) extends Actor with ConfiguredDataService {
  def actorRefFactory = context
  def receive = runRoute(route)
  def executionContext = context.dispatcher
}

/** An actor serving data DataRegistry data via a spray based REST api.  */
class DataServer(val registry: DataRegistry, val store:Storage, 
    override val webRoot: Option[String] = None) extends Actor with DataService {
  def actorRefFactory = context
  def receive = runRoute(route)
  def executionContext = context.dispatcher
}


