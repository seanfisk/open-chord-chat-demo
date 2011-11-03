package edu.gvsu.cis.cis656.lab2;

/**
 * <p>
 * Title: Lab2
 * </p>
 * <p>
 * Description: Registration record for the Chord Chat System
 * </p>
 * 
 * @author Jonathan Engelsma
 * @version 1.0
 */
import java.io.Serializable;

/**
 * This class represents the information that the chat client registers with the
 * presence server.
 */
public class RegistrationInfo implements Serializable
{
	private static final long serialVersionUID = 2144157610883535152L;
	private String userName;
	private String host;
	private boolean status;
	private int port;

	/**
	 * RegistrationInfo constructor.
	 * 
	 * @param uname
	 *            Name of the user being registered.
	 * @param h
	 *            Name of the host their client is running on.
	 * @param p
	 *            The port # their client is listening for connections on.
	 * @param s
	 *            The status, true if the client is available to host a game,
	 *            false otherwise.
	 */
	public RegistrationInfo(String uname, String h, int p, boolean s)
	{
		this.userName = uname;
		this.host = h;
		this.port = p;
		this.status = s;
	}

	/**
	 * Determine the name of the user.
	 * 
	 * @return The name of the user.
	 */
	public String getUserName()
	{
		return this.userName;
	}

	/**
	 * Determine the host the user is on.
	 * 
	 * @return The name of the host client resides on.
	 */
	public String getHost()
	{
		return this.host;
	}

	/**
	 * Get the port the client is listening for connections on.
	 * 
	 * @return port value.
	 */
	public int getPort()
	{
		return this.port;
	}

	/**
	 * Get the status of the client - true means availability, false means don't
	 * disturb.
	 * 
	 * @return status value.
	 */
	public boolean getStatus()
	{
		return this.status;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	/**
	 * @param host
	 *            the host to set
	 */
	public void setHost(String host)
	{
		this.host = host;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(boolean status)
	{
		this.status = status;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port)
	{
		this.port = port;
	}

	/**
	 * Chord requires stored data items to override the default hashCode()
	 * method. Leave this here or you will have problems! See OpenChord manual
	 * for details.
	 */
	public int hashCode()
	{
		return this.toString().intern().hashCode();
	}

	/**
	 * Chord requires stored data items to override the default equals() method.
	 * Leave this here or you will have problems! See OpenChord manual for
	 * details.
	 */
	public boolean equals(Object o)
	{
		if(o instanceof RegistrationInfo)
		{
			return ((RegistrationInfo) o).toString().equals(this.toString());
		}
		return false;
	}

	/**
	 * hashCode() and equals() use this. Don't touch it!
	 */
	public String toString()
	{
		return this.userName + "@" + this.host + ":" + this.port;
	}
}
