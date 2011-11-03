require "faker" # for fake names

desc 'Lab Exercise 3 - An old-school instant messaging implementation with Chord structured P2P using Distributed Hash Tables'
define 'talk-chord' do
  host = 'localhost'
  port = 2468
  
  # set up project specific information
  project.version = '1.0'
  project.group = 'gvsu'
  manifest['Copyright'] = 'Sean Fisk (C) 2011'
  compile.options.target = '1.6'
  
  # dependencies - openchord
  ## openchord constants
  OPENCHORD_VERSION = '1.0.5'
  OPENCHORD = "openchord:openchord:jar:#{OPENCHORD_VERSION}"
  OPENCHORD_FILE = "open-chord_#{OPENCHORD_VERSION}"
  OPENCHORD_URL = "http://sourceforge.net/projects/open-chord/files/Open%20Chord%201.0/#{OPENCHORD_VERSION}/#{OPENCHORD_FILE}.zip/download"
  
  ## download openchord from sourceforge
  openchord_zip = download("#{path_to(:target)}/#{OPENCHORD_FILE}.zip" => OPENCHORD_URL)
  
  ## extract openchord to target
  openchord_unzipped_dir = unzip(path_to(:target, OPENCHORD_FILE) => openchord_zip)
  
  ## get openchord jar file
  OPENCHORD_FILE.gsub!('-', '') # yay for consistent naming
  openchord_jar = file("#{openchord_unzipped_dir}/dist/#{OPENCHORD_FILE}.jar" => openchord_unzipped_dir)
  
  ## create openchord artifact
  openchord = artifact(OPENCHORD).from(openchord_jar)
  
  ## install to maven repository
  install openchord
  
  desc 'Common files'
  define 'common' do
    compile.with OPENCHORD
    
    package :jar
  end
  
  desc 'Presence service'
  define 'server' do
    main_class = 'edu.gvsu.cis.cis656.lab2.PresenceServiceImpl'
    java_security_policy = "#{project.base_dir}/security.policy"
    
    compile.with project('common'), OPENCHORD
    manifest['Main-Class'] = main_class
    package(:jar).merge(project('common'))
    
    run.using :main => [main_class, port],
    :properties => {
      'java.rmi.server.codebase' => "file://#{project('common').package}",
      'java.security.policy' => java_security_policy
    }
    
    desc 'Run the server jar'
    task 'run-jar' => ['package'] do
      sh "java \
'-Djava.rmi.server.codebase=file://#{package}' \
'-Djava.security.policy=#{java_security_policy}' \
-jar '#{package}' #{port}"
    end
  end
  
  desc 'Chat client'
  define 'client' do
    main_class = 'edu.gvsu.cis.cis656.lab2.ChatClient'
    java_security_policy = "#{project.base_dir}/security.policy"
    
    # dependencies - jline
    ## jline constants
    JLINE_VERSION = '1.0'
    JLINE = "jline:jline:jar:#{JLINE_VERSION}"
    JLINE_FILE = "jline-#{JLINE_VERSION}"
    JLINE_URL = "http://sourceforge.net/projects/jline/files/jline/#{JLINE_VERSION}/#{JLINE_FILE}.zip/download"
    
    ## download jline from sourceforge
    jline_zip = download("#{path_to(:target)}/#{JLINE_FILE}.zip" => JLINE_URL)
    
    ## extract jline to target
    jline_unzipped_dir = unzip(path_to(:target) => jline_zip)
    
    ## get jline jar file
    jline_jar = file("#{jline_unzipped_dir}/#{JLINE_FILE}.jar" => jline_unzipped_dir)
    
    ## create jline artifact
    jline = artifact(JLINE).from(jline_jar)
    
    ## install to maven repository
    install jline
    
    compile.with project('common'), JLINE, OPENCHORD
    manifest['Main-Class'] = main_class
    package(:jar).merge(project('common'))
    
    run.using :main => [main_class, Faker::Name.first_name, "#{host}:#{port}"],
    :properties => {
      'java.rmi.server.codebase' => "file://#{project('common').package}",
      'java.security.policy' => java_security_policy
    }
    
    desc 'Run the client jar'
    task 'run-jar' => ['package'] do
      sh "java \
'-Djava.rmi.server.codebase=file://#{package}' \
'-Djava.security.policy=#{java_security_policy}' \
-jar '#{package}' \
#{Faker::Name.first_name} #{host}:#{port}"
    end
  end
  
  desc 'Convenience method to run the rmiregistry on the test port'
  task 'rmiregistry' do
    puts "Running rmiregistry in the background on port #{port}"
  	sh "rmiregistry #{port} &"
  end
end

