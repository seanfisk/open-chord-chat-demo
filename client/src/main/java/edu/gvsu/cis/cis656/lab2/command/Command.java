package edu.gvsu.cis.cis656.lab2.command;

import java.rmi.RemoteException;

import edu.gvsu.cis.cis656.lab2.PresenceService;
import edu.gvsu.cis.cis656.lab2.RegistrationInfo;

// commands
public abstract class Command
{
	protected String name;
	protected String argFormat;
	protected String description;
	protected PresenceService presenceService;
	protected RegistrationInfo userInfo;

	public Command(String name, String argFormat, String description, PresenceService presenceService, RegistrationInfo userInfo)
	{
		this.name = name;
		this.argFormat = argFormat;
		this.description = description;
		this.presenceService = presenceService;
		this.userInfo = userInfo;
	}

	public abstract void execute(String args) throws RemoteException;

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the argFormat
	 */
	public String getArgFormat()
	{
		return argFormat;
	}

	/**
	 * @param argFormat
	 *            the argFormat to set
	 */
	public void setArgFormat(String argFormat)
	{
		this.argFormat = argFormat;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * @return the presenceService
	 */
	public PresenceService getPresenceService()
	{
		return presenceService;
	}

	/**
	 * @param presenceService
	 *            the presenceService to set
	 */
	public void setPresenceService(PresenceService presenceService)
	{
		this.presenceService = presenceService;
	}

	/**
	 * @return the regInfo
	 */
	public RegistrationInfo getRegInfo()
	{
		return userInfo;
	}

	/**
	 * @param regInfo
	 *            the regInfo to set
	 */
	public void setRegInfo(RegistrationInfo regInfo)
	{
		this.userInfo = regInfo;
	}

	public String toString()
	{
		return name + " - " + description;
	}
}