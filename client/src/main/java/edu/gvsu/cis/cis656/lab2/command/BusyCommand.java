package edu.gvsu.cis.cis656.lab2.command;

import java.rmi.RemoteException;

import edu.gvsu.cis.cis656.lab2.PresenceService;
import edu.gvsu.cis.cis656.lab2.RegistrationInfo;

public class BusyCommand extends AvailabilityCommand
{
	public BusyCommand(PresenceService presenceService, RegistrationInfo regInfo)
	{
		super("busy", null, "do not receive messages", presenceService, regInfo);
	}

	public void execute(String args) throws RemoteException
	{
		setAvailability(false);
	}
}