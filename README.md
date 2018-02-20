# Overview


Pivotal GemFire 8.x provides a flexible framework for your security authentication plug-ins. This projects example implementations.

## Authentication


- For authenticator, you must implement the interface  com.gemstone.gemfire.security.Authenticator



## Authorization

- For authorization, the implementation interface is com.gemstone.gemfire.security.AccessControl


## Configuration

- Create implementation of com.gemstone.gemfire.security.AuthInitialize
- Use a public static method to return an instance of the class.


**Peer to Peer**

For peers and locators use the GemFire property *security-peer-authenticator* to a static create method that returns 
and implementation of the Authenticator interface.

	security-peer-auth-init=...


** Client Server **

For authorizing client credentials use the property *security-client-authenticator*
 
 Example
 
	security-client-authenticator=...

----------------------------------------


## Property File Authenticator Example

The package **io.pivotal.gemfire.security.legacy.properties** contain an implementation
of the authenticator based on storing user credentials in the properties.

**Sample configuration**


	security-client-auth-init=io.pivotal.gemfire.security.legacy.properties.PropertiesAuthenticator
	


The user/password can be stored a GemFire security properties file.
The formation is *security-users-*${USERNAME}=EncryptedorUnEncryptedPassword

The following example property is for a username *nyla*  unencrypted password *PurpleAnd#Pink*

	security-users-nyla=PurpleAnd#Pink

The following property is for a username *pjohn*  encrypted password
	
	security-users-pjohn={cryption}a8+kDY+shMmL2ZCOV+/njA==
	
	
** Runtime Setup **

YOU MUST SET the environment variable CRYPTION_KEY=

	export CRYPTION_KEY=test
	

*From gfsh*




	start locator --J=-Dgemfire.mcast-port=0 --name=locator --security-properties-file=/Projects/solutions/gedi/dev/legacy/gemfire-security-legacy-extensions/src/test/resources/property-ex-security-server.properties --classpath=/Projects/solutions/gedi/dev/legacy/gemfire-security-legacy-extensions/target/classes/:/Projects/solutions/gedi/dev/legacy/gemfire-security-legacy-extensions/lib/nyla.solutions.core-1.1.2.jar --connect=false


	start server  --J=-Dgemfire.mcast-port=0 --name=server1 --server-port=40404 --security-properties-file=/Projects/solutions/gedi/dev/legacy/gemfire-security-legacy-extensions/src/test/resources/property-ex-security-server.properties --classpath=/Projects/solutions/gedi/dev/legacy/gemfire-security-legacy-extensions/target/classes/:/Projects/solutions/gedi/dev/legacy/gemfire-security-legacy-extensions/lib/nyla.solutions.core-1.1.2.jar --locators=Gregorys-MBP[10334]


	create region --name=test --type=PARTITION
	
	


	
----------------------------------------

## LDAP Example

**Sample configuration**


	security-client-auth-init=io.pivotal.gemfire.security.legacy.LDAPAuthenticator.create
	security-basedn=ou=system
	security-ldapUrl=ldap://localhost:389
	
	security-proxyPassword=secret
	security-serviceAccountDn=uid=admin,ou=system


# Starting Cluster

From gfsh

	start locator --J=-Dgemfire.mcast-port=0 --name=locator --security-properties-file=/Projects/solutions/gedi/dev/legacy/gemfire-security-legacy-extensions/src/main/cfg/gfsecurity.properties --classpath=/Projects/solutions/gedi/dev/legacy/gemfire-security-legacy-extensions/target/classes/:/Projects/solutions/gedi/dev/legacy/gemfire-security-legacy-extensions/lib/nyla.solutions.core-1.1.2.jar --connect=false
	
	
	start server  --J=-Dgemfire.mcast-port=0 --name=server1 --server-port=40404 --security-properties-file=/Projects/solutions/gedi/dev/legacy/gemfire-security-legacy-extensions/src/main/cfg/gfsecurity.properties --classpath=/Projects/solutions/gedi/dev/legacy/gemfire-security-legacy-extensions/target/classes/:/Projects/solutions/gedi/dev/legacy/gemfire-security-legacy-extensions/lib/nyla.solutions.core-1.1.2.jar --locators=Gregorys-MBP[10334]
	
	
	create region --name=test --type=PARTITION