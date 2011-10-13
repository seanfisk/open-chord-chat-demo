/**
 * 
 */
package edu.gvsu.cis.cis656.lab2;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedHashMap;
import java.util.Scanner;

/**
 * @author Sean Fisk <fiskse@mail.gvsu.edu>
 */
public class ChatClient
{
	private PresenceService presenceService = null;
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		new ChatClient().go(args);
	}
	
	void go(String[] args)
	{
		// check number of args
		if(args.length < 1 || args.length > 2 || args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h"))
			usage();
		
		// parse args
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

		// set up security manager if it doesn't exist
		if(System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());
		
		try
		{
			// get registry
			// make absolutely sure we always get the JVM defaults (and don't just supply them)
			Registry registry;
			if(host != null && port != 0)
				registry = LocateRegistry.getRegistry(host, port);
			else if(host != null)
				registry = LocateRegistry.getRegistry(host);
			else
				registry = LocateRegistry.getRegistry();
			
			// get the handle to the presence service
			presenceService = (PresenceService)registry.lookup("PresenceService");
			
			// bind the server socket behind the message listener
			MessageListener messageListener = new MessageListener();
			
			// set up registration info
			RegistrationInfo reg = new RegistrationInfo(
					username,
					messageListener.getInetAddress().getHostAddress(),
					messageListener.getLocalPort(),
					true);
			
			// register with the presence service
			if(!presenceService.register(reg))
			{
				System.err.println("Sorry, the name `" + username + "' is taken.");
				System.exit(1);
			}
			
			// start the message listener
			new Thread(messageListener).start();
			
			// add available commands
			LinkedHashMap<String, Command> commands = new LinkedHashMap<String, Command>();
			commands.put("friends", new FriendsCommand());
			commands.put("talk", new TalkCommand());
			commands.put("quit", new QuitCommand());
			
			// print out command list
			for(Command value : commands.values())
				System.out.println(value);
						
			// enter command loop
			Scanner scanner = new Scanner(System.in);
			
			while(true)
			{
				// grab command
				Command command = commands.get(scanner.next());
				
				// check invalid
				if(command == null)
				{
					System.err.println("Invalid command.");
					continue;
				}
				
				// quit on quit command (cannot `break' in a class)
				if(command instanceof QuitCommand)
					break;
				
				// execute command
				command.execute(scanner.nextLine());
			}
			
			// close the listening thread
			messageListener.close();
		}
		catch(AccessException e)
		{
			System.err.println("Cannot access RMI resource.");
			e.printStackTrace();
			System.exit(1);
		}
		catch(RemoteException e)
		{
			System.err.println("RMI error.");
			e.printStackTrace();
			System.exit(1);
		}
		catch(NotBoundException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static void usage()
	{
		System.err.println("Usage: java edu.gvsu.cis.cis656.lab2.ChatClient user [host[:port]]");
		System.exit(1);
	}
	
	// commands
	private interface Command
	{
		public void execute(String args) throws RemoteException;
		public String toString();
	}
		
	// friends
	private class FriendsCommand implements Command
	{
		public void execute(String args) throws RemoteException
		{
			System.out.println(presenceService.listRegisteredUsers());
		}
		
		public String toString()
		{
			return "friends						list registered users and their availability";
		}
	}
	
	// quit
	private class QuitCommand implements Command
	{
		public void execute(String args) throws RemoteException
		{
			
		}
		
		public String toString()
		{
			return "quit						Exit the chat client";
		}
	}
	
	// talk
	private class TalkCommand implements Command
	{
		public void execute(String args) throws RemoteException
		{
			Scanner scanner = new Scanner(args);
			String username = scanner.next();
			String message = scanner.nextLine();
			RegistrationInfo reg = presenceService.lookup(username);
			
			if(reg == null)
			{
				System.out.println("User `" + username + "' isn't registered on this server.");
				return;
			}
			
			if(!reg.getStatus())
			{
				System.out.println("User `" + username + "' isn't available right now.");
				return;
			}
			
			try
			{
				Socket socket = new Socket(reg.getHost(), reg.getPort());
				PrintWriter out = new PrintWriter(socket.getOutputStream());
				out.println(message);
				out.close();
				socket.close();
			}
			catch(UnknownHostException e)
			{
				System.err.println("Unknown host.");
				e.printStackTrace();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			
		}
		
		public String toString()
		{
			return "talk {username} {message}	send a message to another user";
		}
	}
}
