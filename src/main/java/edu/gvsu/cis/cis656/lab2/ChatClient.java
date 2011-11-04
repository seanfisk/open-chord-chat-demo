/**
 * 
 */
package edu.gvsu.cis.cis656.lab2;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

import jline.ConsoleReader;
import jline.SimpleCompletor;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.internal.Lists;

import de.uniba.wiai.lspi.chord.service.ServiceException;
import edu.gvsu.cis.cis656.lab2.command.AvailableCommand;
import edu.gvsu.cis.cis656.lab2.command.BusyCommand;
import edu.gvsu.cis.cis656.lab2.command.Command;
import edu.gvsu.cis.cis656.lab2.command.ExitCommand;
import edu.gvsu.cis.cis656.lab2.command.TalkCommand;
import edu.gvsu.cis.cis656.lab2.util.PromptBuilder;

/**
 * @author Sean Fisk <fiskse@mail.gvsu.edu>
 */
public class ChatClient
{
	private class CommandLineOptions
	{
		@Parameter
		private List<String> parameters = Lists.newArrayList();
		
		@Parameter(names = "-master", description = "Make this client the master node.")
		private boolean master = true;
				
		public List<String> getParameters()
		{
			return parameters;
		}
		
		public boolean getMaster()
		{
			return master;
		}
	}
	
	public static void main(String[] args)
	{
		new ChatClient().go(args);
	}

	void go(String[] args)
	{
		CommandLineOptions options = new CommandLineOptions();
		JCommander jcommander = new JCommander(options, args);
		List<String> parameters = options.getParameters();
		
		// check number of args
		if(parameters.size() < 1 || parameters.size() > 2)
		{
			jcommander.usage();
			System.exit(1);
		}

		// parse args
		String userName = parameters.get(0);
		String host = null;
		int port = 0;
		if(parameters.size() == 2)
		{
			String hostPort[] = parameters.get(1).split(":", 2);
			if(hostPort.length > 0)
			{
				host = hostPort[0];

				// print usage - must give a host if a port is given
				if(host.length() == 0)
				{					
					jcommander.usage();
					System.exit(1);
				}
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

		try
		{
			// construct presence service
			PresenceService presenceService = new PresenceServiceImpl(options.getMaster(), host, port);

			// bind the server socket behind the message listener
			MessageListener messageListener = new MessageListener();

			// set up registration info
			// the address provided by
			// `messageListener.getInetAddress().getHostAddress()' will most
			// likely be wrong, and it almost definitely won't be an external IP
			// it gets fixed at the server
			RegistrationInfo userInfo = new RegistrationInfo(userName, messageListener.getInetAddress().getHostAddress(), messageListener.getLocalPort(), true);

			// register with the presence service
			presenceService.register(userInfo);

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

			// add available commands
			Command[] commandList = {new TalkCommand(presenceService, userInfo), new BusyCommand(presenceService, userInfo), new AvailableCommand(presenceService, userInfo), new ExitCommand(presenceService, userInfo)};
			LinkedHashMap<String, Command> commands = new LinkedHashMap<String, Command>();
			String[] simpleCommands = new String[commandList.length];
			int commandIndex = 0;
			for(Command command : commandList)
			{
				simpleCommands[commandIndex++] = command.getName();
				commands.put(command.getName(), command);
			}
			
			SimpleCompletor simpleCommandsCompletor = new SimpleCompletor(simpleCommands);
			consoleReader.addCompletor(simpleCommandsCompletor);

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

			// start the message listener
			messageListener.setConsoleReader(consoleReader);
			new Thread(messageListener).start();

			// enter command loop
			Scanner scanner;
			Pattern zeroBytePattern = Pattern.compile("\\z");
			while(true)
			{
				// print prompt
				String line = consoleReader.readLine(PromptBuilder.buildPrompt(userInfo));

				Command command;
				String commandArgs;

				// they entered something
				if(line != null)
				{
					scanner = new Scanner(line);

					// grab command with default delimiter
					// Character.isWhitespace()
					String commandString;
					try
					{
						commandString = scanner.next();
					}
					catch(NoSuchElementException e)
					{
						// They didn't enter anything
						continue;
					}

					command = commands.get(commandString);

					// check invalid
					if(command == null)
					{
						System.err.println("Invalid command.");
						continue;
					}

					// execute command
					scanner.useDelimiter(zeroBytePattern);
					commandArgs = scanner.hasNext() ? scanner.next() : null;
				}
				// they pressed Ctrl-D or equivalent, they want to quit
				else
				{
					// get exit command name
					String exitCommandName = simpleCommands[simpleCommands.length - 1];

					// they didn't press enter, print "exit" and a new line
					System.out.println(exitCommandName);

					// actually quit, without parsing anything
					command = commands.get(exitCommandName);
					commandArgs = null;
				}

				command.execute(commandArgs);

				// exit on exit command (cannot `break' in a class)
				if(command instanceof ExitCommand)
					break;
			}

			// close the listening thread
			messageListener.close();
		}
		catch(ServiceException e)
		{
			System.err.println("OpenChord error.");
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
		System.err.println("Usage: java edu.gvsu.cis.cis656.lab2.ChatClient [-master] user [host[:port]]");
		System.exit(1);
	}
}
