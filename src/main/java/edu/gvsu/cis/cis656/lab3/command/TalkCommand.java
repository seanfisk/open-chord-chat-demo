package edu.gvsu.cis.cis656.lab3.command;

import java.util.NoSuchElementException;
import java.util.Scanner;

import de.uniba.wiai.lspi.chord.service.ServiceException;
import edu.gvsu.cis.cis656.lab3.PresenceService;
import edu.gvsu.cis.cis656.lab3.RegistrationInfo;

// talk
public class TalkCommand extends TalkingCommand
{
	public TalkCommand(PresenceService presenceService, RegistrationInfo userInfo)
	{
		super("talk", "{username} {message}", "send a message to another user", presenceService, userInfo);
	}

	public void execute(String args) throws ServiceException
	{
		if(args == null)
		{
			System.out.println("\nIncorrect format.\n" + this);
			return;
		}
		Scanner scanner = new Scanner(args);
		String recipient;
		String message;
		try
		{
			recipient = scanner.next();
			scanner.skip("\\s*"); // skip whitespace
			message = scanner.nextLine(); // grab rest of line
		}
		catch(NoSuchElementException e)
		{
			System.out.println("\nIncorrect format.\n" + this);
			return;
		}

		sendMessageToUser(recipient, message);
	}
}