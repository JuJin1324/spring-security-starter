# spring-security-starter
## Gradle
### Spring Security
> ```groovy
> ...
> dependencies {
>   ...
>   implementation 'org.springframework.boot:spring-boot-starter-security'
>   testImplementation 'org.springframework.security:spring-security-test'
> }
> ...
> ```

### JWT
> ```groovy
> ...
> dependencies {
>   ...
>   implementation 'io.jsonwebtoken:jjwt:0.9.1'
> }
> ...
> ```

## Deprecated
### WebSecurityConfigurerAdapter
> 기존 SecurityConfig 클래스에 WebSecurityConfigurerAdapter 클래스를 상속받아서 사용하던 것에서 상속을 제거 후 
> SecurityFilterChain 클래스를 빈으로 등록해서 사용한다.
> 
> 참조사이트: [Security WebSecurityConfigurerAdapter Deprecated 해결하기](https://devlog-wjdrbs96.tistory.com/434)

## HttpSecurity + JWT 설정
### Disable Http Basic
> `http.httpBasic().disable()`: 일반적인 루트가 아닌 다른 방식으로 요청시 거절, 
> header에 id, pw가 아닌 token(jwt)을 달고 간다. 그래서 basic이 아닌 bearer를 사용한다.

### authorizeRequests
> `http.authorizeRequests`: 요청에 대한 사용권한 체크

### addFilterBefore
> `http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)`: 
> JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 넣는다.
> 
> 토큰에 저장된 유저정보를 활용하여야 하기 때문에 CustomUserDetailService 클래스를 생성해야한다.

### antMatchers + authenticated
> `http.antMatchers("/test").authenticated()`: /test API 는 권한은 필요 없고 인증만 필요.

### antMatchers + hasRole
> `http.antMatchers("/admin/**").hasRole("ADMIN")`: /admin/** API 는 인증 뿐만 아니라 권한도 필요.

### http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) 
> 세션을 사용하지 않는다고 설정한다.

## CSRF 공격
### CSRF 공격이란?
> 크로스 사이트 요청 위조는 사용자가 인증한 세션에서 웹 애플리케이션이 정상적인 요청과 비정상적인 요청을 구분하지 못하는 점을 악용하는 공격 방식
> 즉, 세션을 이용한 인증 유지의 경우 csrf 보안 옵션 활성화가 필요하고, JWT 처럼 세션이 아닌 토큰 인증 방식에서는 csrf 보안 옵션 활성화가 필요 없음.

### 참조사이트
> [크로스 사이트 요청 위조란?](https://nordvpn.com/ko/blog/csrf/)
