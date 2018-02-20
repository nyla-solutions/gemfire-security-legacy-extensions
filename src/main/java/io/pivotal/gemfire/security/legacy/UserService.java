package io.pivotal.gemfire.security.legacy;

public interface UserService
{

	/**
	 * 
	 * @param id the user id
	 * @return the user 
	 */
	User findUser(String id);

}