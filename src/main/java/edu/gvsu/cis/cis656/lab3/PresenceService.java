package edu.gvsu.cis.cis656.lab3;

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
	 * @return Boolean indicating whether the registration was successful.
	 */
	boolean register(RegistrationInfo reg) throws ServiceException;

	/**
	 * Updates the information of a currently registered client.
	 * 
	 * @param reg
	 *            The updated registration info.
	 * @return true if successful, or false if no user with the given name is
	 *         registered.
	 */
	boolean updateRegistrationInfo(RegistrationInfo reg) throws ServiceException;

	/**
	 * Unregister a client from the presence service. Client must call this
	 * method when it terminates execution.
	 * 
	 * @param reg
	 *            The information about a client to be unregistered.
	 */
	void unregister(RegistrationInfo reg) throws ServiceException;

	/**
	 * Lookup the registration information of another client.
	 * 
	 * @name The name of the client that is to be located.
	 * @return The RegistrationInfo info for the client, or null if no such
	 *         client was found.
	 */
	RegistrationInfo lookup(String name) throws ServiceException;

	/**
	 * Leave the current Chord network. Client's must call this before they
	 * terminate in order to make sure the keys they maintain are passed to
	 * their successor in the Chord ring.
	 */
	void leave() throws ServiceException;
	
	/**
	 * Get the list of known friends at this time.
	 * 
	 * @return The list of know friends.
	 */
	Set<String> getKnownFriends();
	
	/**
	 * Set the list of known friends at this time.
	 * 
	 * @param knownFriends The new list of know friends.
	 */
	void setKnownFriends(Set<String> knownFriends);
}