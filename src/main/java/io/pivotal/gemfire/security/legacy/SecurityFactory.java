package io.pivotal.gemfire.security.legacy;

import com.gemstone.gemfire.security.AuthInitialize;
import com.gemstone.gemfire.security.Authenticator;

import io.pivotal.gemfire.security.legacy.ldap.LDAPAuthenticator;

/**
 * SecurityFactory is representable for the create authorization/authentication objects
 * @author Gregory Green
 *
 */
public class SecurityFactory
{
	private SecurityFactory()
	{
	}
	/**
	 * 
	 * @return new CryptionPropertyAuthInitialize()
	 */
	public static AuthInitialize createAuthInitialize()
	{
		//DO NOT USE spring
		return new CryptionPropertyAuthInitialize();
	}// ------------------------------------------------

	/**
	 * 
	 * @return new LDAPAuthenticator()
	 */
	public static Authenticator createAuthenticator()
	{
		return new LDAPAuthenticator();
	}// ------------------------------------------------
}
