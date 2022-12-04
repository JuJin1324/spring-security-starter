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

## Security Basic
### CSRF 공격
> 크로스 사이트 요청 위조는 사용자가 인증한 세션에서 웹 애플리케이션이 정상적인 요청과 비정상적인 요청을 구분하지 못하는 점을 악용하는 공격 방식
> 즉, 세션을 이용한 인증 유지의 경우 csrf 보안 옵션 활성화가 필요하고, JWT 처럼 세션이 아닌 토큰 인증 방식에서는 csrf 보안 옵션 활성화가 필요 없음.

### 참조사이트
> [크로스 사이트 요청 위조란?](https://nordvpn.com/ko/blog/csrf/)

## JWT authentication filter
### JwtAuthenticationFilter
> `회원 생성 API` 와 `인증 토큰 조회 API` 는 `Registration token` 을 통해서 인증한다.   
> 그 외 API 는 `Access token` 을 통해서 인증한다.  
> `UsernamePasswordAuthenticationToken` 생성 시 2개의 생성자를 통해서 인증 정보를 생성할 수 있다.  
> 1.UsernamePasswordAuthenticationToken(Object principal, Object credentials): 생성자 내부를 보면 인증되지 않은 인증 정보를 생성한다.  
> 2.UsernamePasswordAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities): 
> 인증된 인증 정보를 생성한다. 즉 JWT 인증이 통과한 후 인증 정보를 생성하기 위해서는 매개변수가 3개인 생성자로 인증 정보를 생성해야한다.  

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
### Actuator 
> 

## Http Basic Security setting for using monitoring tools(Spring Actuator, Prometheus and Grafana)

