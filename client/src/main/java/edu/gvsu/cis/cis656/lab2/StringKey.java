package edu.gvsu.cis.cis656.lab2;

/**
 * <p>Title: Lab2</p>
 * <p>Description: Chord Key for strings </p>
 * @author Jonathan Engelsma
 * @version 1.0
 */
import de.uniba.wiai.lspi.chord.service.Key;

/**
 * Implements a simple Chord Key for Strings.  See sample code or OpenChord manual for more details.
 */
public class StringKey implements Key {

	String theString ;
	
	public StringKey ( String theString )
	{		
		this.theString = theString ;
	}
	
	public byte [] getBytes (){
		return this.theString.getBytes();
	}
	
	public int hashCode (){
		return this.theString.hashCode();
	}
	
	public boolean equals ( Object o){
		if (o instanceof StringKey )
		{
			return (( StringKey)o).theString.equals(this.theString );
		}
		return false;
	}
}
