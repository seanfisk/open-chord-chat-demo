/**
 * 
 */
package edu.gvsu.cis.cis656.lab2;

import java.io.Serializable;
import java.net.MalformedURLException;
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

	public PresenceServiceImpl(boolean master, String host, int port)
	{
		super();

		// url setups
		PropertiesLoader.loadPropertyFile();
		String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
		URL localURL = null;
		try
		{
			localURL = new URL(protocol + "://localhost:8080/");
		}
		catch(MalformedURLException e)
		{
			throw new RuntimeException(e);
		}

		// create Chord network
		if(master)
		{
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
		// join an existing Chord network
		else
		{
			URL bootstrapURL = null;
			try
			{
				bootstrapURL = new URL(protocol + "://" + host + ':' + port);
			}
			catch(MalformedURLException e)
			{
				throw new RuntimeException(e);
			}
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

	@Override public void register(RegistrationInfo userInfo) throws ServiceException
	{
		chord.insert(new StringKey(userInfo.getUserName()), userInfo);
	}

	@Override public void unregister(RegistrationInfo userInfo) throws ServiceException
	{
		chord.remove(new StringKey(userInfo.getUserName()), userInfo);
	}

	@Override public Set<Serializable> lookup(String name) throws ServiceException
	{
		return chord.retrieve(new StringKey(name));
	}

	@Override public void leave() throws ServiceException
	{
		chord.leave();
	}
}
