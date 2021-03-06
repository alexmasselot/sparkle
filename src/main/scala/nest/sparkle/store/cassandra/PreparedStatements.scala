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

package nest.sparkle.store.cassandra

import com.datastax.driver.core.Session

trait PreparedStatements[T] {
  private var preparedOpt: Option[T] = None

  def preparedStatements(initFn: ()=>T): T = {
    preparedOpt.getOrElse {
      val prepared = initFn()
      preparedOpt = Some(prepared)
      prepared
    }
  }

}
