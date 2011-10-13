package edu.gvsu.cis.cis656.lab2;
/**
 * <p>Title: Lab1</p>
 * <p>Description: Old School Instant Messaging Application </p>
 * @author Jonathan Engelsma
 * @version 1.0
 */
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

/**
 * The abstract interface that is to implemented by a remote
 * presence server.  ChatClients will use this interface to
 * register themselves with the presence server, and also to
 * determine and locate other users who are available for chat
 * sessions.
 */
public interface PresenceService extends Remote {

    /**
     * Register a client with the presence service.
     * @param reg The information that is to be registered about a client.
     * @return true if the user was successfully registered, or false if somebody
     * the given name already exists in the system.
     */
    boolean register(RegistrationInfo reg) throws RemoteException;

    /**
     * Updates the information of a currently registered client. 
     * @param reg The updated registration info. 
     * @return true if successful, or false if no user with the given
     * name is registered.
     * 
     */
    boolean updateRegistrationInfo(RegistrationInfo reg) throws RemoteException;
    
    /**
     * Unregister a client from the presence service.  Client must call this
     * method when it terminates execution.
     * @param userName The name of the user to be unregistered.
     */
    void unregister(String userName) throws RemoteException;

    /**
     * Lookup the registration information of another client.
     * @name The name of the client that is to be located.
     * @return The RegistrationInfo info for the client, or null if
     * no such client was found.
     */
    RegistrationInfo lookup(String name) throws RemoteException;

    /**
     * Determine all users who are currently registered in the system.
     * @return An array of RegistrationInfo objects - one for each client
     * present in the system.
     */
    Vector<RegistrationInfo> listRegisteredUsers() throws RemoteException;
}