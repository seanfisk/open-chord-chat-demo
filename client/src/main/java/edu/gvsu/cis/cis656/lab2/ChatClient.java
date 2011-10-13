/**
 * 
 */
package edu.gvsu.cis.cis656.lab2;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author Sean Fisk <fiskse@mail.gvsu.edu>
 */
public class ChatClient
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		if(args.length < 1 || args.length > 2 || args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h"))
			usage();
		
		String username = args[0];
		String host = null;
		int port = 0;
		if(args.length == 2)
		{
			String hostport[] = args[1].split(":", 2);
			if(hostport.length > 0)
			{
				host = hostport[0];
				
				// print usage - must give a host if a port is given
				if(host == "")
					usage();
			}
			if(hostport.length > 1)
			{
				try
				{
					port = Integer.parseInt(hostport[1]); 
				}
				catch(NumberFormatException e) // for Integer.parseInt()
				{
					System.err.println("Invalid port number.");
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
		
		if(System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());
		try
		{
			// make absolutely sure we always get the JVM defaults (and don't just supply them)
			Registry registry;
			if(host != null && port != 0)
				registry = LocateRegistry.getRegistry(host, port);
			else if(host != null)
				registry = LocateRegistry.getRegistry(host);
			else
				registry = LocateRegistry.getRegistry();
			
			String name = "PresenceService";
			PresenceService presenceService = (PresenceService)registry.lookup(name);
			
			RegistrationInfo reg = new RegistrationInfo(username, "localhost", 0, true);
			System.out.println("Success? " + presenceService.register(reg));
		}
		catch(Exception e)
		{
			System.err.println("PresenceService exception:");
			e.printStackTrace();
		}
	}

	private static void usage()
	{
		System.err.println("Usage: java edu.gvsu.cis.cis656.lab2.ChatClient user [host[:port]]");
		System.exit(1);
	}
}
