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
    compile.with project('common')
    package(:jar).merge(project('common'))
  end
  
  desc 'Chat client'
  define 'client' do
    compile.with project('common')
    package(:jar).merge(project('common'))
  end
end  
