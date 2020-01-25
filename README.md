# JMP Security Utils

This project contains a number of utilities used by [JMP](https://github.com/djcass44/jmp)
Please be aware that these utilities are made for a specific purpose and may not meet your needs.
They are also in active development so production use is not recommended.

It provides the following:

* Config-based CORS toggling
* Disables CSRF
* *(optional)* OAuth2-based authentication
* *(optional)* Jwt-based authentication (database +/ ldap)

## OAuth2

OAuth2 can be enabled using the following annotation

```kotlin
@EnableOAuth2
@SpringBootApplication
class SpringApp

fun main(args: Array<String>) {
    runApplication<SpringApp>(*args)
}
```

## JWT

JWT can be enabled using the following annotation.

```kotlin
@EnableJwt
@SpringBootApplication
class SpringApp

fun main(args: Array<String>) {
    runApplication<SpringApp>(*args)
}
```

## LDAP

LDAP requires the `@EnableJwt` annotation as it piggy-backs of its Jwt generation.
LDAP support is enabled via the following spring config:

* `SPRING_LDAP_ENABLED=true`
* ```yaml
    spring:
      ldap:
        enabled: true
    ```
* `spring.ldap.enabled=true`

Configuration of LDAP is done using standard spring configuration.

# Collaboration

Feel free to open a merge request if you have something you want to add.