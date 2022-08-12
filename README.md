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
* Image upload & processing
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
4. Add data source (Postgres)
5. Terminate process - stop server
6. `docker-compose down` - *stop Postgres & pgAdmin*

___
**Quickstart**

Build API docker image locally & start it with database & pg admin uses (dock profile not relevant for production).
Uses default bridge network between 3 services.

1. `mvn clean package compile com.google.cloud.tools:jib-maven-plugin:3.2.1:dockerBuild -Dimage=spring-jwt-v2`
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
AWS_PROFILE=ecr-push-user mvn clean package --debug jib:build -Pdocker-remote
</pre>
Or if we are using a variable instead of setting xxxxxxxxxxxx directly we can add `-Dcontainer_registry=xxxxxxxxxxxx`
<pre>
AWS_PROFILE=ecr-push-user mvn  --debug jib:build -Pdocker-remote -Dcontainer_registry=xxxxxxxxxxxx
AWS_PROFILE=ecr-push-user mvn clean package --debug jib:build -Pdocker-remote -Dcontainer_registry=xxxxxxxxxxxx
</pre>

**Caution!<br>**
If you get 403 or 404 check your `&lt;image>...&lt;/image>` tag or `container_registry` variable

### Maven profiles 

**docker-remote** <br><br>
**docker-local** <br><br>
**manual-integration-test** <br><br>
**native** <br><br>

**Draft** <br>
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

### Connect to RDS from local machine
1. Pick RDS create database (Postgres).
2. Set password username
3. We use default VPC group which means we can't yet connect outside AWS network (only ec2 for example can connect) <br>
if we wish to connect we will need to set `Public access: Yes` & assign to a VPC which has: `Inbound rule: Postgres allow from anywhere` <br>
***Note!!*** Don't do this for prod environments or for instances running on AWS (never assign to default VPC or set public access)
4. To add inbound rule to default VPC go to security groups -> find default VPCs security group & click -> Edit Inbound rules
<pre>
{
  "newRules": [ { "Type": "PostgresSQL", "Source": "Anywhere-IPv4" }, { "Type": "PostgresSQL", "Source": "Anywhere-IPv4" } ]
}
</pre>
And yet again don't do this in production
5. Add data source (Postgres) to IntelliJ (username & password) IAM also possible
6. Not supported in this spring version but we could add for better integration (read replicas, cluster support)
<pre>
&lt;dependency>
    &lt;groupId>org.springframework.cloud&lt;/groupId>
    &lt;artifactId>spring-cloud-starter-aws&lt;/artifactId>
&lt;/dependency>
</pre>

### Deploying to Elastic Beanstalk
1. Create a new RDS with new VPS no public access -> Add access ElasticBeans HTTP (or setup when creating elastic beans instance)
2. Go to Roles -> Elastic Beans (default role for elastic bean instances) -> attach permission EC2ContainerRegistryReadOnly (so we can download our container)
3. Create `Dockerrun.aws.json` <br>
*Note version 3 is when you use compose* <br>
*For accessing other private container registries (none  ECS) Take a look at  <br> <a href='https://docs.aws.amazon.com/AmazonECS/latest/developerguide/private-auth-container-instances.html'>Private registry authentication for container instances</a> <br> <a href='https://www.oasisworkflow.com/accessing-private-docker-images-from-aws-elastic-beanstalk'>Accessing private Docker images from AWS Elastic Beanstalk</a> <br> <a href='https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/single-container-docker-configuration.html'>Docker configuration</a>*
<pre>
{
  "AWSEBDockerrunVersion": "1",
  "Image": {
    "Name": "xxxxxxxxxxxx.dkr.ecr.eu-central-1.amazonaws.com/jwt-v2-api:latest"
  },
  "Ports": [
    {
      "ContainerPort": 8080,
      "HostPort": 8080
    }
  ]
}
</pre>
3. Elastic Beans -> New -> Select Docker -> Upload `Dockerrun.aws.json`
4. Setup configuration ENV variables (values for all the ENV variables in application.yml(default one) - currently stored as plaintext presumably a better solution exists AWS secret manager to name one)
5. Finish 
6. Repeat the steps just to confirm
### CI/CD Setup (GitHub Actions)
#### Integration testing 

#### Deployment