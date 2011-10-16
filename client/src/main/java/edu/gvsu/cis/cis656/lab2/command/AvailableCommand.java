package edu.gvsu.cis.cis656.lab2.command;

import java.rmi.RemoteException;

import edu.gvsu.cis.cis656.lab2.PresenceService;
import edu.gvsu.cis.cis656.lab2.RegistrationInfo;

public class AvailableCommand extends AvailabilityCommand
{
	public AvailableCommand(PresenceService presenceService, RegistrationInfo userInfo)
	{
		super("available", null, "receive messages", presenceService, userInfo);
	}

	public void execute(String args) throws RemoteException
	{
		setAvailability(true);
	}
}