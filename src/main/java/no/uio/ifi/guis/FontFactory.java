package no.uio.ifi.guis;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;

public class FontFactory {
	public static void changeFont ( Component component, Font font )
	{
	    component.setFont ( font );
	    if ( component instanceof Container )
	    {
	        for ( Component child : ( ( Container ) component ).getComponents () )
	        {
	            changeFont ( child, font );
	        }
	    }
	}
}
