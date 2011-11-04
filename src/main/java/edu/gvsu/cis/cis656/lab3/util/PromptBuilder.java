package edu.gvsu.cis.cis656.lab3.util;

import edu.gvsu.cis.cis656.lab3.RegistrationInfo;

public class PromptBuilder
{
	public static String buildPrompt(RegistrationInfo userInfo)
	{
		return userInfo.getUserName() + ':' + (userInfo.getStatus() ? "available" : "busy") + "> ";
	}
}
