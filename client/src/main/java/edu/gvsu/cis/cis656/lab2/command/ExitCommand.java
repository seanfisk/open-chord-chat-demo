package edu.gvsu.cis.cis656.lab2.command;

import java.rmi.RemoteException;

import edu.gvsu.cis.cis656.lab2.PresenceService;
import edu.gvsu.cis.cis656.lab2.RegistrationInfo;

public class ExitCommand extends Command
{
	public ExitCommand(PresenceService presenceService, RegistrationInfo regInfo)
	{
		super("exit", null, "exit the chat client", presenceService, regInfo);
	}

	public void execute(String args) throws RemoteException
	{
		presenceService.unregister(regInfo.getUserName());
	}
}