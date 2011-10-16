/**
 * 
 */
package edu.gvsu.cis.cis656.lab2;

import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

import jline.ArgumentCompletor;
import jline.Completor;
import jline.ConsoleReader;
import jline.MultiCompletor;
import jline.NullCompletor;
import jline.SimpleCompletor;
import edu.gvsu.cis.cis656.lab2.command.AvailableCommand;
import edu.gvsu.cis.cis656.lab2.command.BroadcastCommand;
import edu.gvsu.cis.cis656.lab2.command.BusyCommand;
import edu.gvsu.cis.cis656.lab2.command.Command;
import edu.gvsu.cis.cis656.lab2.command.ExitCommand;
import edu.gvsu.cis.cis656.lab2.command.FriendsCommand;
import edu.gvsu.cis.cis656.lab2.command.TalkCommand;
import edu.gvsu.cis.cis656.lab2.completor.FriendCompletor;

/**
 * @author Sean Fisk <fiskse@mail.gvsu.edu>
 */
public class ChatClient
{
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
		String userName = args[0];
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
			PresenceService presenceService = (PresenceService) registry.lookup("PresenceService");

			// bind the server socket behind the message listener
			MessageListener messageListener = new MessageListener();

			// set up registration info
			// the address provided by
			// `messageListener.getInetAddress().getHostAddress()' will most
			// likely be wrong, and it almost definitely won't be an external IP
			// it gets fixed at the server
			RegistrationInfo userInfo = new RegistrationInfo(userName, messageListener.getInetAddress().getHostAddress(), messageListener.getLocalPort(), true);
			messageListener.setRegistrationInfo(userInfo);

			// register with the presence service
			if(!presenceService.register(userInfo))
			{
				System.err.println("Sorry, the name `" + userName + "' is taken.");
				System.exit(1);
			}

			// start the message listener
			new Thread(messageListener).start();

			// set up JLine console reader
			ConsoleReader consoleReader = null;
			try
			{
				consoleReader = new ConsoleReader();
			}
			catch(IOException e)
			{
				System.err.println("Error creating the JLine console reader.");
				e.printStackTrace();
				System.exit(1);
			}

			// use bell, history, and pagination
			consoleReader.setBellEnabled(true);
			consoleReader.setUseHistory(true);
			consoleReader.setUsePagination(true);

			String[] simpleCommands = {"friends", "broadcast", "busy", "available", "exit"};
			SimpleCompletor simpleCommandsCompletor = new SimpleCompletor(simpleCommands);
			SimpleCompletor talkCommandPrefixCompletor = new SimpleCompletor("talk");
			FriendCompletor talkCommandFriendCompletor = new FriendCompletor(presenceService);
			Completor[] talkCommandArguments = {talkCommandPrefixCompletor, talkCommandFriendCompletor, new NullCompletor()};
			ArgumentCompletor talkCommandCompletor = new ArgumentCompletor(talkCommandArguments);
			MultiCompletor globalCompletor = new MultiCompletor(new Completor[] {simpleCommandsCompletor, talkCommandCompletor});
			consoleReader.addCompletor(globalCompletor);

			// add available commands
			Command[] commandList = {new FriendsCommand(presenceService, userInfo), new TalkCommand(presenceService, userInfo), new BroadcastCommand(presenceService, userInfo), new BusyCommand(presenceService, userInfo), new AvailableCommand(presenceService, userInfo), new ExitCommand(presenceService, userInfo)};
			LinkedHashMap<String, Command> commands = new LinkedHashMap<String, Command>();
			for(Command command : commandList)
				commands.put(command.getName(), command);

			// print out command list
			System.out.println();
			final int COLUMN_WIDTH = 40;
			final String TWO_COLUMN_FORMAT = "%" + -COLUMN_WIDTH + "s%" + -COLUMN_WIDTH + "s\n";
			System.console().printf(TWO_COLUMN_FORMAT, "command", "description");
			for(int i = 0; i < 2 * COLUMN_WIDTH; ++i)
				System.out.print('-');
			System.out.println('\n');
			for(Command value : commands.values())
			{
				String commandStr = value.getName() + (value.getArgFormat() != null ? ' ' + value.getArgFormat() : "");
				System.out.printf(TWO_COLUMN_FORMAT, commandStr, value.getDescription());
			}
			System.out.println();

			// enter command loop
			Scanner scanner;
			Pattern zeroBytePattern = Pattern.compile("\\z");
			while(true)
			{
				// print prompt
				String line = consoleReader.readLine(userName + ':' + (userInfo.getStatus() ? "available" : "busy") + "> ");
				scanner = new Scanner(line);
				// System.out.print(userName + ':' + (regInfo.getStatus() ?
				// "available" : "busy") + "> ");

				// grab command with default delimiter Character.isWhitespace()
				Command command = commands.get(scanner.next());

				// check invalid
				if(command == null)
				{
					System.err.println("Invalid command.");
					continue;
				}

				// execute command
				scanner.useDelimiter(zeroBytePattern);
				command.execute(scanner.hasNext() ? scanner.next() : null);

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
		catch(IOException e)
		{
			System.err.println("Error reading from the console.");
			e.printStackTrace();
		}
	}

	private static void usage()
	{
		System.err.println("Usage: java edu.gvsu.cis.cis656.lab2.ChatClient user [host[:port]]");
		System.exit(1);
	}
}
