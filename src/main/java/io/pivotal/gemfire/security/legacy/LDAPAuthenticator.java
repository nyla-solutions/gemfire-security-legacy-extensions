package io.pivotal.gemfire.security.legacy;

import java.security.Principal;
import java.util.Properties;

import javax.naming.NamingException;

import com.gemstone.gemfire.LogWriter;
import com.gemstone.gemfire.distributed.DistributedMember;
import com.gemstone.gemfire.security.AuthenticationFailedException;
import com.gemstone.gemfire.security.Authenticator;

import nyla.solutions.core.ds.LDAP;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.Cryption;

/**
 * <pre>
 * The Authenticator instance is called during the client cache connection for security.
 * 
 * 
 *  Sample configuration
 *  
 *  security-client-auth-init=io.pivotal.gemfire.security.legacy.LDAPAuthenticator.create
 *  security-basedn=ou=system
 *  security-ldapUrl=ldap://localhost:389
 *  security-proxyPassword=secret
 *  security-serviceAccountDn=uid=admin,ou=system
 *  </pre>
 * @author Gregory Green
 *
 */
public class LDAPAuthenticator implements Authenticator, SecurityConstants
{
	//@Override
	public void close()
	{

	}// ------------------------------------------------
	/**
	 * The Authenticator will construct the initial directory context. 
	 * The security credentials (username/password) will be provided to the context.
	 * @param properties the input login properties
	 * @param distributedMember the distribute member information 
	 * @return the security credentials
	 */
	//@Override
	public Principal authenticate(Properties properties, DistributedMember distributedMember)
			throws AuthenticationFailedException
	{
		if(properties == null)
			    	throw new AuthenticationFailedException("properties not provided");
		
		String userName = properties.getProperty(USERNAME_PROP);
	    if (userName == null) {
	      throw new AuthenticationFailedException(
	          LDAPAuthenticator.class.getName()+" user name property ["
	              + USERNAME_PROP + "] not provided");
	    }
	    String passwd = properties.getProperty(PASSWORD_PROP);
	    if (passwd == null || passwd.length() == 0) 
	    {
	      throw new AuthenticationFailedException(LDAPAuthenticator.class.getName()+" password property ["
	              + PASSWORD_PROP + "] not provided");
	    }
	    
	    try
	    {
		    //check if password prefixed with cryption
		    passwd = Cryption.interpret(passwd);	   
		    
		    
		    try (LDAP ldap = this.ldapConnectionFactory.connect(this.ldapUrl, this.serviceAccountDn,
		    		this.proxyPassword.toCharArray()))
		    		{		
		    			try
		    			{
		    				passwd = Cryption.interpret(passwd);
		    			}
		    			catch(Exception e)
		    			{
		    				//securityLogger.warn("Detected password interpration error. This may be caused by an incorrect password, but you should check that the CRYPTION_KEY environment variable is a minimum of 16 characters, then regenerate any needed passwords.");
		    				throw new AuthenticationFailedException(e.getMessage());
		    			
		    			}
		    		
		    			if (ldap == null)
		    				throw new IllegalArgumentException("ldap is required from factory: "+ldapConnectionFactory.getClass().getName());
		    			
		    			Principal principal = ldap.authenicate(userName, passwd.toCharArray(), this.basedn, uidAttribute, memberOfAttrNm,
		    			groupAttrNm, timeout);
		    			
		    			securityLogger.info("AUTHENTICATED:"+principal);
		    			
		    			return principal;
		    		}
		    		catch(AuthenticationFailedException e)
		    		{
		    			securityLogger.warning(e);
		    			throw e;
		    		}
		    		catch (NamingException |RuntimeException e)
		    		{
		    			securityLogger.warning(e);
		    			throw new AuthenticationFailedException(e.getMessage(),e);
		    		}
		 
	    }
	    catch (Exception e) 
	    {
	    	securityLogger.warning(e);
	      throw new AuthenticationFailedException(
	          "LdapUserAuthenticator: Failure with provided username, password "
	              + "combination for user name: " + userName+" ERROR:"+e.getMessage());
	    }
	    
	}// ------------------------------------------------

	//@Override
	public void init(Properties properties, LogWriter logger, LogWriter securityLogger)
			throws AuthenticationFailedException
	{
		this.logger = logger;
		
		if(this.logger == null )
			throw new IllegalArgumentException("this.logger required");
		
		this.securityLogger = securityLogger;
		if(this.securityLogger== null )
			throw new IllegalArgumentException("this.securityLogger required");
		
		this.basedn = properties.getProperty("security-basedn");
		if(this.basedn== null || this.basedn.length() == 0 )
			throw new IllegalArgumentException("security-basedn required");
		
		this.groupAttrNm = properties.getProperty("security-groupAttrNm");
		
		this.ldapUrl = properties.getProperty("security-ldapUrl");
		
		if(this.ldapUrl == null || this.ldapUrl.length() ==0)
			throw new IllegalArgumentException("security-ldapUrl  required");
		
		this.memberOfAttrNm  = properties.getProperty("security-memberOfAttrNm");
		
		this.proxyPassword = properties.getProperty("security-proxyPassword");
		
		if(this.proxyPassword == null || this.proxyPassword.length() == 0)
			throw new IllegalArgumentException("security-proxyPassword  required");
		
		this.serviceAccountDn = properties.getProperty("security-serviceAccountDn");
		if(this.serviceAccountDn== null )
			throw new IllegalArgumentException("security-serviceAccountDn required");
		
	}// ------------------------------------------------
	/**
	 * @return the mustEncryptPassword
	 */
	public boolean isMustEncryptPassword()
	{
		return mustEncryptPassword;
	}// ------------------------------------------------
	/**
	 * 
	 * @param mustEncryptPassword the mustEncryptPassword to set
	 */
	public void setMustEncryptPassword(boolean mustEncryptPassword)
	{
		this.mustEncryptPassword = mustEncryptPassword;
	}// ------------------------------------------------

	public static LDAPAuthenticator create()
	{
		return new LDAPAuthenticator();
	}
	private String proxyPassword ="";
	private String basedn;
	private String uidAttribute;
	private String memberOfAttrNm = "memberOf"; 
	private String groupAttrNm; //Ex: "CN";
	private String serviceAccountDn = null;
	private String ldapUrl;
	private LogWriter logger;
	private LogWriter securityLogger;
	
	private int timeout = Config.getPropertyInteger("LDAP_TIMEOUT", 10).intValue();
	private final LDAPConnectionFactory ldapConnectionFactory = new LDAPConnectionFactory();
	private boolean mustEncryptPassword = Config.getPropertyBoolean(LDAPAuthenticator.class,"mustEncryptPassword",false);
}
