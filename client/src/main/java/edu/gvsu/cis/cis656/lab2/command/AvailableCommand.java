package edu.gvsu.cis.cis656.lab2.command;

import de.uniba.wiai.lspi.chord.service.ServiceException;
import edu.gvsu.cis.cis656.lab2.PresenceService;
import edu.gvsu.cis.cis656.lab2.RegistrationInfo;

public class AvailableCommand extends AvailabilityCommand
{
	public AvailableCommand(PresenceService presenceService, RegistrationInfo userInfo)
	{
		super("available", null, "receive messages", presenceService, userInfo);
	}

	public void execute(String args) throws ServiceException
	{
		setAvailability(true);
	}
}