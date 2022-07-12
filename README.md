## Features

* JWT
* Flyway
* Hibernate / Data JPA
  * Queries
  * Indexing
  * Caching
  * Composite Key Structure
* Proper PostgresSQL configuration & syntax
* Integration Testing (h2m)
* Unit testing
* Mocking
* Reflection (in testing)
* Generics (JWTService)
### Migration generated roles
* Admin
* Moderator
* User

### Configuration


### Spring security filters

__Username & password Authentication filter__
* Responsible for user login (populating the authentication object which is then handled by spring magic)
* SecurityContextHolder.getContext().getAuthentication().getPrincipal() - Is our principle object in this case model.PrincipleUser (implements our UserDetails extension interface ExpandedUserDetails, so we can get email and id) which is extension of spring.security.User it is created in UserDetailsService it needed to be extended to include userId & email. UserDetailsService is called in DaoAuthenticationProvider which is called in AuthenticationManager all of this is true because it is configured as such.
* If there is a case where authentication.principle is not PrincipleUser (ExpandedUserDetails) we will get Runtime exception

__Database Access__
* We needed to add @Transactional to userDetailsService.loadUserByUsername since we are using lazy loading we could use eager loading which would allow us to remove the annotation