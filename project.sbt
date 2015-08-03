lazy val albert = project in file(".") dependsOn(
  engine, plugin_api, integration_api, plugins
  ) aggregate(
  engine, plugin_api, integration_api, plugins
  )

lazy val plugins = project dependsOn plugin_api

lazy val plugin_api = project

lazy val engine = project dependsOn(plugin_api, integration_api)

lazy val integration_api = project