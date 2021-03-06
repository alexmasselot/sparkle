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

import java.nio.file.Path
import nest.sparkle.util.WatchPath
import akka.actor.ActorSystem
import collection.mutable
import spray.caching.LruCache
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import akka.actor.TypedActor
import spray.util._
import java.io.FileNotFoundException
import akka.actor.TypedProps
import scala.concurrent.future
import scala.concurrent.Promise
import scala.concurrent.duration._
import scala.util.control.Exception._
import java.nio.file.NoSuchFileException
import scala.util.Try
import nest.sparkle.util.TryToFuture._
  

/** Create a data registry backed by a filesystem subdirectory */
object DirectoryDataRegistry {
  def apply(path:Path, glob:String = "**")(implicit system:ActorSystem):DirectoryDataRegistryApi ={
    TypedActor(system).typedActorOf(
        TypedProps(classOf[DirectoryDataRegistryApi],
                   new DirectoryDataRegistryActor(path, glob)).withTimeout(30.seconds),
        "DirectoryDataRegistry_"+path.toString.replace('/', '-')
      )
  }
}

/** Internal API for the DirectoryDataRegistryActor proxy.  Includes both the public and protected
 *  proxied actor messages.  */
protected trait DirectoryDataRegistryApi extends DataRegistry {
  protected[graph] def fileChange(change:WatchPath.Change)
}

/** A data registry backed by a filesystem subdirectory */
class DirectoryDataRegistryActor(path:Path, glob:String = "**")
    extends DirectoryDataRegistryApi with TypedActor.PreStart {
  implicit val system = TypedActor.context.system
  implicit val execution = TypedActor.context.dispatcher
  
  private val self = TypedActor.self[DirectoryDataRegistryApi]
  private val files = mutable.HashSet[String]()  
  private val loadedSets = LruCache[DataSet](maxCapacity = 100)
  private val watcher = WatchPath(path, glob)
  
  def preStart() {
    val initialFiles = watcher.watch(self.fileChange) 
    val fileNames = initialFiles.await.map(_.toString)
    files ++= fileNames
  }
  
  /** return the DataSet for the given path string */
  def findDataSet(name:String):Future[DataSet] = {
    loadedSets(name, () => loadSet(name))
  }
  
  def allSets():Future[Iterable[String]] = {
    Future.successful(files)
  }
  
  /** asynchronously load a .csv or .tsv file */
  private def loadSet(name:String):Future[DataSet] = {
    val resolved = path.resolve(name)
    val fullPathTry = Try(resolved.toRealPath())
    fullPathTry.toFuture.flatMap {fullPath =>
      FileLoadedDataSet.loadAsync(fullPath, name) 
    }
  } 
  
  /** called when the filesystem watcher notices a change */
  protected[graph] def fileChange(change:WatchPath.Change) {
    import WatchPath._
    
    def localPath(fullPath:Path):String = {
      path.relativize(fullPath).toString
    }
    
    change match {
      case Added(fullPath) =>
        files += localPath(fullPath)
      case Modified(fullPath) =>        
        loadedSets.remove(localPath(fullPath))
      case Removed(fullPath) =>
        val changedPath = localPath(fullPath)
        loadedSets.remove(changedPath)
        files.remove(changedPath)
    }
  } 
  
  def postStop() {
    TypedActor(system).stop(watcher)
  }
  
}
