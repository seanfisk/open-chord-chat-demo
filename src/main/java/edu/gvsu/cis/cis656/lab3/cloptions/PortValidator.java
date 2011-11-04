package edu.gvsu.cis.cis656.lab3.cloptions;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class PortValidator implements IParameterValidator
{
	public void validate(String name, String value) throws ParameterException
	{
		int n = -1;
		try
		{
			n = Integer.parseInt(value);
		}
		catch(NumberFormatException e)
		{
			System.err.println("Invalid port number.");
			e.printStackTrace();
			System.exit(1);
		}
		if(n < 0 || n > 65535)
			throw new ParameterException("Parameter " + name + " should be a positive port number from 0-65535 (found " + value + ")");
	}
}
