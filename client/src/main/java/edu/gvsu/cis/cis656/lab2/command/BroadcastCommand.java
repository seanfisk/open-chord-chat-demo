package edu.gvsu.cis.cis656.lab2.command;

import java.rmi.RemoteException;

import edu.gvsu.cis.cis656.lab2.PresenceService;
import edu.gvsu.cis.cis656.lab2.RegistrationInfo;

public class BroadcastCommand extends TalkingCommand
{
	public BroadcastCommand(PresenceService presenceService, RegistrationInfo regInfo)
	{
		super("broadcast", "{message}", "send a message to all available users", presenceService, regInfo);
	}

	public void execute(String args) throws RemoteException
	{
		if(args.length() == 0)
		{
			System.out.println("\nIncorrect format.\n" + this);
			return;
		}
		for(RegistrationInfo reg : presenceService.listRegisteredUsers())
		{
			if(reg.getStatus() && !regInfo.getUserName().equals(reg.getUserName()))
				sendMessageToUser(reg.getUserName(), args);
		}
	}
}