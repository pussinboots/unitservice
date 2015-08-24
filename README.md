Unitservice
======================

RESTful Web Services that trigger test execution by running jenkins builds and populate the test result.


Scenario
--------

Trigger test execution easiely with just a rest call and get the test result back to display them for the user.
Start as a prototyp project to support JUnit test runs and also cucumber test runs. The test run itself is handled by jenkins
and the build system of choice (Maven, Ant, ...).

Resources
--------

- http://localhost:9000/rest/hello/jenkins?project=<name of the jenkins project to run>
  - start a jenkins build of the specified project and if it is finished return the test result as json.

Todo
--------
- implements a Jenkins Client based on the Jenkins Remote API 

After Deploy on Application Server
----------------------------------
- The REST WS are accessible under /rest/*

Creating a Local Maven Archetype
--------------------------------
- Generate an archetype: <code> mvn archetype:create-from-project </code>
- Go to <code>target/generated-sources/archetype</code> and run <code>mvn install </code>
- Create a fresh project from Archetype <code> mvn archetype:generate -DarchetypeCatalog=local </code> using <code>com.pampanet:jersey-guice-bootstrap-archetype </code>
- From Eclipse you have check the "include snapshot archetypes" checkbox, and select the archetype from the catalog after installing it.
