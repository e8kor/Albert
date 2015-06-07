package org.system.command

/**
 * Created by evgeniikorniichuk on 06/06/15.
 */
sealed trait Command

trait Request extends Command

trait Responce extends Command
