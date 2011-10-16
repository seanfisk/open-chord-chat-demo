package edu.gvsu.cis.cis656.lab2.command;

import java.rmi.RemoteException;

import edu.gvsu.cis.cis656.lab2.PresenceService;
import edu.gvsu.cis.cis656.lab2.RegistrationInfo;

public class FriendsCommand extends Command
{
	public FriendsCommand(PresenceService presenceService, RegistrationInfo regInfo)
	{
		super("friends", null, "list registered users and their availability", presenceService, regInfo);
	}

	public void execute(String args) throws RemoteException
	{
		final int COLUMN_WIDTH = 20;
		final String TWO_COLUMN_FORMAT = "%" + -COLUMN_WIDTH + "s%" + -COLUMN_WIDTH + "s\n";
		System.out.println();
		System.console().printf(TWO_COLUMN_FORMAT, "availability", "user");
		for(int i = 0; i < 2 * COLUMN_WIDTH; ++i)
			System.out.print('-');
		System.out.println('\n');
		for(RegistrationInfo reg : presenceService.listRegisteredUsers())
			if(!regInfo.getUserName().equals(reg.getUserName()))
				System.console().printf(TWO_COLUMN_FORMAT, reg.getStatus() ? "available" : "busy", reg.getUserName());
		System.out.println();
	}
}