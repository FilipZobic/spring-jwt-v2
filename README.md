## Features

* JWT
* Hibernate / Flyway / Data JPA
  * Queries
  * Indexing
  * Caching
  * Composite Key Structure
* Proper PostgresSQL configuration & syntax
* Testing
  * Mocking
  * Reflection (in testing)
  * Unit testing
  * Integration Testing (h2m)
* Generics (JWTService)
* Custom DTO parameter validation
* Custom PreAuthorize
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

__Input validation__
* @Validated(ValidationSequence.class) @RequestBody AuthSignUpDTO signUpDTO we use this and example: @Annotation(... groups = First.class) to have an order in validation for example check if its not null then check if valid data format
* We defined our custom annotations to avoid code duplication and more control over our input fields
* Currently, UniqueEmail & UniqueUsername annotations check the database we also index those columns for faster search (UserService autowired)

### Starting the API


___
**Development**

Starts pgAdmin & postgresql database use this compose file while developing start spring app with IDE preferably

1. `docker-compose up`
2. ![Alt text](Configure-InteliJ.png?raw=true "Configure IDE set profile to dev")
3. Start with IDE
4. Terminate process - stop server
5. `docker-compose down` - *stop Postgres & pgAdmin*

___
**Quickstart**

Build API docker image locally & start it with database & pg admin uses (dock profile not relevant for production).
Uses default bridge network between 3 services.

1. `mvn compile com.google.cloud.tools:jib-maven-plugin:3.2.1:dockerBuild -Dimage=spring-jwt-v2`
2. `docker-compose -f docker-compose-dock.yaml up`
3. `docker-compose down --remove-orphans` - *stop all services*