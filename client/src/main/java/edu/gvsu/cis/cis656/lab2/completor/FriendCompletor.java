package edu.gvsu.cis.cis656.lab2.completor;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;

import jline.Completor;
import edu.gvsu.cis.cis656.lab2.PresenceService;
import edu.gvsu.cis.cis656.lab2.RegistrationInfo;

public class FriendCompletor implements Completor
{
	PresenceService presenceService;

	public FriendCompletor(PresenceService presenceService)
	{
		this.presenceService = presenceService;
	}

	@SuppressWarnings("unchecked") @Override public int complete(String buffer, int cursor, @SuppressWarnings("rawtypes") List clist)
	{
		String start = (buffer == null) ? "" : buffer;
		try
		{
			for(RegistrationInfo reg : presenceService.listRegisteredUsers())
			{
				// only list them if they are available
				String userName = reg.getUserName();
				if(reg.getStatus() && userName.startsWith(start))
					clist.add(userName);
			}
		}
		catch(RemoteException e)
		{
			System.err.println("Could not retrieve the friends list.");
			e.printStackTrace();
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