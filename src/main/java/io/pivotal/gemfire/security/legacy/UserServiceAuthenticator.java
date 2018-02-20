package io.pivotal.gemfire.security.legacy;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Properties;

import com.gemstone.gemfire.LogWriter;
import com.gemstone.gemfire.distributed.DistributedMember;
import com.gemstone.gemfire.security.AuthenticationFailedException;
import com.gemstone.gemfire.security.Authenticator;

import nyla.solutions.core.util.Cryption;
import nyla.solutions.core.util.Debugger;


/**
 * @author Gregory Green
 *
 */
public class UserServiceAuthenticator implements Authenticator
{
	
	public static final String USERNAME_PROP = "security-username";
	public static final String PASSWORD_PROP = "security-password";
	
	public UserServiceAuthenticator()
	{
		this(new ConfiguredUserCacheLoader());
	}
	
	public UserServiceAuthenticator(UserService userService)
	{		
		this.userService = userService;
	}
	
	@Override
	public Principal authenticate(Properties credentials, DistributedMember distributedMember)
			throws com.gemstone.gemfire.security.AuthenticationFailedException 
	{

		if(credentials == null )
			throw new AuthenticationFailedException("null properties, properties required");
		
		this.securityLogger.info("AUDIT authenticate:"+credentials.getProperty("security-username"));
			
		String userName  = null;
			userName = credentials.getProperty(USERNAME_PROP);	
			if (userName == null || userName.length() == 0){
				throw new AuthenticationFailedException(USERNAME_PROP+" required");
			}
			
			String password = credentials.getProperty(PASSWORD_PROP);
			
			
			if (password == null)
				throw new AuthenticationFailedException(PASSWORD_PROP+" required");
			
			User user = this.userService.findUser(userName);
			
			if(user == null)
				throw new AuthenticationFailedException("user \""+userName+"\" not found");
	
			byte[] userEncryptedPasswordBytes = user.getEncryptedPassword();
			
			if(userEncryptedPasswordBytes == null || userEncryptedPasswordBytes.length == 0)
				throw new AuthenticationFailedException("password is required");
			
			String userEncryptedPassword =  new String(userEncryptedPasswordBytes,StandardCharsets.UTF_8);
			try
			{
				
				String storedUnEncrypted = null;
				
				try
				{
					//compare password
					storedUnEncrypted = cryption.decryptText(userEncryptedPassword);
				}
				catch (NumberFormatException e)
				{
					throw new AuthenticationFailedException("Stored password Invalid p:"+userEncryptedPassword+" STACK:"+Debugger.stackTrace(e));
				}
				
				//test without encrypt
				if(storedUnEncrypted.equals(password))
					return user;
				
				int indexOfCryption = password.indexOf(Cryption.CRYPTION_PREFIX);
				if(indexOfCryption > -1)
					password = password.substring(indexOfCryption+Cryption.CRYPTION_PREFIX.length());
				
				String unencryptedPassword = null;
				try
				{
					unencryptedPassword = cryption.decryptText(password);
				}
				catch(NumberFormatException e)
				{
					unencryptedPassword = password;
				}
				
				if(!unencryptedPassword.equals(storedUnEncrypted))
					throw new AuthenticationFailedException("Password user or password not found");
			}

			catch (Exception e)
			{
				throw new AuthenticationFailedException(e.getMessage(),e);
			}
			
			return user;
			
	}//------------------------------------------------
    
    //@Override
  	public void init(Properties properties, LogWriter logger, LogWriter securityLogger)
  			throws AuthenticationFailedException
  	{
  		cryption = new Cryption();
  		
  		this.logger = logger;
  		
  		if(this.logger == null )
  			throw new IllegalArgumentException("this.logger required");
  		
  		this.securityLogger = securityLogger;
  		if(this.securityLogger== null )
  			throw new IllegalArgumentException("this.securityLogger required");
  	}
  	
  	public static Authenticator create()
  	{
  		return new UserServiceAuthenticator();
  		
  	}//--------------------------------------------
  	
  	@Override
  	public void close() {	
  	}
  	
  	private Cryption cryption;
	private LogWriter logger;
	private LogWriter securityLogger;
    private final UserService userService;

}
