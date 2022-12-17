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

## Spring security authentication
### SecurityFilterChain Bean 등록
> `@Configuration` 빈 클래스에 `@EnableWebSecurity` 를 선언 후  
> SecurityFilterChain 을 빈으로 등록하는 메서드를 생성한다.

### 동작 구성
> **1.FilterChainProxy**  
> doFilter 메서드에 breakPoint 를 걸어서 debugger 섹션에서 this 아래 filterChains 변수에서 위에서 빈으로 등록했던 SecurityFilterChain 의 객체 정보를 확인할 수 있다.   
> 
> **2.ExceptionTranslationFilter**   
> Authentication filter 중 가장 먼저 진입하는 filter 이며 예외 전파를 위해서 가장 먼저 진입하는 Filter 이다.  
> 해당 필터의 doFilter 메서드에서 try/catch 블락의 catch 블락 안에 breakPoint 를 걸어서 내부에서 어떤 예외가 발생했는지 확인할 수 있다.  
> 
> **3.AuthenticationFilter**
> client 에서 request 로 받은 username 과 password 를 AuthenticationToken 객체에 담은 뒤 `SecurityContextHolder.getContext().setAuthentication(authentication);`
> 를 통해서 AuthenticationProvider 로 전달한다.   
> 주의할 점은 AuthenticationFilter 에서 예외를 발생시키지 않는다는 것이다. 예외를 발생시킬 시 SecurityConfig 에서 설정한 .permitAll() 과 같이 
> 인가 설정이 걸린 URI 들이 정상 동작하지 않을 수 있다.
> 
> **4.AuthenticationManager**   
> 등록된 AuthenticationProvider 들을 사용해서 authenticate 를 진행한다.
> 
> **5.AuthenticationProvider**  
> securityContext 로 전달받은 AuthenticationToken 객체를 검증 후 인증 처리를 진행한다.

### UsernamePasswordAuthenticationToken 주의 사항
> `UsernamePasswordAuthenticationToken` 생성 시 2개의 생성자를 통해서 인증 정보를 생성할 수 있다.  
> 1.UsernamePasswordAuthenticationToken(Object principal, Object credentials): 생성자 내부를 보면 인증되지 않은 인증 정보를 생성한다.  
> 2.UsernamePasswordAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities):

## Error Debugging Point
> Spring security 에러 시 디버깅해봐야할 포인트   

### ExceptionTranslationFilter
> `doFilter` 메서드에서 `catch (Exception ex) {` 아래 부분에 브레이크 포인트를 둔 후 디버깅한다.  

## Deprecated
### WebSecurityConfigurerAdapter
> 기존 SecurityConfig 클래스에 WebSecurityConfigurerAdapter 클래스를 상속받아서 사용하던 것에서 상속을 제거 후 
> SecurityFilterChain 클래스를 빈으로 등록해서 사용한다.
> 
> 참조사이트: [Security WebSecurityConfigurerAdapter Deprecated 해결하기](https://devlog-wjdrbs96.tistory.com/434)

## Security Basic
### CSRF 공격
> 크로스 사이트 요청 위조는 사용자가 인증한 세션에서 웹 애플리케이션이 정상적인 요청과 비정상적인 요청을 구분하지 못하는 점을 악용하는 공격 방식
> 즉, 세션을 이용한 인증 유지의 경우 csrf 보안 옵션 활성화가 필요하고, JWT 처럼 세션이 아닌 토큰 인증 방식에서는 csrf 보안 옵션 활성화가 필요 없음.

### 참조사이트
> [크로스 사이트 요청 위조란?](https://nordvpn.com/ko/blog/csrf/)

## JWT authentication filter
### JwtAuthenticationFilter
> `회원 생성 API` 와 `인증 토큰 조회 API` 는 `Registration token` 을 통해서 인증한다.  
> `업데이트된 인증 토큰 조회 API` 는 `Refresh token` 을 통해서 인증한다.  
> 그 외 API 는 `Access token` 을 통해서 인증한다.   
> 
> filter 에서 `SecurityContextHolder.getContext().setAuthentication(authentication);` 를 명시해서 
> SecurityContext 에 authentication 을 저장해두면 `AbstractSecurityInterceptor.authenticateIfRequired` 메서드에서 
> SecurityContext 에 저장된 authentication 을 `AuthenticationProvider` 에 넘겨준 후 AuthenticationProvider 에서 인증 진행한 결과인
> authentication 객체를 SecurityContext 에 저장한다.  

### UnauthorizedExceptionFilter
> `JwtAuthenticationFilter` 에서 인증에 실패하여 UnauthorizedException 이 발생한 경우 예외를 Response 에 담아서 반환한다.    
> Filter 의 호출 규칙에 따라서 `JwtAuthenticationFilter` 앞에 호출되도록 한다.  
> ```java
> ...
> .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
> .addFilterBefore(unauthorizedExceptionFilter, JwtAuthenticationFilter.class)
> ...
> ```

## JWT 로 접근 인증하는 도메인
> 1.전화번호 인증 생성 -> 2.전화번호 인증 검증(verificationCode 는 DB에서 직접 조회해서 찾는다.) 
> -> 3.회원 생성(이미 생성한 회원이면 건너뛴다.) -> 4.인증 토큰 조회 -> 5.회원 단건 조회

### Authentication
> 전화번호 인증 생성: `POST /authentications/phone`  
> 전화번호 인증 검증: `POST /authentications/phone?verify=login`  
> 인증 토큰 조회: `GET /authentications/token`  

### 전화번호 인증 생성
> URI: `POST /authentications/phone`  
> Request body: `{"countryCode": "82", "phoneNo": "01012341234"}`  
> Response status: `201 Created`   
> Response body: `{"authId": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"}"`

### 전화번호 인증 검증
> URI: `POST /authentications/phone`  
> Request param: `verify=true`  
> Request body: `{"authId": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx", "verificationCode": "123456"}`
> Response body: `{"registrationToken": "{jwt}"}`  

### 인증 토큰 조회
> URI: `GET /authentications/token`
> Request Header: `authorization=bearer {registration token}`
> Response body: `{"accessToken": "", "refreshToken": ""}`

### User
> 회원 생성: `POST /users`    
> 회원 단건 조회: `GET /users/{userId}`    

###  회원 생성
> URI: `POST /users`  
> Request body: `{"nickname": "닉네임"}`  
> Request header: `Authorization: {registration token}`    
> Response status: `201 Created`  
> Response body: `{"userId": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"}`  

### 회원 단건 조회
> URI: `GET /users/{userId}`  
> Request header: `Authorization: Bearer xxxxxxxxxxxxxxxxxxxxxxxxxxx(accessToken)`  
> Response body: `{"userId": "", "nickname": ""}`

## JWT Security setting
### WebSecurityCustomizer
> 인증(Authentication) 이 필요없는 URI 는 `WebSecurityCustomizer` 를 Bean 으로 등록   
> ```java
> @Bean
> public WebSecurityCustomizer webSecurityCustomizer() {
>   return (web) -> web.ignoring().antMatchers("/authentication/**");
> }
> ```

### authorizeRequests
> `http.authorizeRequests`: 요청에 대한 사용권한 체크

### Authorized Domain
> `.antMatchers("/admin/**").hasRole("ROLE_ADMIN")`: Admin 도메인만 `ROLE_ADMIN` 접근 권한이 필요하도록 설정  
> `.antMatchers("/**").permitAll()`: 나머지 도메인은 인증만하면 인가 없이 접근 허용

### addFilterBefore
> `http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)`:
> JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 넣는다.
>
> 토큰에 저장된 유저정보를 활용하여야 하기 때문에 CustomUserDetailService 클래스를 생성해야한다.

### http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
> 세션을 사용하지 않는다고 설정한다.

## Http Basic 으로 접근 인증하는 도메인
### Multiple SecurityFilterChain
> `SecurityConfig.java` 파일에 여러개의 SecurityFilterChain 생성이 가능하다.  
> FilterChainProxy.class 의 doFilter 메서드의 첫 부분에 break point 를 걸고 API 호출을 한다.   
> debugger 에서 this 아래 filterChains 에서 등록된 SecurityFilterChain 을 확인할 수 있다.  
> 
> URI 에 따라서 사용하는 SecurityFilterChain 분기   
> `http.antMatcher()`(antMatchers 아님) 에 해당 SecurityFilterChain 을 이용할 URI 를 지정할 수 있다.   
> 여기서는 '/actuator' 로 시작하는 URI 은 Http Basic Authentication 을 이용하고, 나머지는 Json Web Token 을 이용하도록 설정하였다.  
> 
> AuthenticationProvider
> SecurityFilterChain 을 여러개 Bean 으로 등록하게 되면 `http.authenticationProvider()` 를 통해서 각각의 
> AuthenticationFilter, AuthenticationManager 와 연동되는 AuthenticationProvider 를 명시해주어야 한다.  
> Http Basic Authentication 의 경우 `MonitoringAuthenticationProvider` 를 명시하였으며, 
> JWT 의 경우 `JwtAuthenticationProvider` 를 명시하였다.  

### Actuator 
> Spring Actuator 를 사용하여 프로젝트 health check, 프로젝트 빌드 정보 및 프로젝트 metrics 정보를 조회할 수 있도록
> `GET /actuator/health`, `GET /actuator/info` Endpoint 을 제공하였다.  
