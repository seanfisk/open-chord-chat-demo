package edu.gvsu.cis.cis656.lab3.command;

import de.uniba.wiai.lspi.chord.service.ServiceException;
import edu.gvsu.cis.cis656.lab3.PresenceService;
import edu.gvsu.cis.cis656.lab3.RegistrationInfo;

public class BusyCommand extends AvailabilityCommand
{
	public BusyCommand(PresenceService presenceService, RegistrationInfo userInfo)
	{
		super("busy", null, "do not receive messages", presenceService, userInfo);
	}

	public void execute(String args) throws ServiceException
	{
		setAvailability(false);
	}
}