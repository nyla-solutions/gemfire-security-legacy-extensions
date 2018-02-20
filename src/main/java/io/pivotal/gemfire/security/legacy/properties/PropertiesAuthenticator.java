package io.pivotal.gemfire.security.legacy.properties;

import java.security.Principal;
import java.util.Map;
import java.util.Properties;

import com.gemstone.gemfire.LogWriter;
import com.gemstone.gemfire.distributed.DistributedMember;
import com.gemstone.gemfire.security.AuthenticationFailedException;
import com.gemstone.gemfire.security.Authenticator;

import io.pivotal.gemfire.security.legacy.SecurityConstants;
import io.pivotal.gemfire.security.legacy.User;
import nyla.solutions.core.util.Cryption;


/**
 * <pre>
 * Set system property
 * 
 * security-users.USERNAME=password
 * </pre> 
 * @author Gregory Green
 *
 */

public class PropertiesAuthenticator implements Authenticator
{		
	
	public static final String SECURITY_USERS_PREFIX_PROP = "security-users-";
	
	/**
	 * Default constructor
	 */
	public PropertiesAuthenticator()
	{}
	/**
	 *
	 *@param properties the security properties
	 */
	public PropertiesAuthenticator(Properties securityProperties)
	{
		this.userPasswordProperties = securityProperties;

	}// --------------------------------------------------------
	/**
	 * Authenticate a username/password request
	 * @param properties username/password an other security properties
	 * @param distributedMember the distributed member
	 */
	@Override
	public Principal authenticate(Properties properties, DistributedMember distributedMember) throws AuthenticationFailedException 
	{
		if(properties== null )
			throw new AuthenticationFailedException("properties required");
		
		
		if(properties.isEmpty() )
			throw new AuthenticationFailedException("NON empty properties required");
		
		
		String username = properties.getProperty(SecurityConstants.USERNAME_PROP);
		if(username== null || username.length() == 0)
			throw new AuthenticationFailedException(SecurityConstants.USERNAME_PROP+" property required");

		String password =  properties.getProperty(SecurityConstants.PASSWORD_PROP);
		if(password == null || password.length() == 0)
			throw new AuthenticationFailedException(SecurityConstants.PASSWORD_PROP+" property required");
	
		password = Cryption.interpret(password);
		
		String userLookupProperty = SECURITY_USERS_PREFIX_PROP.concat(username);
		
		String realPassword = this.userPasswordProperties.getProperty(userLookupProperty);
		
		if(realPassword == null )
			throw new AuthenticationFailedException("Username:"+username+" or password not found");
		
		if(!realPassword.equals(password))
			throw new AuthenticationFailedException("Username:"+username+" or password not found.");
			
		return new User(username, realPassword.getBytes(), null);
	}// --------------------------------------------------------

	@Override
	public void init(Properties securityProperties, LogWriter logger, LogWriter securityLogger) 
			throws AuthenticationFailedException {
		
		if(securityProperties == null || securityProperties.isEmpty() )
			throw new IllegalArgumentException("securityProperties  required");
		
	
		this.userPasswordProperties = new Properties();
		
		String key, value;
		try {
			for ( Map.Entry<Object, Object> entry : securityProperties.entrySet()) {
				
				key = (String)entry.getKey();
				
				if(!key.startsWith(SECURITY_USERS_PREFIX_PROP))
					continue;
				
				value = (String)entry.getValue();
				value = Cryption.interpret(value);
				
				userPasswordProperties.put(key, value);
				
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(),e);
		}
		
		
	}// --------------------------------------------------------
	
	/**
	 * @return the userPasswordProperties
	 */
	Properties getUserPasswordProperties() {
		return userPasswordProperties;
	}// --------------------------------------------------------
	
	public static Authenticator create()
	{
		return new PropertiesAuthenticator();
	}// --------------------------------------------------------
	@Override
	public void close() {
	}// --------------------------------------------------------
	private  Properties userPasswordProperties = null;
}
