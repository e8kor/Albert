# AlbertPluginAPI

Plugin API in simples concept of how to make your logic work as part of application execution flow.

- Each plugin should extend `akka.Actor` class
- Each plugin should perform its execution on receiving of `StartWork` command
- Each plugin should respond to its parent with results of its execution (Success or Failed) and info that is required in report of suite execution.
