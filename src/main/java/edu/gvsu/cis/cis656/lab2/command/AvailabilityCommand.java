package edu.gvsu.cis.cis656.lab2.command;

import de.uniba.wiai.lspi.chord.service.ServiceException;
import edu.gvsu.cis.cis656.lab2.PresenceService;
import edu.gvsu.cis.cis656.lab2.RegistrationInfo;

public abstract class AvailabilityCommand extends Command
{
	public AvailabilityCommand(String name, String argFormat, String description, PresenceService presenceService, RegistrationInfo userInfo)
	{
		super(name, argFormat, description, presenceService, userInfo);
	}

	protected void setAvailability(boolean available) throws ServiceException
	{
		if(available == userInfo.getStatus())
		{
			System.out.println("Note: You are already " + (available ? "available" : "busy") + ".");
			return;
		}
		presenceService.unregister(userInfo);
		userInfo.setStatus(available);
		presenceService.register(userInfo);
	}
}
