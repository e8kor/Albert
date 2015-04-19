package org.system.scenario

/**
 * Created by evgeniikorniichuk on 23/03/15.
 */

//TODO not used know. What I really need ? Data ? Properties? Configuration?
//TODO properties probably have same meaning as typesafe config
@Deprecated
trait ScenarioProperties[T <: ScenarioProperties] {
  self:T =>

}
