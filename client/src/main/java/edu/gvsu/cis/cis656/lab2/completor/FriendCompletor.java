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
	RegistrationInfo userInfo;

	public FriendCompletor(PresenceService presenceService, RegistrationInfo userInfo)
	{
		this.presenceService = presenceService;
		this.userInfo = userInfo;
	}

	@SuppressWarnings("unchecked") @Override public int complete(String buffer, int cursor, @SuppressWarnings("rawtypes") List clist)
	{
		String start = (buffer == null) ? "" : buffer;
		try
		{
			for(RegistrationInfo otherUserInfo : presenceService.listRegisteredUsers())
			{
				// only list them if they are available
				String userName = otherUserInfo.getUserName();
				if(otherUserInfo.getStatus() &&
						userName.startsWith(start) &&
						!userInfo.getUserName().equals(otherUserInfo.getUserName()))
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