package edu.gvsu.cis.cis656.lab2.command;

import java.rmi.RemoteException;
import java.util.Vector;

import edu.gvsu.cis.cis656.lab2.PresenceService;
import edu.gvsu.cis.cis656.lab2.RegistrationInfo;

public class FriendsCommand extends Command
{
	public FriendsCommand(PresenceService presenceService, RegistrationInfo userInfo)
	{
		super("friends", null, "list registered users and their availability", presenceService, userInfo);
	}

	public void execute(String args) throws RemoteException
	{
		Vector<RegistrationInfo> registeredUsers = presenceService.listRegisteredUsers();
		if(registeredUsers.size() == 1)
		{
			System.out.println("Noone else is currently registered...");
			return;
		}
		final int COLUMN_WIDTH = 20;
		final String TWO_COLUMN_FORMAT = "%" + -COLUMN_WIDTH + "s%" + -COLUMN_WIDTH + "s\n";
		System.out.println();
		System.console().printf(TWO_COLUMN_FORMAT, "availability", "user");
		for(int i = 0; i < 2 * COLUMN_WIDTH; ++i)
			System.out.print('-');
		System.out.println('\n');
		for(RegistrationInfo otherUserInfo : registeredUsers)
			if(!userInfo.getUserName().equals(otherUserInfo.getUserName()))
				System.console().printf(TWO_COLUMN_FORMAT, otherUserInfo.getStatus() ? "available" : "busy", otherUserInfo.getUserName());
		System.out.println();
	}
}