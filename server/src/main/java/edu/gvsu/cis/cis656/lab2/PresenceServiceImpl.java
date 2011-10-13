/**
 * 
 */
package edu.gvsu.cis.cis656.lab2;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Vector;

/**
 * @author Sean Fisk <fiskse@mail.gvsu.edu>
 */
public class PresenceServiceImpl implements PresenceService
{
	private HashMap<String, RegistrationInfo> users;

	public PresenceServiceImpl()
	{
		super();
		users = new HashMap<String, RegistrationInfo>();
	}
	
	@Override public boolean register(RegistrationInfo reg) throws RemoteException
	{
		if(users.containsKey(reg.getUserName()))
			return false;
		users.put(reg.getUserName(), reg);
		return true;
	}

	@Override public boolean updateRegistrationInfo(RegistrationInfo reg) throws RemoteException
	{
		if(!users.containsKey(reg.getUserName()))
			return false;
		users.put(reg.getUserName(), reg);
		return true;
	}

	@Override public void unregister(String userName) throws RemoteException
	{
		users.remove(userName);
	}

	@Override public RegistrationInfo lookup(String name) throws RemoteException
	{
		return users.get(name);
	}

	@Override public Vector<RegistrationInfo> listRegisteredUsers() throws RemoteException
	{
		return (Vector<RegistrationInfo>) users.values();
	}

	/**
	 * Main method
	 * 
	 * @param args
	 *            Command-line arguments
	 */
	public static void main(String[] args)
	{
		// check args, print usage if necessary
		if(args.length > 1 || (args.length == 1 && (args[0].equalsIgnoreCase("-h") || args[0].equalsIgnoreCase("--help"))))
		{
			System.err.println("Usage: java PresenceServiceImpl [port]");
			System.exit(1);
		}

		// start rmi connection
		if(System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());

		try
		{
			String name = "PresenceService";
			PresenceService presenceService = new PresenceServiceImpl();
			PresenceService stub = (PresenceService) UnicastRemoteObject.exportObject(presenceService, 0);

			Registry registry;
			if(args.length == 1)
			{
				// parse port
				int port = 0;
				try
				{
					port = Integer.parseInt(args[0]);
				}
				catch(NumberFormatException e) // for Integer.parseInt()
				{
					System.err.println("Invalid port number.");
					e.printStackTrace();
					System.exit(1);
				}
				registry = LocateRegistry.getRegistry(port);
			}
			else
				registry = LocateRegistry.getRegistry();
			registry.rebind(name, stub);
			System.out.println("PresenceServiceImpl bound");
		}
		catch(Exception e)
		{
			System.err.println("PresenceServiceImpl exception:");
			e.printStackTrace();
		}

	}
}
