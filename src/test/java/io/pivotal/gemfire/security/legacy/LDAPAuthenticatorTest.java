package io.pivotal.gemfire.security.legacy;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Properties;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.gemstone.gemfire.LogWriter;
import com.gemstone.gemfire.distributed.DistributedMember;
import com.gemstone.gemfire.security.AuthenticationFailedException;

import io.pivotal.gemfire.security.legacy.ldap.LDAPAuthenticator;


public class LDAPAuthenticatorTest {

	@Test
	public void test() throws Exception
	{
		LDAPAuthenticator authenticator = LDAPAuthenticator.create();
		
		Properties properties = null;
		DistributedMember distributedMember = null;
		
		try
		{
			authenticator.authenticate(properties, distributedMember);
			fail();
		}
		catch(AuthenticationFailedException e)
		{}
		
		
		init(authenticator);
		
		
		properties = new Properties();
		properties.setProperty(SecurityConstants.USERNAME_PROP, "cluster");
		properties.setProperty(SecurityConstants.PASSWORD_PROP, "cluster");
		
		Principal principal = authenticator.authenticate(properties, distributedMember);
		
		assertNotNull(principal);
		
		
	}
	
	private void init(LDAPAuthenticator authenticator) throws FileNotFoundException, IOException
	{
		Properties properties = new Properties();
		properties.load(new FileReader(Paths.get("src/main/cfg/gfsecurity.properties").toFile()));
		
		LogWriter logger = Mockito.mock(LogWriter.class);
		LogWriter securityLogger = Mockito.mock(LogWriter.class);
		authenticator.init(properties, logger, securityLogger);
	}

}
