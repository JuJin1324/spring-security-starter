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

---

## 개요
### 인증(Authentication) 및 인가(Authorization)
> **인증(Authentication)**  
> 해당 사용자가 본인이 맞는지 확인하는 과정  
> 
> **인가(Authorization)**  
> 해당 사용자가 요청하는 자원을 실행할 수 있는 권한이 있는가를 확인하는 과정

### Credential 기반 인증
> Principal(접근 주체): 아이디  
> Credential(비밀번호): 비밀번호  

### Filter
> Spring security 는 인증과 권한에 대한 부분을 Filter 로 처리한다.  
> 
> **요청 처리 flow**  
> 정상: 요청 -> FilterChainProxy -> BasicAuthenticationFilter -> ... -> ExceptionTranslationFilter -> FilterSecurityInterceptor -> Controller
>
> 에러 발생: 요청 -> FilterChainProxy -> BasicAuthenticationFilter -> ... -> ExceptionTranslationFilter ->
> [예외 발생] FilterSecurityInterceptor -> [예외 Catch] ExceptionTranslationFilter
> 
> Spring security 는 등록된 기본 Filter 들은 로직 중간에 예외를 발생시키지 않고 등록된 마지막 필터인 FilterSecurityInterceptor 까지 
> 모두 거친 후에 FilterSecurityInterceptor 에서 필요한 예외를 발생시켜 바로 위 필터인 ExceptionTranslationFilter 에서 예외를 처리하여 
> 응답을 주도록 구현되어 있다.  

### 커스텀 인증 필터
> Spring security 가 제공하는 FormLogin, HTTP BASIC 외에 JWT 와 같은 사용자 커스텀 인증 방식을 사용하고 싶은 경우에는 
> 사용자가 커스텀 인증 필터 클래스를 생성한 후에 Security config 에 해당 커스텀 인증 필터를 등록한다.
> 
> 커스텀 인증 필터를 등록할 때 주의해야할 사항이 있는데 Spring security 의 기본 필터들은 예외를 throw 하지 않고 마지막 필터인 FilterSecurityInterceptor 까지
> 요청을 보낸다.  
> 
> 만약 사용자가 만든 커스텀 인증 필터에서 예외를 throw 하고 싶은 경우 해당 예외를 응답으로 처리해줄 커스텀 인증 예외처리 필터를 함께 만들어서 커스텀 인증 필터 앞에 등록해야한다.  
> 허나 인증 필터는 요청으로 들어온 인증 정보를 `Authentication` 객체로 변환하는 것이 주 임무임으로 예외를 throw 하는 대신에 authentication 객체의 멤버 변수가 모두 null 로 만들거나
> 커스텀 authentication 객체를 생성하여 에러 정보를 담아 마지막 필터인 FilterSecurityInterceptor 까지 요청을 전달 후에 실제 인증 로직이 담긴 커스텀 Provider 객체에서
> 예외를 던지도록 하는 것이 나아보인다. Provider 를 호출하는 ProviderManager 에서 처리해주는 예외는 `AuthenticationException` 를 상속한 예외만 처리해주니
> Provider 에서 예외를 던지려는 경우 `AuthenticationException` 를 상속하여 커스텀 예외를 던지거나 Spring security 에서 제공되는 예외를 던지도록 하자.  
> 
> **FilterSecurityInterceptor**  
> 커스텀 인증 필터에서 인증 정보를 `Authentication` 객체에 담은 후에 `SecurityContextHolder.getContext().setAuthentication(authentication);` 
> 를 통해서 SecurityContext 에 담으면 필터를 지나고 지나서 마지막 필터인 FilterSecurityInterceptor 에 도착하고 여기서 `AuthenticationManager` 를 
> 구현한 `ProviderManager` 를 통해서 실제 인증 로직이 진행된다.  
> 
> ProviderManager 에 사용자 커스텀 인증 로직을 담은 Provider 만들어 등록한다.
> 그러면 요청이 필터를 거쳐 마지막 필터인 FilterSecurityInterceptor 에 도착하고 여기서 ProviderManager 가 사용자가 등록한 커스텀 Provider 를 사용하여
> 인증을 진행하게 된다.  

---

## Security config
### Security 설정
> `@Configuration` 빈 클래스에 `@EnableWebSecurity` 를 선언 후 SecurityFilterChain 을 빈으로 등록하는 메서드를 생성한다.

### Deprecated
> 기존 SecurityConfig 클래스에 WebSecurityConfigurerAdapter 클래스를 상속받아서 사용하던 것에서 상속을 제거 후
> SecurityFilterChain 클래스를 빈으로 등록해서 사용한다.

### CSRF 공격
> 크로스 사이트 요청 위조는 사용자가 인증한 세션에서 웹 애플리케이션이 정상적인 요청과 비정상적인 요청을 구분하지 못하는 점을 악용하는 공격 방식
> 즉, 세션을 이용한 인증 유지의 경우 csrf 보안 옵션 활성화가 필요하고, JWT 처럼 세션이 아닌 토큰 인증 방식에서는 csrf 보안 옵션 활성화가 필요 없음.

### 참조사이트
> [크로스 사이트 요청 위조란?](https://nordvpn.com/ko/blog/csrf/)
> [Security WebSecurityConfigurerAdapter Deprecated 해결하기](https://devlog-wjdrbs96.tistory.com/434)
---

## HTTP BASIC 를 통한 Security 구현
### Security 설정
> ```java
> @Configuration
> @EnableWebSecurity
> @RequiredArgsConstructor
> public class SecurityConfig {
>     ...
>
> @Bean
> @Order(1)
> public SecurityFilterChain monitoringFilterChain(HttpSecurity http) throws Exception {
>         http.authorizeRequests()
>             .antMatcher("/domains/**")  // 현재 설정이 적용될 도메인 URI
>             .csrf().disable()           // csrf disable
>             .antMatchers("/permits/**").permitAll() // 해당 도메인 URI 의 요청은 모두 인증을 건너뜀
>             .antMatchers("/admins/**").hasRole("ADMIN")   // 해당 도메인 URI 요청은 모두 인증 및 ADMIN 역할의 인가가 필요
>             .and()
>             .httpBasic()                // HTTP BASIC 을 통한 인증
>             .realmName("Application Monitoring")    // realm은 아래와 같이 해설 형식으로 구성되어 사용자가 권한에 대한 범위를 이해하는 데 도움이 되어야 합니다. 
>                                                     // 예시처럼 Application 의 Monitoring 에 해당하는 정보만 열람이 가능하다는 표시  
>             .and()
>             .authenticationProvider(monitoringAuthenticationProvider);  // 인증 로직을 담은 커스텀 Provider 클래스 등록 
> 
>         return http.build();
>     }
> }
> ```

### 참조사이트
> [HTTP basic auth란? - (인증 방식 4단계 / Hand-Shaking / 보안 / realm)](https://blog.naver.com/PostView.naver?blogId=asd7005201&logNo=222446348947&parentCategoryNo=&categoryNo=23&viewDate=&isShowPopularPosts=true&from=search)

---

## JWT(Json Web Token) 을 통한 Security 구현
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
