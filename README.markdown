# Incremental Test Plugin

A Jenkins plugin to allow breaking a build when no new tests have been added between commits - mainly aimed at existing projects where committers are slacking off when it comes to tests.

## Using

Enable or disable from a jobs configuration.

## To Do

* Add an excludes list to avoid breaking the build for configuration updates or untestable code.
* Allow resetting of the plugin status.
* Create a view to allow showing why the build has failed.
* Allow naming and shaming committers who don't add tests.
* Unit tests for the plugin :)

## Licence

MIT Licence