package org.system.api

/**
 * Created by evgeniikorniichuk on 15/03/15.
 */
package object plugin {

  trait PluginMessage

  case class PluginScript(script: String)

  case class PluginType(pluginName: String)

  case class PluginCommand(pluginType: PluginType, pluginScript: PluginScript)

}