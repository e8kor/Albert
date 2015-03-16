package org.system.api

/**
 * Created by evgeniikorniichuk on 15/03/15.
 */
package object actor {

  import akka.actor.{ActorRef, Props}

  object withProps {

    import scala.reflect.io.Path
    import scala.reflect.{ClassTag, classTag}

    def apply[T: ClassTag](): Props = Props(classTag[T] runtimeClass)

    def apply[T: ClassTag](actorRef: ActorRef): Props = Props(classTag[T] runtimeClass, actorRef)

    def apply[T: ClassTag](concurrency: Int): Props = Props(classTag[T] runtimeClass, concurrency)

    def apply[T: ClassTag](path: Path): Props = Props(classTag[T] runtimeClass, path)

    def apply[T: ClassTag](topic: String): Props = Props(classTag[T] runtimeClass, topic)

    def apply[T: ClassTag](actorRef: ActorRef, path: Path): Props = Props(classTag[T] runtimeClass, actorRef, path)

    def apply[T: ClassTag](actorRef: ActorRef, key: String): Props = Props(classTag[T] runtimeClass, actorRef, key)

  }

}
