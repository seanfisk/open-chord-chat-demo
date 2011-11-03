package edu.gvsu.cis.cis656.lab2;

/**
 * <p>
 * Title: Lab2
 * </p>
 * <p>
 * Description: Simple Chat Interface for OpenChord
 * </p>
 * 
 * @author Jonathan Engelsma
 * @version 1.0
 */
import java.io.Serializable;
import java.util.Set;

import de.uniba.wiai.lspi.chord.service.ServiceException;

/**
 * The abstract interface that is to implemented by a remote presence server.
 * ChatClients will use this interface to register themselves with the presence
 * server, and also to determine and locate other users who are available for
 * chat sessions.
 */
public interface PresenceService
{
	/**
	 * Register a client with the presence service.
	 * 
	 * @param reg
	 *            The information that is to be registered about a client.
	 */
	void register(RegistrationInfo userInfo) throws ServiceException;

	/**
	 * Unregister a client from the presence service. Client must call this
	 * method when it terminates execution.
	 * 
	 * @param userName
	 *            The name of the user to be unregistered.
	 */
	void unregister(RegistrationInfo userInfo) throws ServiceException;

	/**
	 * Lookup the registration information of another client.
	 * 
	 * @name The name of the client that is to be located.
	 * @return The RegistrationInfo info for the client, or null if no such
	 *         client was found.
	 */
	Set<Serializable> lookup(String name) throws ServiceException;

	/**
	 * Leave the current Chord network. Client's must call this before they
	 * terminate in order to make sure the keys they maintain are passed to
	 * their successor in the Chord ring.
	 */
	void leave() throws ServiceException;
}
