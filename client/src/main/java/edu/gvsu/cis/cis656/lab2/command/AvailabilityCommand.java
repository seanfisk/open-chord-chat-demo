package edu.gvsu.cis.cis656.lab2.command;

import java.rmi.RemoteException;

import edu.gvsu.cis.cis656.lab2.PresenceService;
import edu.gvsu.cis.cis656.lab2.RegistrationInfo;

public abstract class AvailabilityCommand extends Command
{
	public AvailabilityCommand(String name, String argFormat, String description, PresenceService presenceService, RegistrationInfo regInfo)
	{
		super(name, argFormat, description, presenceService, regInfo);
	}

	protected void setAvailability(boolean available) throws RemoteException
	{
		if(available == regInfo.getStatus())
			System.out.println("Note: You are already " + (available ? "available" : "busy") + ".");
		regInfo.setStatus(available);
		presenceService.updateRegistrationInfo(regInfo);
	}
}
