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

The package **io.pivotal.gemfire.security.legacy.properties** contains an implementation
of the authenticator based on storing user credentials in the properties.


*Password Encryption*

YOU MUST SET the environment variable CRYPTION_KEY

Example

	export CRYPTION_KEY=test

Encrypted password can be generated using the nyla.solutions.core.util.Cryption tool.

See [https://github.com/nyla-solutions/nyla/tree/master/nyla.solutions.core](https://github.com/nyla-solutions/nyla/tree/master/nyla.solutions.core)


	java -classpath  lib/nyla.solutions.core-1.1.2.jar nyla.solutions.core.util.Cryption  YOUR-PASSWORD

	
Include the output with the prefix {cryption} in your property file



**Sample configuration**


	security-client-authenticator=io.pivotal.gemfire.security.legacy.properties.PropertiesAuthenticator
	


The user/password can be stored a GemFire security properties file.
The formation is *security-users-*${USERNAME}=EncryptedorUnEncryptedPassword

The following example property is for a username *nyla*  unencrypted password *PurpleAnd#Pink*

	security-users-nyla=PurpleAnd#Pink

The following property is for a username *pjohn*  encrypted password
	
	security-users-pjohn={cryption}a8+kDY+shMmL2ZCOV+/njA==


The following GemFire security property will enable Peer to peer
authenticator.

	security-peer-authenticator=io.pivotal.gemfire.security.legacy.properties.PropertiesAuthenticator.create


The following property will allow the security-username and security-password to be sent when the started member joins the cluster. This example code using the CryptionPropertyAuthInitialize object to initialize the 
security username/password sent to the server. It will encrypt the password
using nyla.solutions.core.util.Cryption. Note the environment variable *CRYPTION_KEY* must be set client to the same value on all members otherwise the passwords will not match.

 
	security-peer-auth-init=io.pivotal.gemfire.security.legacy.CryptionPropertyAuthInitialize.create

The following the cluster server side property that will authenticate the user as the "cluster" user.

	security-username=cluster
	security-password={cryption}g2BadPiglBcj1HECZzC6Qw==

	
**Runtime Setup**




*From gfsh (sample start)*


	start locator --J=-Dgemfire.mcast-port=0 --name=locator --security-properties-file=/Projects/solutions/gedi/dev/legacy/gemfire-security-legacy-extensions/src/test/resources/property-ex-security-server.properties --classpath=/Projects/solutions/gedi/dev/legacy/gemfire-security-legacy-extensions/target/classes/:/Projects/solutions/gedi/dev/legacy/gemfire-security-legacy-extensions/lib/nyla.solutions.core-1.1.2.jar --connect=false


	start server  --J=-Dgemfire.mcast-port=0 --name=server1 --server-port=40404 --security-properties-file=/Projects/solutions/gedi/dev/legacy/gemfire-security-legacy-extensions/src/test/resources/property-ex-security-server.properties --classpath=/Projects/solutions/gedi/dev/legacy/gemfire-security-legacy-extensions/target/classes/:/Projects/solutions/gedi/dev/legacy/gemfire-security-legacy-extensions/lib/nyla.solutions.core-1.1.2.jar --locators=Gregorys-MBP[10334]


	create region --name=test --type=PARTITION

**Client Test**


The following is sample code to establish a connection with a username/password. This example code using the CryptionPropertyAuthInitialize object to initialize the 
security username/password sent to the server. It will encrypt the password
using nyla.solutions.core.util.Cryption. Note the environment variable *CRYPTION_KEY* must be set client to the same value on the server otherwise the passwords will not match. 


	ClientCacheFactory factory = new ClientCacheFactory()
					.set("security-username", username)
					.set("security-password", password)
					.set("security-client-auth-init", "io.pivotal.gemfire.security.legacy.CryptionPropertyAuthInitialize.create")
					.addPoolLocator(locatorHost, locatorPort);
	
	ClientCache cache = factory.create();
	....
	
	
----------------------------------------

## LDAP Example


The package **io.pivotal.gemfire.security.legacy.ldap** contains an implementation
of the authenticator based on authenticating against LDAP.

**Sample configuration**


	security-client-auth-init=io.pivotal.gemfire.security.legacy.ldap.LDAPAuthenticator.create
	security-basedn=ou=system
	security-ldapUrl=ldap://localhost:389
	
	security-proxyPassword=secret
	security-serviceAccountDn=uid=admin,ou=system


# Starting Cluster

From gfsh (sample start)

	start locator --J=-Dgemfire.mcast-port=0 --name=locator --security-properties-file=/Projects/solutions/gedi/dev/legacy/gemfire-security-legacy-extensions/src/main/cfg/gfsecurity.properties --classpath=/Projects/solutions/gedi/dev/legacy/gemfire-security-legacy-extensions/target/classes/:/Projects/solutions/gedi/dev/legacy/gemfire-security-legacy-extensions/lib/nyla.solutions.core-1.1.2.jar --connect=false
	
	
	start server  --J=-Dgemfire.mcast-port=0 --name=server1 --server-port=40404 --security-properties-file=/Projects/solutions/gedi/dev/legacy/gemfire-security-legacy-extensions/src/main/cfg/gfsecurity.properties --classpath=/Projects/solutions/gedi/dev/legacy/gemfire-security-legacy-extensions/target/classes/:/Projects/solutions/gedi/dev/legacy/gemfire-security-legacy-extensions/lib/nyla.solutions.core-1.1.2.jar --locators=Gregorys-MBP[10334]
	
	
	create region --name=test --type=PARTITION