# Getting Started
## Requirements:
```
Spring Boot: 3.3.5
Maven: 3.9+
Java: 17
(Optional) Docker Engine: 25.0.2
```
## Spring Security Implemented Features In This Project

- Custom ```DaoAuthenticationProvider``` Using H2 Database and Spring JDBC
- Custom ```AuthenticationManager```
- Custom ```MessageSource``` for spring security
- Custom ```UserDetailsService```
- Custom Login and Logout Page
- Custom Login Success Page to ```/home```
- Custom ```WebAuthenticationDetails``` to capture extra login parameter with username and password
- Implemented CSRF
- ```DaoAuthenticationProvider``` change password flow in ```/change-password``` endpoint
- Custom Method based Authorization
- Custom ```AuthorizationManager```
- Spring Security Event Listeners

### Clone this repository:

```bash
git clone https://github.com/deepaksorthiya/spring-boot-3-security-6-custom-authprovider.git
```
```bash
cd spring-boot-3-security-6-custom-authprovider
```

### Build Project:

```bash
./mvnw clean package
```

### Run Project:

```bash
./mvnw spring-boot:run
```
### Users for Testing
```
USER1 ==> Username: user Password: password
USER2 ==> Username: admin Password : admin
```

### Rest APIs
http://localhost:8080/user <br>
http://localhost:8080/admin <br>
http://localhost:8080/hasPermission <br>
http://localhost:8080/principal <br>
http://localhost:8080/exception <br>
http://localhost:8080/authUser <br>
http://localhost:8080/change-password <br>
http://localhost:8080/authentication <br>
http://localhost:8080/server-info

h2 database console :
<br>
http://localhost:8080/h2-console

### Build Docker Image(docker should be running):

```bash
./mvnw clean spring-boot:build-image -DskipTests
```

### Run Using Docker
```bash
docker run --name spring-boot-3-security-6-custom-authprovider -p 8080:8080 deepaksorthiya/spring-boot-3-security-6-custom-authprovider:0.0.1-SNAPSHOT
```
### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.2.5/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.2.5/maven-plugin/reference/html/#build-image)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/3.2.5/reference/htmlsingle/index.html#actuator)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.2.5/reference/htmlsingle/index.html#web)
* [Spring Security](https://docs.spring.io/spring-boot/docs/3.2.5/reference/htmlsingle/index.html#web.security)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.2.5/reference/htmlsingle/index.html#data.sql.jpa-and-spring-data)
* [JDBC API](https://docs.spring.io/spring-boot/docs/3.2.5/reference/htmlsingle/index.html#data.sql)
* [Spring Data JDBC](https://docs.spring.io/spring-boot/docs/3.2.5/reference/htmlsingle/index.html#data.sql.jdbc)
* [Thymeleaf](https://docs.spring.io/spring-boot/docs/3.2.5/reference/htmlsingle/index.html#web.servlet.spring-mvc.template-engines)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Accessing Relational Data using JDBC with Spring](https://spring.io/guides/gs/relational-data-access/)
* [Managing Transactions](https://spring.io/guides/gs/managing-transactions/)
* [Using Spring Data JDBC](https://github.com/spring-projects/spring-data-examples/tree/master/jdbc/basics)
* [Handling Form Submission](https://spring.io/guides/gs/handling-form-submission/)

