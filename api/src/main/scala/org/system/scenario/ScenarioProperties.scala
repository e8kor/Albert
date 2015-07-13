package org.system.scenario

/**
 * Created by evgeniikorniichuk on 23/03/15.
 */

//TODO not used know. What I really need ? Data ? Properties? Configuration?
//TODO properties probably have same meaning as typesafe config
@deprecated("no need in such class", "0.1-SNAPSHOT")
trait ScenarioProperties[T <: ScenarioProperties[T]] {
  self:T =>

}
