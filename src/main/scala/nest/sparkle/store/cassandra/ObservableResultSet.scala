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

import com.datastax.driver.core.ResultSet
import scala.concurrent.Future
import rx.lang.scala.Observable
import rx.lang.scala.Observer
import rx.lang.scala.Subscription
import scala.concurrent.ExecutionContext
import scala.collection.JavaConverters._
import com.datastax.driver.core.Row
import nest.sparkle.util.GuavaConverters._
import scala.annotation.tailrec
import com.datastax.driver.core.ResultSetFuture

object ObservableResultSet {
  /** a ResultSetFuture that can be converted into an Observable for asynchronously 
   *  working with the stream of Rows as the arrive from the database */
  implicit class WrappedResultSet(val resultSetFuture: ResultSetFuture) {

    /** return an Observable[Row] for the Future[ResultSet].  */
    def observerableRows(implicit executionContext: ExecutionContext): Observable[Row] = {

      val asScalaFuture = resultSetFuture.toFuture
      
      /** A constructor function for making an Observable.  The function takes an Observer to which it
        * feeds rows as they arrive.  It returns a Subscription so that the Observer can can abort the stream
        * early if necessary.
        */
      val subscribed: Observer[Row] => Subscription = { observer =>
        var cancelled = false

        asScalaFuture.foreach { resultSet =>
          /** Iterate through the rows as they arrive from the network, calling observer.onNext for each row.
            *
            * rowChunk() is called once for each available group ('chunk') of resultSet rows.  It
            * recursively calls itself to process the next fetched set of rows until there are now more rows left.
            */
          def rowChunk() {
            if (!cancelled) {
              val iterator = resultSet.iterator().asScala
              val availableNow = resultSet.getAvailableWithoutFetching()
              
              iterator.take(availableNow).foreach { row => 
                observer.onNext(row) 
              }

              if (!resultSet.isFullyFetched()) {    // CONSIDER - is this a race with availableNow?
                resultSet.fetchMoreResults().toFuture.foreach { _ => rowChunk() }
              } else {
                observer.onCompleted()
              }
            }
          }

          rowChunk()
        }

        asScalaFuture.onFailure {
          case error: Throwable =>
            observer.onError(error)
        }

        Subscription {
          cancelled = true
        }        
      }
      
      Observable(subscribed)
    }

  }
}
