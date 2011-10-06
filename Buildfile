desc 'Lab Exercise 2 - An old-school instant messaging implementation'
define 'old-school-im' do

  project.version = '1.0'
  
  desc 'Server'
  define 'presence-service' do
    package :jar
  end

  desc 'Client'
  define 'chat-client' do
    package :jar
  end

end  
