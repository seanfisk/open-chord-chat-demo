require 'faker' # for fake names

desc 'Lab Exercise 3 - An old-school instant messaging implementation with Chord structured P2P using Distributed Hash Tables'
define 'talk-chord' do

  # arguments
  host = 'localhost'
  port = 9999

  # set up project specific information
  project.version = '1.0'
  project.group = 'gvsu'
  manifest['Copyright'] = 'Sean Fisk (C) 2011'

  # dependencies
  repositories.remote << 'http://repo1.maven.org/maven2'

  ## jcommander
  JCOMMANDER = transitive('com.beust:jcommander:jar:1.17')

  ## jline
  JLINE = transitive('jline:jline:jar:1.0')

  ## openchord
  ### constants
  OPENCHORD_VERSION = '1.0.5'
  OPENCHORD = "openchord:openchord:jar:#{OPENCHORD_VERSION}"

  ## log4j - optional openchord dependency
  LOG4J = 'log4j:log4j:jar:1.2.15'

  ## create artifact
  openchord = artifact(OPENCHORD).from("lib/openchord_#{OPENCHORD_VERSION}.jar")

  ## install to local maven repository
  install openchord

  # compilation
  compile.with JCOMMANDER, JLINE, OPENCHORD, LOG4J

  # packaging into a jar
  package :jar

  # running the application
  main_class = 'edu.gvsu.cis.cis656.lab3.ChatClient'
  manifest['Main-Class'] = main_class

  ## two ways to run it - normal and master
  ## please see <http://stackoverflow.com/questions/577944/how-to-run-rake-tasks-from-within-rake-tasks>
  task 'run-normal' do
    run.using(:main => [main_class, Faker::Name.first_name, "#{host}:#{port}"]).invoke
  end
  task 'run-master' do
    run.using(:main => [main_class, '--master', port, Faker::Name.first_name]).invoke
  end
end
