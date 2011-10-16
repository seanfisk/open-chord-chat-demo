package edu.gvsu.cis.cis656.lab2.command;

import java.rmi.RemoteException;

import edu.gvsu.cis.cis656.lab2.PresenceService;
import edu.gvsu.cis.cis656.lab2.RegistrationInfo;

public abstract class AvailabilityCommand extends Command
{
	public AvailabilityCommand(String name, String argFormat, String description, PresenceService presenceService, RegistrationInfo userInfo)
	{
		super(name, argFormat, description, presenceService, userInfo);
	}

	protected void setAvailability(boolean available) throws RemoteException
	{
		if(available == userInfo.getStatus())
			System.out.println("Note: You are already " + (available ? "available" : "busy") + ".");
		userInfo.setStatus(available);
		presenceService.updateRegistrationInfo(userInfo);
	}
}
