/**
 * 
 */
package edu.gvsu.cis.cis656.lab3.command;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.UnknownHostException;
import java.util.Set;

import de.uniba.wiai.lspi.chord.service.ServiceException;
import edu.gvsu.cis.cis656.lab3.PresenceService;
import edu.gvsu.cis.cis656.lab3.RegistrationInfo;
import edu.gvsu.cis.cis656.lab3.util.PromptBuilder;

public abstract class TalkingCommand extends Command
{
	public TalkingCommand(String name, String argFormat, String description, PresenceService presenceService, RegistrationInfo userInfo)
	{
		super(name, argFormat, description, presenceService, userInfo);
	}

	protected void sendMessageToUser(String recipient, String message) throws ServiceException
	{
		RegistrationInfo recipInfo = presenceService.lookup(recipient);
		Set<String> knownFriends = presenceService.getKnownFriends();

		if(recipInfo == null)
		{
			System.out.println("User `" + recipient + "' isn't registered on this server.");
			knownFriends.remove(recipInfo);
			return;
		}

		if(!recipInfo.getStatus())
		{
			System.out.println("User `" + recipient + "' isn't available right now.");
			return;
		}

		try
		{
			Socket socket = new Socket(recipInfo.getHost(), recipInfo.getPort());
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			out.println(PromptBuilder.buildPrompt(userInfo) + message);
			out.close();
			socket.close();
		}
		catch(UnknownHostException e)
		{
			System.err.println("Unknown host.");
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		knownFriends.add(recipient);
	}

}
