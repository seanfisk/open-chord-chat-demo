package edu.gvsu.cis.cis656.lab2.command;

import java.rmi.RemoteException;

import edu.gvsu.cis.cis656.lab2.PresenceService;
import edu.gvsu.cis.cis656.lab2.RegistrationInfo;

public class BroadcastCommand extends TalkingCommand
{
	public BroadcastCommand(PresenceService presenceService, RegistrationInfo userInfo)
	{
		super("broadcast", "{message}", "send a message to all available users", presenceService, userInfo);
	}

	public void execute(String args) throws RemoteException
	{
		if(args.length() == 0)
		{
			System.out.println("\nIncorrect format.\n" + this);
			return;
		}
		for(RegistrationInfo otherUserInfo : presenceService.listRegisteredUsers())
		{
			if(otherUserInfo.getStatus() && !userInfo.getUserName().equals(otherUserInfo.getUserName()))
				sendMessageToUser(otherUserInfo.getUserName(), args);
		}
	}
}