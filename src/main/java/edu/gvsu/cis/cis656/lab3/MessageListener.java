/**
 * 
 */
package edu.gvsu.cis.cis656.lab3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import jline.ConsoleReader;

/**
 * @author Sean Fisk <fiskse@mail.gvsu.edu>
 */
public class MessageListener implements Runnable
{
	private ServerSocket serverSocket;
	private ConsoleReader consoleReader;

	public MessageListener()
	{
		try
		{
			serverSocket = new ServerSocket(0);
		}
		catch(IOException e)
		{
			throw new RuntimeException("Error binding the listening socket.", e);
		}
	}

	public InetAddress getInetAddress()
	{
		return serverSocket.getInetAddress();
	}

	public int getLocalPort()
	{
		return serverSocket.getLocalPort();
	}

	public void close()
	{
		try
		{
			serverSocket.close();
		}
		catch(IOException e)
		{
			throw new RuntimeException("Error closing the server socket.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		try
		{
			while(true)
			{
				Socket sock = serverSocket.accept();
				BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				String line;
				while((line = reader.readLine()) != null)
				{
					consoleReader.printString('\n' + line + '\n');
					consoleReader.flushConsole();
					consoleReader.drawLine();
					consoleReader.flushConsole();
				}

				reader.close();
				sock.close();
			}
		}
		catch(SocketException e)
		{
			// socket was closed
			return;
		}
		catch(IOException e)
		{
			throw new RuntimeException("Error in receiving messages.", e);
		}
	}

	/**
	 * @return the consoleReader
	 */
	public ConsoleReader getConsoleReader()
	{
		return consoleReader;
	}

	/**
	 * @param consoleReader
	 *            the consoleReader to set
	 */
	public void setConsoleReader(ConsoleReader consoleReader)
	{
		this.consoleReader = consoleReader;
	}
}
