/**
 * 
 */
package edu.gvsu.cis.cis656.lab2.command;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.UnknownHostException;

import edu.gvsu.cis.cis656.lab2.PresenceService;
import edu.gvsu.cis.cis656.lab2.RegistrationInfo;

public abstract class TalkingCommand extends Command
{
	public TalkingCommand(String name, String argFormat, String description, PresenceService presenceService, RegistrationInfo regInfo)
	{
		super(name, argFormat, description, presenceService, regInfo);
	}

	protected void sendMessageToUser(String recipient, String message) throws RemoteException
	{
		RegistrationInfo reg = presenceService.lookup(recipient);

		if(reg == null)
		{
			System.out.println("User `" + recipient + "' isn't registered on this server.");
			return;
		}

		if(!reg.getStatus())
		{
			System.out.println("User `" + recipient + "' isn't available right now.");
			return;
		}

		try
		{
			Socket socket = new Socket(reg.getHost(), reg.getPort());
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			out.println(regInfo.getUserName() + "> " + message);
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
	}

}
