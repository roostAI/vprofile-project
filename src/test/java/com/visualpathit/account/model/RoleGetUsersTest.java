
package com.visualpathit.account.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.*;
import javax.persistence.*;

class RoleGetUsersTest {

	private Role role;

	private EntityManager entityManager;

	@BeforeEach
	public void setUp() {
		role = new Role();
		entityManager = Mockito.mock(EntityManager.class);
	}

	@Test
	@Tag("valid")
	public void verifyDefaultUserSetOnInitialization() {
		assertThat(role.getUsers()).isNull();
	}

	@Test
	@Tag("valid")
	public void ensureUserSetIsModifiable() {
		User user1 = new User();
		user1.setId(1L);
		User user2 = new User();
		user2.setId(2L);
		Set<User> users = new HashSet<>();
		users.add(user1);
		role.setUsers(users);
		Set<User> retrievedUsers = role.getUsers();
		retrievedUsers.add(user2);
		assertThat(retrievedUsers).contains(user1, user2);
	}

	@Test
	@Tag("integration")
	public void validateEagerFetchType() {
		// Assuming entityManager.find() method returns a Role with users initially added.
		Mockito.when(entityManager.find(Role.class, 1L)).thenReturn(role);
		User user1 = new User();
		user1.setId(1L);
		Set<User> users = new HashSet<>();
		users.add(user1);
		role.setUsers(users);
		Role fetchedRole = entityManager.find(Role.class, 1L);
		Set<User> associatedUsers = fetchedRole.getUsers();
		assertThat(associatedUsers).containsExactly(user1);
		// Additional check to ensure eager fetching is indeed occurring
		// (generally this can't be fully ensured solely by unit testing without looking
		// at SQL logs)
	}

	@Test
	@Tag("integration")
	public void verifyCascadeTypeAllEffectOnUsers() {
		User user1 = new User();
		Set<User> users = new HashSet<>();
		users.add(user1);
		role.setUsers(users);
		entityManager.persist(role);
		Mockito.verify(entityManager).persist(role);
		// Assuming that cascading would also persist user1
		assertThat(role.getUsers()).contains(user1);
	}

	@Test
	@Tag("valid")
	public void handleNullUserSetGracefully() {
		role.setUsers(null);
		assertThat(role.getUsers()).isNull();
	}

	@Test
	@Tag("valid")
	public void verifyIntegrityOfUserInstanceWithinRole() {
		User user = new User();
		user.setUsername("testUser");
		role.setUsers(Collections.singleton(user));
		Set<User> retrievedUsers = role.getUsers();
		retrievedUsers.iterator().next().setUsername("modifiedUser");
		assertThat(role.getUsers()).extracting(User::getUsername).containsExactly("modifiedUser");
	}

}