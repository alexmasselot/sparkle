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

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import rx.lang.scala.Observable
import rx.lang.scala.Observer
import rx.lang.scala.Subscription
import scala.concurrent.Promise
import scala.util.Success
import scala.util.Failure

/** convert a scala Future to an Observable and back */
object ObservableFuture {

  implicit class WrappedFuture[T](val futureValue: Future[T]) {
    /** return an observable that will return a single value when the future completes */
    def toObservable(implicit executionContext: ExecutionContext): Observable[T] = {
      val subscribed: Observer[T] => Subscription = { observer =>
        var cancelled = false

        futureValue.foreach { value =>
          if (!cancelled) {
            observer.onNext(value)
            observer.onCompleted()
          }
        }

        futureValue.onFailure {
          case e: Throwable =>
            observer.onError(e)
        }

        Subscription {
          cancelled = true
        }
      }

      Observable(subscribed)
    }
  }
  
  implicit class WrappedObservable[T](val observable: Observable[T]) {
    
    /** return an Future that will return a single sequence for the observable stream */
    def toFutureSeq: Future[Seq[T]] = {
      val promise = Promise[Seq[T]]()
      def onNext(value:Seq[T]) {
        promise.complete(Success(value))
      }
      
      def onError(error:Throwable) {
        promise.complete(Failure(error))
      }
      
      observable.toSeq.subscribe(onNext _, onError _)
      promise.future
    }
  }

}
