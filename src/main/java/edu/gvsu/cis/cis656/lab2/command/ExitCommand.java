package edu.gvsu.cis.cis656.lab2.command;

import de.uniba.wiai.lspi.chord.service.ServiceException;
import edu.gvsu.cis.cis656.lab2.PresenceService;
import edu.gvsu.cis.cis656.lab2.RegistrationInfo;

public class ExitCommand extends Command
{
	public ExitCommand(PresenceService presenceService, RegistrationInfo userInfo)
	{
		super("exit", null, "exit the chat client", presenceService, userInfo);
	}

	public void execute(String args) throws ServiceException
	{
		presenceService.unregister(userInfo);
	}
}