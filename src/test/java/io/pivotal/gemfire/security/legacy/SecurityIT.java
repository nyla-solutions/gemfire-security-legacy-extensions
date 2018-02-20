package io.pivotal.gemfire.security.legacy;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.RegionExistsException;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.client.ClientRegionFactory;
import com.gemstone.gemfire.cache.client.ClientRegionShortcut;
import com.gemstone.gemfire.cache.client.ServerOperationException;

@Ignore
public class SecurityIT {

	@Test
	public void authentication() {

		String locatorHost = "Gregorys-MBP";
		int locatorPort = 10334;
		
		ClientCache cache  = null;
		Region<String, String> region = null;
		
		String username = "admin";
		String password = "secret";

		try
		{
			ClientCacheFactory factory = new ClientCacheFactory()
					.set("security-username", username)
					.set("security-password", password)
					.set("security-client-auth-init", "io.pivotal.gemfire.security.legacy.CryptionPropertyAuthInitialize.create")
					.addPoolLocator(locatorHost, locatorPort);

			cache = factory.create();

			ClientRegionFactory<String, String> crf = cache.createClientRegionFactory(ClientRegionShortcut.PROXY);

			region = crf.create("test");

			region.put("test", "test");
		}
		finally
		{
			try {
				if (region != null)
					region.close();
			} catch (Exception e) {
			}

			try {
				if (cache != null)
					cache.close();
			} catch (Exception e) {
			}
		}

	}
	
	@Test
	public void authenticationStoredEncryptedPassword() {

		
		ClientCache cache  = null;
		Region<String, String> region = null;
		
		String username = "encrypted";
		String password = "password";

		try
		{
			ClientCacheFactory factory = new ClientCacheFactory().set("security-username", username)
					.set("security-password", password)
					.set("security-client-auth-init", CryptionPropertyAuthInitialize.class.getName() + ".create")
					.addPoolLocator("Gregorys-MBP", 10334);

			cache = factory.create();

			ClientRegionFactory<String, String> crf = cache.createClientRegionFactory(ClientRegionShortcut.PROXY);

			region = crf.create("test");

			region.put("test", "test");
		}
		finally
		{
			try {
				if (region != null)
					region.close();
			} catch (Exception e) {
			}

			try {
				if (cache != null)
					cache.close();
			} catch (Exception e) {
			}
		}

	}
	
	@Test
	public void authenticationAllEncrypted() {

		
		ClientCache cache  = null;
		Region<String, String> region = null;
		
		String username = "encrypted";
		String password = "{cryption}a8+kDY+shMmL2ZCOV+/njA==";

		try
		{
			ClientCacheFactory factory = new ClientCacheFactory().set("security-username", username)
					.set("security-password", password)
					.set("security-client-auth-init", CryptionPropertyAuthInitialize.class.getName() + ".create")
					.addPoolLocator("Gregorys-MBP", 10334);

			cache = factory.create();

			ClientRegionFactory<String, String> crf = cache.createClientRegionFactory(ClientRegionShortcut.PROXY);

			region = crf.create("test");

			region.put("test", "test");
		}
		finally
		{
			try {
				if (region != null)
					region.close();
			} catch (Exception e) {
			}

			try {
				if (cache != null)
					cache.close();
			} catch (Exception e) {
			}
		}

	}
	
	@Test
	public void authenticationStoredEncryptedPasswordInvalid() {

		
		ClientCache cache  = null;
		Region<String, String> region = null;
		
		String username = "encrypted";
		String password = "NOGOOD";

		try
		{
			ClientCacheFactory factory = new ClientCacheFactory().set("security-username", username)
					.set("security-password", password)
					.set("security-client-auth-init", CryptionPropertyAuthInitialize.class.getName() + ".create")
					.addPoolLocator("Gregorys-MBP", 10334);

			cache = factory.create();

			ClientRegionFactory<String, String> crf = cache.createClientRegionFactory(ClientRegionShortcut.PROXY);

			region = crf.create("test");

			region.put("test", "test");
			fail();
		}
		catch(ServerOperationException e)
		{
			assertTrue(e.getMessage().contains("AuthenticationFailed"));
		}	
		finally
		{
			try {
				if (region != null)
					region.close();
			} catch (Exception e) {
			}

			try {
				if (cache != null)
					cache.close();
			} catch (Exception e) {
			}
		}

	}

	@Test
	public void authenticationVALID() {

		Region<String, String> region = null;
		ClientCache cache = null;

		try {
			String username = "valid";
			String password = "valid";

			ClientCacheFactory factory = new ClientCacheFactory().set("security-username", username)
					.set("security-password", password)
					.set("security-client-auth-init", CryptionPropertyAuthInitialize.class.getName() + ".create")
					.addPoolLocator("Gregorys-MBP", 10334);

			cache = factory.create();

			ClientRegionFactory<String, String> crf = cache.createClientRegionFactory(ClientRegionShortcut.PROXY);

			try {
				region = crf.create("test");
			} catch (RegionExistsException e) {
				region = cache.getRegion("test");
			}

			region.put("test", "test");

		} finally {
			try {
				if (region != null)
					region.close();
			} catch (Exception e) {
			}

			try {
				if (cache != null)
					cache.close();
			} catch (Exception e) {
			}
		}

	}

	@Test
	public void authenticationINVALID() {

		Region<String, String> region = null;
		ClientCache cache = null;
		try {
			String username = "INVALID";
			String password = "INVALID";

			ClientCacheFactory factory = new ClientCacheFactory().set("security-username", username)
					.set("security-password", password)
					.set("security-client-auth-init", CryptionPropertyAuthInitialize.class.getName() + ".create")
					.addPoolLocator("Gregorys-MBP", 10334);

			cache = factory.create();

			ClientRegionFactory<String, String> crf = cache.createClientRegionFactory(ClientRegionShortcut.PROXY);

			region = crf.create("test");

			region.put("test", "test");
			fail();
		} 
		catch(ServerOperationException e)
		{
			assertTrue(e.getMessage().contains("AuthenticationFailed"));
		}	
		finally {
		
			try {
				if (region != null)
					region.close();
			} catch (Exception e) {
			}

			try {
				if (cache != null)
					cache.close();
			} catch (Exception e) {
			}
		}

	}
}
