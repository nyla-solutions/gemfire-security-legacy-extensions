package io.pivotal.gemfire.security.legacy.properties;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.Principal;
import java.util.Properties;

import org.junit.Test;
import org.mockito.Mockito;

import com.gemstone.gemfire.LogWriter;
import com.gemstone.gemfire.security.AuthenticationFailedException;

import io.pivotal.gemfire.security.legacy.SecurityConstants;

public class PropertiesAuthenticatorTest {

	@Test
	public void testAuthenticate() throws Exception 
	{
		
		
		PropertiesAuthenticator authenticator = createAuthenticator();
		
		assertNotNull(authenticator);
		
		try
		{
			authenticator.authenticate(null, null);
			fail();
		}
		catch(AuthenticationFailedException e){}
		
		Properties props = new Properties();
		
		try
		{
			authenticator.authenticate(props, null);
			fail();
		}
		catch(AuthenticationFailedException e){}
		
		props.setProperty(SecurityConstants.USERNAME_PROP, "invalid");
		try
		{
			authenticator.authenticate(props, null);
			fail();
		}
		catch(AuthenticationFailedException e){}
		
		props.setProperty(SecurityConstants.PASSWORD_PROP, "invalid");
		
		try
		{
			authenticator.authenticate(props, null);
			fail();
		}
		catch(AuthenticationFailedException e){}
		
		
		props.setProperty(SecurityConstants.PASSWORD_PROP, "");
		try
		{
			authenticator.authenticate(props, null);
			fail();
		}
		catch(AuthenticationFailedException e){}
		
		props.setProperty(SecurityConstants.PASSWORD_PROP, "valid");
		
		try
		{
			authenticator.authenticate(props, null);
			fail();
		}
		catch(AuthenticationFailedException e){}
		
		props.setProperty(SecurityConstants.USERNAME_PROP, "valid");
		
		Principal principal = authenticator.authenticate(props, null);
		
		assertNotNull(principal);
		
		assertEquals(props.get(SecurityConstants.USERNAME_PROP), principal.getName());
		
	}// --------------------------------------------------------
	
	@Test
	public void testEncryptedPasswords() throws Exception 
	{		
		
			PropertiesAuthenticator authenticator = createAuthenticator();
			
			Properties props = new Properties();
			props.setProperty(SecurityConstants.USERNAME_PROP, "unencrypted");
			props.setProperty(SecurityConstants.PASSWORD_PROP, "password");
			
			Principal principal = authenticator.authenticate(props, null);
			
			assertNotNull(principal);
			assertEquals(principal.getName(), props.getProperty(SecurityConstants.USERNAME_PROP));
			
			
			props.setProperty(SecurityConstants.USERNAME_PROP, "encrypted");
			props.setProperty(SecurityConstants.PASSWORD_PROP, "password");
			
			principal = authenticator.authenticate(props, null);
			assertNotNull(principal);
			assertEquals(principal.getName(), props.getProperty(SecurityConstants.USERNAME_PROP));
			
			
			
			props.setProperty(SecurityConstants.USERNAME_PROP, "encrypted");
			props.setProperty(SecurityConstants.PASSWORD_PROP, "{cryption}a8+kDY+shMmL2ZCOV+/njA==");
			
			principal = authenticator.authenticate(props, null);
			assertNotNull(principal);
			assertEquals(principal.getName(), props.getProperty(SecurityConstants.USERNAME_PROP));
			
	}// --------------------------------------------------------

	private PropertiesAuthenticator createAuthenticator() throws IOException, FileNotFoundException {
		Properties securityProperties = new Properties();
		securityProperties.load(new FileReader(new File("src/test/resources/property-ex-security-server.properties")));
		LogWriter logger = Mockito.mock(LogWriter.class);
		LogWriter securityLogger = Mockito.mock(LogWriter.class);
		PropertiesAuthenticator authenticator = (PropertiesAuthenticator)PropertiesAuthenticator.create();
		authenticator.init(securityProperties, logger, securityLogger);
		
		Properties usersProp = authenticator.getUserPasswordProperties();
		
		assertNotNull(usersProp);
		assertTrue(usersProp.size()>=2);
		
		return authenticator;
	}

}
