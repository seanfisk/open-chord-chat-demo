require "faker" # for fake names

desc 'Lab Exercise 3 - An old-school instant messaging implementation with Chord structured P2P using Distributed Hash Tables'
define 'talk-chord' do
  
  # arguments
  host = 'localhost'
  port = 2468
  
  # set up project specific information
  project.version = '1.0'
  project.group = 'gvsu'
  manifest['Copyright'] = 'Sean Fisk (C) 2011'
  compile.options.target = '1.6'
  
  # dependencies
  ## ibiblio public
  repositories.remote << 'http://www.ibiblio.org/maven2'
  ## maven central
  repositories.remote << 'http://repo1.maven.org/maven2/'
  ## apache
  repositories.remote << 'http://ftp.cica.es/mirrors/maven2/'
  
  ## jcommander
  JCOMMANDER = transitive('com.beust:jcommander:jar:1.17')
  
  ## jline
  ### constants
  JLINE_VERSION = '1.0'
  JLINE = "jline:jline:jar:#{JLINE_VERSION}"
  JLINE_FILE = "jline-#{JLINE_VERSION}"
  JLINE_URL = "http://sourceforge.net/projects/jline/files/jline/#{JLINE_VERSION}/#{JLINE_FILE}.zip/download"
  
  ### download from sourceforge
  jline_zip = download("#{path_to(:target)}/#{JLINE_FILE}.zip" => JLINE_URL)
  
  ## extract to target directory
  jline_unzipped_dir = unzip(path_to(:target) => jline_zip)
  
  ## get jar file
  jline_jar = file("#{jline_unzipped_dir}/#{JLINE_FILE}/#{JLINE_FILE}.jar" => jline_unzipped_dir)
  
  ## create artifact
  jline = artifact(JLINE).from(jline_jar)
  
  ## install to local maven repository
  install jline
  
  ## log4j - openchord dependency
  LOG4J = 'log4j:log4j:jar:1.2.15'
  
  ## openchord
  ### constants
  OPENCHORD_VERSION = '1.0.5'
  OPENCHORD = "openchord:openchord:jar:#{OPENCHORD_VERSION}"
  
  ## create artifact
  openchord = artifact(OPENCHORD).from("lib/openchord_#{OPENCHORD_VERSION}.jar")
  
  ## install to local maven repository
  install openchord
  
  # compilation
  compile.with JCOMMANDER, LOG4J, JLINE, OPENCHORD
  
  # packaging into a jar
  package :jar
  
  # running the application
  main_class = 'edu.gvsu.cis.cis656.lab2.ChatClient'
  manifest['Main-Class'] = main_class
  
  run.using :main => [main_class]#, Faker::Name.first_name, "#{host}:#{port}"]
end
