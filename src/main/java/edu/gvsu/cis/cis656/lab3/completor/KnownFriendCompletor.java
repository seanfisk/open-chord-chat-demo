package edu.gvsu.cis.cis656.lab3.completor;

import java.util.Collections;
import java.util.List;

import jline.Completor;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import edu.gvsu.cis.cis656.lab3.PresenceService;
import edu.gvsu.cis.cis656.lab3.RegistrationInfo;

public class KnownFriendCompletor implements Completor
{
	PresenceService presenceService;
	RegistrationInfo userInfo;

	public KnownFriendCompletor(PresenceService presenceService, RegistrationInfo userInfo)
	{
		this.presenceService = presenceService;
		this.userInfo = userInfo;
	}

	@SuppressWarnings("unchecked") @Override public int complete(String buffer, int cursor, @SuppressWarnings("rawtypes") List clist)
	{
		String start = (buffer == null) ? "" : buffer;
		try
		{
			for(String otherUserName : presenceService.getKnownFriends())
			{
				// only list them if they are available
				if(otherUserName.startsWith(start) && !userInfo.getUserName().equals(otherUserName))
				{
					// if these requirements are satisfied, check to see if they are available
					// if so, add them to the completions list
					RegistrationInfo otherUserInfo = presenceService.lookup(otherUserName);
					if(otherUserInfo.getStatus())
						clist.add(otherUserName);
				}
			}
		}
		catch(ServiceException e)
		{
			throw new RuntimeException("Could not retrieve information from the network", e);
		}
		Collections.sort(clist);

		// the rest is ripped from JLine's SimpleCompletor implementation

		// put a space after the completion if this is the only completion
		if(clist.size() == 1)
			clist.set(0, ((String) clist.get(0)) + " ");

		// the index of the completion is always from the beginning of
		// the buffer.
		return clist.size() == 0 ? -1 : 0;

	}

}