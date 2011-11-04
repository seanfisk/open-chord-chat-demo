/**
 * 
 */
package edu.gvsu.cis.cis656.lab2;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Set;

import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

/**
 * @author Sean Fisk <fiskse@mail.gvsu.edu>
 */
public class PresenceServiceImpl implements PresenceService
{
	private Chord chord;

	public PresenceServiceImpl(boolean isMaster, String host, int port)
	{
		super();

		// load properties
		PropertiesLoader.loadPropertyFile();

		if(isMaster)
			createNetwork(port);
		else
			joinNetwork(host, port);
	}

	@Override
	public boolean register(RegistrationInfo userInfo) throws ServiceException
	{
		StringKey key = new StringKey(userInfo.getUserName());

		// only allow unique usernames
		if(!chord.retrieve(key).isEmpty())
			return false;

		chord.insert(key, userInfo);
		return true;
	}

	@Override
	public boolean updateRegistrationInfo(RegistrationInfo userInfo) throws ServiceException
	{
		StringKey key = new StringKey(userInfo.getUserName());
		Set<Serializable> valueSet = chord.retrieve(key);
		if(valueSet.isEmpty())
			return false;

		// we are assuming only one value per key, since we restrict this in
		// register()
		chord.remove(key, (Serializable) valueSet.toArray()[0]);
		chord.insert(key, userInfo);
		return true;
	}

	@Override
	public void unregister(RegistrationInfo userInfo) throws ServiceException
	{
		// we are assuming only one value per key, since we restrict this in
		// register()
		chord.remove(new StringKey(userInfo.getUserName()), userInfo);
	}

	@Override
	public RegistrationInfo lookup(String name) throws ServiceException
	{
		// we are assuming only one value per key, since we restrict this in
		// register()
		Set<Serializable> valueSet = chord.retrieve(new StringKey(name));
		if(valueSet.isEmpty())
			return null;
		return (RegistrationInfo) valueSet.toArray()[0];
	}

	@Override
	public void leave() throws ServiceException
	{
		chord.leave();
	}

	private void createNetwork(int port)
	{
		// create a new Chord network
		String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);

		// local url
		String localURLStr;
		URL localURL = null;
		try
		{
			localURLStr = protocol + "://" + InetAddress.getLocalHost().getHostAddress() + ':' + port + '/';
		}
		catch(UnknownHostException e)
		{
			throw new RuntimeException(e);
		}
		try
		{
			localURL = new URL(localURLStr);
		}
		catch(MalformedURLException e)
		{
			throw new RuntimeException("Malformed URL: " + localURLStr, e);
		}

		// actually create the network
		chord = new ChordImpl();
		try
		{
			chord.create(localURL);
		}
		catch(ServiceException e)
		{
			throw new RuntimeException(" Could not create DHT ! ", e);
		}
	}

	private void joinNetwork(String bootstrapHost, int bootstrapPort)
	{
		// join an existing Chord network
		String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);

		// find a random port to which to bind
		// we must do this otherwise multiple clients on the same machine (often
		// used during testing) will collide
		int localPort;
		try
		{
			ServerSocket server = new ServerSocket(0);
			localPort = server.getLocalPort();
			server.close();
		}
		catch(IOException e)
		{
			throw new RuntimeException("Error while trying to find a free port", e);
		}

		// local url
		String localURLStr;
		URL localURL = null;
		try
		{
			localURLStr = protocol + "://" + InetAddress.getLocalHost().getHostAddress() + ':' + localPort + '/';
		}
		catch(UnknownHostException e)
		{
			throw new RuntimeException(e);
		}
		try
		{
			localURL = new URL(localURLStr);
		}
		catch(MalformedURLException e)
		{
			throw new RuntimeException("Malformed URL: " + localURLStr, e);
		}

		// bootstrap url
		String bootstrapURLStr = protocol + "://" + bootstrapHost + ':' + bootstrapPort + '/';
		URL bootstrapURL = null;
		try
		{
			bootstrapURL = new URL(bootstrapURLStr);
		}
		catch(MalformedURLException e)
		{
			throw new RuntimeException("Malformed URL: " + bootstrapURLStr, e);
		}

		// actually join the network
		chord = new ChordImpl();
		try
		{
			chord.join(localURL, bootstrapURL);
		}
		catch(ServiceException e)
		{
			throw new RuntimeException("Could not join DHT!", e);
		}
	}
}
