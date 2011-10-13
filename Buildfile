require "faker" # for fake names

desc 'Lab Exercise 2 - An old-school instant messaging implementation'
define 'talk' do
  
  # set up project specific information
  project.version = '1.0'
  project.group = 'gvsu'
  manifest['Copyright'] = 'Sean Fisk (C) 2011'
  compile.options.target = '1.6'
  
  desc 'Common files'
  define 'common' do
    package :jar
  end
  
  desc 'Presence service'
  define 'server' do
    main_class = 'edu.gvsu.cis.cis656.lab2.PresenceServiceImpl'
    java_rmi_server_hostname = 'localhost'
    java_security_policy = "#{project.base_dir}/security.policy"
    
    compile.with project('common')
    manifest['Main-Class'] = main_class
    package(:jar).merge(project('common'))
    
    run.using :main => main_class,
    :properties => {
      'java.rmi.server.hostname' => java_rmi_server_hostname,
      'java.rmi.server.codebase' => "file://#{project('common').package}",
      'java.security.policy' => java_security_policy
    }
    
    desc 'Run the server jar'
    task 'run-jar' => ['package'] do
      sh "java \
-Djava.rmi.server.hostname=#{java_rmi_server_hostname} \
'-Djava.rmi.server.codebase=file://#{package}' \
'-Djava.security.policy=#{java_security_policy}' \
-jar '#{package}'"
    end
  end
  
  desc 'Chat client'
  define 'client' do
    main_class = 'edu.gvsu.cis.cis656.lab2.ChatClient'
    java_rmi_server_hostname = 'localhost'
    java_security_policy = "#{project.base_dir}/security.policy"
    
    compile.with project('common')
    manifest['Main-Class'] = main_class
    package(:jar).merge(project('common'))
    
    run.using :main => [main_class, Faker::Name.first_name],
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
#{Faker::Name.first_name}"
    end
  end
end

