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

## 작업
### 1.JWT Login Process

### 2.Account Login Process
