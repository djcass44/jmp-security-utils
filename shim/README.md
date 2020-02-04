# jmp-security-utils/shim

This module aims to implement the minimum required JPA beans in order to utilise jmp-security-utils/core

#### Usage

```kotlin
@EnableSecurityDataShim
@EnableOAuth2 // recommended
@EnableJwt // recommended
@SpringBootApplication
class MyApp
```