# JMP Security Utils

This project contains a number of security utilities used by [JMP](https://github.com/djcass44/jmp).
Please be aware that these utilities are made for a specific purpose and may not meet your needs.
They are also in active development so production use is not recommended.

This library provides the following:

* Config-based CORS toggling
* Disables CSRF
* OAuth2-based authentication
* Jwt-based authentication (database and/or ldap)

## Integration

In order to use this library you will need to extend the following classes:
* `UserRepository`
* `GroupRepository`
* `SessionRepository`

Examples of this can be seen in the NoOp implementations provided by this library.

Enable this project by adding the following to your `spring.factories`

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
    dev.dcas.jmp.spring.security.SecurityAutoConfiguration
```

## LDAP

LDAP support is enabled via one of the following spring config:

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