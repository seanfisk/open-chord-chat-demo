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
import java.util.NoSuchElementException;
import java.util.Scanner;
import jline.*;

/**
 * @author Sean Fisk <fiskse@mail.gvsu.edu>
 */
public class ChatClient
{
	private static final int COLUMN_WIDTH = -40;
	private static final String TWO_COLUMN_FORMAT = "%" + COLUMN_WIDTH + "s%" + COLUMN_WIDTH + "s\n";

	private PresenceService presenceService = null;
	private String userName;
	private RegistrationInfo regInfo;

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
		userName = args[0];
		String host = null;
		int port = 0;
		if(args.length == 2)
		{
			String hostPort[] = args[1].split(":", 2);
			if(hostPort.length > 0)
			{
				host = hostPort[0];

				// print usage - must give a host if a port is given
				if(host.length() == 0)
					usage();
			}
			if(hostPort.length > 1)
			{
				try
				{
					port = Integer.parseInt(hostPort[1]);
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
			// make absolutely sure we always get the JVM defaults (and don't
			// just supply them)
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
			// the address provided by
			// `messageListener.getInetAddress().getHostAddress()' will most
			// likely be wrong, and it almost definitely won't be an external IP
			// it gets fixed at the server
			regInfo = new RegistrationInfo(userName, messageListener.getInetAddress().getHostAddress(), messageListener.getLocalPort(), true);
			messageListener.setRegistrationInfo(regInfo);

			// register with the presence service
			if(!presenceService.register(regInfo))
			{
				System.err.println("Sorry, the name `" + userName + "' is taken.");
				System.exit(1);
			}

			// start the message listener
			new Thread(messageListener).start();

			// add available commands
			LinkedHashMap<String, Command> commands = new LinkedHashMap<String, Command>();
			commands.put("friends", new FriendsCommand());
			commands.put("talk", new TalkCommand());
			commands.put("broadcast", new BroadcastCommand());
			commands.put("busy", new BusyCommand());
			commands.put("available", new AvailableCommand());
			commands.put("exit", new ExitCommand());

			// print out command list
			System.out.println();
			System.console().printf(TWO_COLUMN_FORMAT, "Command", "Description");
			for(int i = 0; i < 2 * -COLUMN_WIDTH; ++i)
				System.out.print('-');
			System.out.println('\n');
			for(Command value : commands.values())
				System.out.print(value);
			System.out.println();

			// enter command loop
			Scanner scanner = new Scanner(System.in);
			ConsoleReader consoleReader = new ConsoleReader();

			while(true)
			{
				// print prompt
				System.out.print(userName + ':' + (regInfo.getStatus() ? "available" : "busy") + "> ");

				// grab command
				Command command = commands.get(scanner.next());

				// check invalid
				if(command == null)
				{
					System.err.println("Invalid command.");
					continue;
				}

				// execute command
				command.execute(scanner.nextLine());

				// exit on exit command (cannot `break' in a class)
				if(command instanceof ExitCommand)
					break;
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

	// helpers
	private void sendMessageToUser(String recipient, String message) throws RemoteException
	{
		RegistrationInfo reg = presenceService.lookup(recipient);

		if(reg == null)
		{
			System.out.println("User `" + recipient + "' isn't registered on this server.");
			return;
		}

		if(!reg.getStatus())
		{
			System.out.println("User `" + recipient + "' isn't available right now.");
			return;
		}

		try
		{
			Socket socket = new Socket(reg.getHost(), reg.getPort());
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			out.println(userName + "> " + message);
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

	// set availability
	private void setAvailability(boolean available) throws RemoteException
	{
		if(available == regInfo.getStatus())
			System.out.println("Note: You are already " + (available ? "available" : "busy") + ".");
		regInfo.setStatus(available);
		presenceService.updateRegistrationInfo(regInfo);
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
			System.out.println();
			System.console().printf(TWO_COLUMN_FORMAT, "Availability", "User");
			for(int i = 0; i < 2 * -COLUMN_WIDTH; ++i)
				System.out.print('-');
			System.out.println('\n');
			for(RegistrationInfo reg : presenceService.listRegisteredUsers())
				System.console().printf(TWO_COLUMN_FORMAT, reg.getStatus() ? "Available" : "Busy", reg.getUserName() + (userName.equals(reg.getUserName()) ? " (You)" : ""));
			System.out.println();

		}

		public String toString()
		{
			return String.format(TWO_COLUMN_FORMAT, "friends", "list registered users and their availability");
		}
	}

	// broadcast
	private class BroadcastCommand implements Command
	{
		public void execute(String args) throws RemoteException
		{
			if(args.length() == 0)
			{
				System.out.println("\nIncorrect format.\n" + this);
				return;
			}
			for(RegistrationInfo reg : presenceService.listRegisteredUsers())
			{
				if(reg.getStatus() && !userName.equals(reg.getUserName()))
					sendMessageToUser(reg.getUserName(), args);
			}
		}

		public String toString()
		{
			return String.format(TWO_COLUMN_FORMAT, "broadcast {message}", "send a message to all available users");
		}
	}

	// talk
	private class TalkCommand implements Command
	{
		public void execute(String args) throws RemoteException
		{
			Scanner scanner = new Scanner(args);
			String recipient;
			String message;
			try
			{
				recipient = scanner.next();
				message = scanner.nextLine();
			}
			catch(NoSuchElementException e)
			{
				System.out.println("\nIncorrect format.\n" + this);
				return;
			}

			sendMessageToUser(recipient, message);
		}

		public String toString()
		{
			return String.format(TWO_COLUMN_FORMAT, "talk {username} {message}", "send a message to another user");
		}
	}

	// busy
	private class BusyCommand implements Command
	{
		public void execute(String args) throws RemoteException
		{
			setAvailability(false);
		}

		public String toString()
		{
			return String.format(TWO_COLUMN_FORMAT, "busy", "do not receive messages");
		}
	}

	// available
	private class AvailableCommand implements Command
	{
		public void execute(String args) throws RemoteException
		{
			setAvailability(true);
		}

		public String toString()
		{
			return String.format(TWO_COLUMN_FORMAT, "available", "receive messages");
		}
	}

	// exit
	private class ExitCommand implements Command
	{
		public void execute(String args) throws RemoteException
		{
			presenceService.unregister(userName);
		}

		public String toString()
		{
			return String.format(TWO_COLUMN_FORMAT, "exit", "exit the chat client");
		}
	}
}
