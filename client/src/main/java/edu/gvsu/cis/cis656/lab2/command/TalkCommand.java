package edu.gvsu.cis.cis656.lab2.command;

import java.rmi.RemoteException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import edu.gvsu.cis.cis656.lab2.PresenceService;
import edu.gvsu.cis.cis656.lab2.RegistrationInfo;

// talk
public class TalkCommand extends TalkingCommand
{
	public TalkCommand(PresenceService presenceService, RegistrationInfo userInfo)
	{
		super("talk", "{username} {message}", "send a message to another user", presenceService, userInfo);
	}

	public void execute(String args) throws RemoteException
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
			message = scanner.nextLine();
		}
		catch(NoSuchElementException e)
		{
			System.out.println("\nIncorrect format.\n" + this);
			return;
		}

		sendMessageToUser(recipient, message);
	}
}