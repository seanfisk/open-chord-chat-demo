package edu.gvsu.cis.cis656.lab3.cloptions;

import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.internal.Lists;

public class CommandLineOptions
{
	@Parameter(description = "The host to which to connect")
	private List<String> parameters = Lists.newArrayList();

	@Parameter(names = "--master", description = "Make this client the master node and specify a port", validateWith = PortValidator.class)
	private int masterPort = -1;

	public List<String> getParameters()
	{
		return parameters;
	}

	public int getMasterPort()
	{
		return masterPort;
	}
}