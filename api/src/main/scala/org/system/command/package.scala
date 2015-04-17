package org.system.model

/**
 * Created by evgeniikorniichuk on 15/03/15.
 */
package object command {

  sealed trait Command

  trait Request extends Command

  trait Responce extends Command

}