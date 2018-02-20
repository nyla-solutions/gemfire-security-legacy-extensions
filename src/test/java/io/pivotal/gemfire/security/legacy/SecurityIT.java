package io.pivotal.gemfire.security.legacy;

import org.junit.Ignore;
import org.junit.Test;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.client.ClientRegionFactory;
import com.gemstone.gemfire.cache.client.ClientRegionShortcut;

@Ignore
public class SecurityIT {


	@Test
	public void authentication() {
		
		String username = "admin";
		String password = "secret";
		
		ClientCacheFactory factory = new ClientCacheFactory()
		.set("security-username", username)
		.set("security-password", password)
		.set("security-client-auth-init",CryptionPropertyAuthInitialize.class.getName()+".create")
		.addPoolLocator("Gregorys-MBP", 10334);
		
		ClientCache cache = factory.create();
		
		ClientRegionFactory<String, String> crf = cache.createClientRegionFactory(ClientRegionShortcut.PROXY);
		
		Region<String,String> region = crf.create("test");
		
		region.put("test", "test");
		
		cache.close();
		
	}
	
	@Test
	public void authenticationVALID() {
		
		String username = "VALID";
		String password = "VALID";
		
		ClientCacheFactory factory = new ClientCacheFactory()
		.set("security-username", username)
		.set("security-password", password)
		.set("security-client-auth-init",CryptionPropertyAuthInitialize.class.getName()+".create")
		.addPoolLocator("Gregorys-MBP", 10334);
		
		ClientCache cache = factory.create();
		
		ClientRegionFactory<String, String> crf = cache.createClientRegionFactory(ClientRegionShortcut.PROXY);
		
		Region<String,String> region = crf.create("test");
		
		region.put("test", "test");
		
		cache.close();
		
	}

}
