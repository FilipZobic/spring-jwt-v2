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

### Amazon Elastic Container Registry

1. Setup IAM user should belong to group or assigned policy directly
<pre>
{
   "Version": "2012-10-17",
   "Statement": [
     {
     "Effect": "Allow",
     "Action": ["ecr:GetAuthorizationToken"],
     "Resource": "*"
     }
   ]
}
</pre>
2. Save IAM user Credentials
3. Elastic Containe Registry -> Create Repository (private) -> name: jwt-v2-api <a href='https://docs.aws.amazon.com/AmazonECR/latest/userguide/repository-policy-examples.html'>(More Info)</a>
4. Make sure user has permissions for repository <br>Select -> Action -> Edit -> Permission -> Add Statement
<pre>
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "AllowPushPull",
      "Effect": "Allow",
      "Principal": {
        "AWS": "arn:aws:iam::USER_ID_PLACEHOLDER:user/USER_ID_PLACEHOLDER"
      },
      "Action": [
        "ecr:BatchCheckLayerAvailability",
        "ecr:BatchGetImage",
        "ecr:CompleteLayerUpload",
        "ecr:GetDownloadUrlForLayer",
        "ecr:InitiateLayerUpload",
        "ecr:PutImage",
        "ecr:UploadLayerPart"
      ]
    }
  ]
}
</pre>
4. Install `aws-cli`
<pre>
brew install awscli
</pre>
5. Install `amazon-ecr-credential-helper` (Google recommends to use credentail helper for Jib) <a href='https://github.com/awslabs/amazon-ecr-credential-helper'>More info</a>
<pre>
brew install docker-credential-helper-ecr
</pre>
6. Setup docker configuration `~/.docker/configuration.json` (xxxxxxxxxxxx generated ECR id)
<pre>
{
  "credHelpers": {
    "xxxxxxxxxxxx.dkr.ecr.eu-central-1.amazonaws.com": "ecr-login"
  }
}
</pre>
7. Setup AWS configuration `~/.aws/config` (no file extension)
<pre>
[default]
region = eu-central-1
</pre>
8. Setup AWS credentials `~/.aws/credentials` <br>
[] - matches a AWS profile will select values bellow it (`[default]` will be selected when not specifying profile) 
<pre>
[ecr-push-user]
aws_access_key_id = IAM_ID_PLACEHOLDER
aws_secret_access_key = IAM_SECRET_PLACEHOLDER
</pre>
9. Configure JIB (pom.xml) `docker-remote profile` (xxxxxxxxxxxx generated ECR id)
<pre>
&lt;to>
    &lt;image>xxxxxxxxxxxx.dkr.ecr.eu-central-1.amazonaws.com/jwt-api-v2&lt;/image>
    &lt;credHelper>ecr-login&lt;/credHelper>
&lt;/to>
</pre>
Or to avoid exposing registry id in public repository send a variable instead
<pre>
&lt;to>
    &lt;image>${container_registry}.dkr.ecr.eu-central-1.amazonaws.com/jwt-api-v2&lt;/image>
    &lt;credHelper>ecr-login&lt;/credHelper>
&lt;/to>
</pre>
10. Run maven command with maven profile set `AWS_PROFILE` system env var if no default value after that add flag `-Pdocker-remote` or `-P docker-remote` so we use publishing profile
<pre>
AWS_PROFILE=ecr-push-user mvn  --debug jib:build -Pdocker-remote
</pre>
Or if we are using a variable instead of setting xxxxxxxxxxxx directly we can add `-Dcontainer_registry=xxxxxxxxxxxx`
<pre>
AWS_PROFILE=ecr-push-user mvn  --debug jib:build -Pdocker-remote -Dcontainer_registry=xxxxxxxxxxxx
</pre>

**Caution!<br>**
If you get 403 or 404 check your `&lt;image>...&lt;/image>` tag or `container_registry` variable

### Maven profiles 

**docker-remote** <br><br>
**docker-local** <br><br>
**manual-integration-test** <br><br>
**native** <br><br>

**DRAFT** <br>
1. `brew install docker-credential-helper`
<pre>
{
	"credsStore": "osxkeychain"
}
</pre>
2. Configure default credentials in `~/.aws/credentials`
3. Run: 
`aws ecr get-login-password --region eu-central-1 | docker login --username AWS --password-stdin xxxxxxxxxxxx.dkr.ecr.eu-central-1.amazonaws.com`
<pre>
{
	"auths": {
		"xxxxxxxxxxxx.dkr.ecr.eu-central-1.amazonaws.com": {} -- will be generated
	},
	"credsStore": "osxkeychain"
}
</pre>