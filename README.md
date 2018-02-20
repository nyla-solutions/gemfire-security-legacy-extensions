# Overview

# Authentication

Pivotal GemFire 8.x provides a flexible framework for your security authentication plug-ins. 


- Create implementation of com.gemstone.gemfire.security.AuthInitialize
- Use a public static method to return an instance of the class.

**Peer to Peer**

For peers and locators use the property *security-peer-authenticator*

	security-peer-auth-init=...


** Client Server **

For authorizing client credentials  use the property *security-client-authenticator*
 
 Example
 
	security-client-authenticator=...




## Property File Authenticator Example

The package 

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