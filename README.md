# spring-security-starter

## Gradle
### Spring Security
> build.gradle
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
> build.gradle
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
> 허나 인증 필터는 요청으로 들어온 인증 정보를 `Authentication` 객체로 변환하는 것이 주 임무임으로 예외를 throw 하는 대신에 authentication 객체의 멤버 변수를 모두 null 로 만들거나
> 커스텀 authentication 객체를 생성하여 에러 정보를 담아 마지막 필터인 FilterSecurityInterceptor 까지 요청을 전달 후에 실제 인증 로직이 담긴 커스텀 Provider 객체에서
> 예외를 던지도록 하는 것이 나아보인다. Provider 를 호출하는 ProviderManager 에서 처리해주는 예외는 `AuthenticationException` 를 상속한 예외만 처리해주니
> Provider 에서 예외를 던지려는 경우 `AuthenticationException` 를 상속하여 커스텀 예외를 던지거나 Spring security 에서 제공되는 예외를 던지도록 하자.  
> 
> **FilterSecurityInterceptor**  
> 커스텀 인증 필터에서 인증 정보를 `Authentication` 객체에 담은 후에 `SecurityContextHolder.getContext().setAuthentication(authentication);` 
> 를 통해서 SecurityContext 에 담으면 필터를 지나고 지나서 마지막 필터인 FilterSecurityInterceptor 에 도착하고 여기서 `AuthenticationManager` 를 
> 구현한 `ProviderManager` 를 통해서 실제 인증 로직이 진행된다.  
> 
> ProviderManager 에 사용자 커스텀 인증 로직을 담은 Provider 를 만들어 등록한다.
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

## HTTP BASIC 를 통한 인증(Authentication) 구현
### Security 설정
> ```java
> @Configuration
> @EnableWebSecurity
> @RequiredArgsConstructor
> public class SecurityConfig {
>    ...
>    // HTTP Basic 인증을 위해서 사용자가 생성한 커스텀 Provider
>    private final HttpBasicAuthenticationProvider httpBasicAuthenticationProvider;
> 
>    @Bean
>    @Order(1)
>    public SecurityFilterChain monitoringFilterChain(HttpSecurity http) throws Exception {
>         http.antMatcher("/basics/**")  // antMatchers 가 아닌 antMatcher 로 현재 설정이 적용될 도메인 URI 를 설정한다. 이 외의 URI 에는 설정이 적용되지 않는다.
>               .authorizeRequests()             
>               .antMatchers("/basics/permits/**").permitAll()        // 해당 도메인 URI 의 요청은 모두 인증을 건너뜀
>               .antMatchers("/basics/admins/**").hasRole("ADMIN")   // 해당 도메인 URI 요청은 모두 인증 및 ADMIN 역할의 인가가 필요
>               .and()
>               .httpBasic()                // HTTP BASIC 을 통한 인증
>               .realmName("Application Monitoring")    // realm은 아래와 같이 해설 형식으로 구성되어 사용자가 권한에 대한 범위를 이해하는 데 도움이 되어야 합니다. 
>                                                     // 예시처럼 Application 의 Monitoring 에 해당하는 정보만 열람이 가능하다는 표시  
>               .and()
>               .authenticationProvider(httpBasicAuthenticationProvider)  // 인증 로직을 담은 커스텀 Provider 클래스 등록
>               .csrf().disable();           // 세션을 통한 인증이 아니기 때문에 csrf 는 disable 한다.
> 
>         return http.build();
>    }
> }
> ```

### 참조사이트
> [HTTP basic auth란? - (인증 방식 4단계 / Hand-Shaking / 보안 / realm)](https://blog.naver.com/PostView.naver?blogId=asd7005201&logNo=222446348947&parentCategoryNo=&categoryNo=23&viewDate=&isShowPopularPosts=true&from=search)

---

## Bearer token 을 통한 인증(Authentication) 구현
### Security 설정
> ```java
> @Configuration
> @EnableWebSecurity
> @RequiredArgsConstructor
> public class SecurityConfig {
>     ...
>     // bearer token 을 사용한 요청에서 인증 정보를 추출하기 위해서 사용자가 생성한 커스텀 filter
>     private final BearerTokenAuthenticationFilter bearerTokenAuthenticationFilter;
>     // access token 을 사용한 요청에서 인증 정보를 검증하기 위해서 사용자가 생성한 커스텀 Provider
>     private final AccessTokenAuthenticationProvider accessTokenAuthenticationProvider;    
> 
>     @Bean
>     public SecurityFilterChain mainFilterChain(HttpSecurity http) throws Exception {
>
>         http.authorizeRequests()
>                 .anyRequest().authenticated()     // 모든 요청에 인증이 요구된다.
>                 .antMatchers("/authentications/phone/**").permitAll()  // 해당 URI 의 요청에는 인증이 요구되지 않는다. 
>                 .and()
>                 // bearerTokenAuthenticationFilter 필터를 UsernamePasswordAuthenticationFilter 앞에 둔다.
>                 .addFilterBefore(bearerTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
>                 .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)   // 세션을 사용하지 않는다고 설정한다.
>                 .and()
>                 .authenticationProvider(accessTokenAuthenticationProvider)    // 인증 로직을 담은 AccessTokenAuthenticationProvider 를 등록한다. 
>                 .csrf().disable();    // 세션을 통한 인증이 아니기 때문에 csrf 는 disable 한다.
> 
>         return http.build();
>     }
> }
> ```

### BearerTokenAuthenticationFilter
> ```java
> @Component
> public class BearerTokenAuthenticationFilter extends OncePerRequestFilter {
> 
>     @Override
>     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
>         SecurityContextHolder.getContext().setAuthentication(getAuthentication(request));
>         filterChain.doFilter(request, response);
>     }
> 
>     private Authentication getAuthentication(HttpServletRequest request) {
>         String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
>         try {
>             return BearerAuthenticationToken.of(new BearerToken(authHeader));
>         } catch (InvalidBearerTokenException e) {
>             return BearerAuthenticationToken.emptyToken();
>         }
>     }
> }
> ```
> Header 의 Bearer 토큰 값을 추출하여 Authentication 객체를 생성한 후에 해당 객체를 SecurityContext 에 등록한다.  
> 예외는 throw 하지 않으며 Bearer 토큰 값이 유효하지 않아 예외가 발생한 경우 try/catch 를 통해 그에 맞는 Authentication 객체를 만들어서 
> SecurityContext 에 등록한다.     

### [사용 안함] UnauthorizedExceptionFilter
> `JwtAuthenticationFilter` 에서 인증에 실패하여 UnauthorizedException 이 발생한 경우 예외를 Response 에 담아서 반환한다.    
> Filter 의 호출 규칙에 따라서 `JwtAuthenticationFilter` 앞에 호출되도록 한다.  
> JwtAuthenticationFilter 에서는 단순히 인증 정보를 Authentication 객체로 변환만 담당하기 때문에 예외를 발생시키지 않도록 구현하였다.
> 그래서 UnauthorizedExceptionFilter 는 더이상 사용하지 않는다.

### AccessTokenAuthenticationProvider
> ```java
> @Component
> @RequiredArgsConstructor
> public class AccessTokenAuthenticationProvider implements AuthenticationProvider {
>     private final ParseAccessTokenUseCase parseAccessTokenUseCase;
> 
>     @Override
>     public Authentication authenticate(Authentication authentication) throws AuthenticationException {
>         BearerAuthenticationToken bearerAuthenticationToken = (BearerAuthenticationToken) authentication;
>         BearerToken bearerToken = (BearerToken) bearerAuthenticationToken.getCredentials();
>         AccessToken accessToken = parse(bearerToken);
> 
>         return new AccessAuthenticationToken(accessToken);
>     }
> 
>     @Override
>     public boolean supports(Class<?> authentication) {
>         return AccessAuthenticationToken.class.isAssignableFrom(authentication);
>     }
> 
>     private AccessToken parse(BearerToken bearerToken) {
>         try {
>             return parseAccessTokenUseCase.parse(bearerToken.getValue());
>         } catch (ExpiredAccessTokenException e) {
>             throw new CredentialsExpiredException(e.getMessage());
>         } catch (InvalidAccessTokenException e) {
>             throw new BadCredentialsException(e.getMessage());
>         }
>     }
> }
> ```
> BearerTokenAuthenticationFilter 에서 추출한 BearerToken 을 AccessToken 으로 변환하여 
> 변환이 정상적으로 동작하는지를 통해 검증한다. 변환 도중에 예외 발생 시 예외를 던진다.  
> 또한 bearerAuthenticationToken.getCredentials() 를 통해서 bearerAuthenticationToken 가 emptyToken(빈 토큰)인 경우 예외를 던지도록
> 하였다.  
> 예외는 AuthenticationException 을 상속받은 예외만 처리가 가능하다.    

---

## 여러개의 SecurityFilterChain
### Config
> `SecurityConfig.java` 파일에 여러개의 SecurityFilterChain 생성이 가능하다.  
> ```java
> @Configuration
> @EnableWebSecurity
> @RequiredArgsConstructor
> public class SecurityConfig {
>     ...
> 
>     @Bean
>     public SecurityFilterChain bearerTokenFilterChain(HttpSecurity http) throws Exception {
>         http.authorizeRequests()
>             ...;
> 
>         return http.build();
>     }
> 
>     @Bean
>     @Order(1)
>     public SecurityFilterChain httpBasicFilterChain(HttpSecurity http) throws Exception {
>         http.antMatcher("/actuator/**")    // 현재 SecurityFilterChain 은 /actuator 아래 URI API 를 호출 시에만 적용된다.
>                                            // 다른 URI 의 API 호출 시에는 mainFilterChain 에서 생성한 SecurityFilterChain 이 적용된다. 
>             .authorizeRequests()
>             ...;
> 
>         return http.build();
>     }
> }
> ```
> `http.antMatcher()`(antMatchers 아님) 에 해당 SecurityFilterChain 을 이용할 URI 를 지정할 수 있다.   
> 여기서는 '/actuator' 로 시작하는 URI 은 Http Basic Authentication 을 이용하고, 나머지는 Bearer Token 을 이용하도록 설정하였다.

### 등록된 FilterChain 확인
> FilterChainProxy.class 의 doFilter 메서드의 첫 부분에 break point 를 걸고 아무 API 호출을 한다.   
> debugger 에서 this 아래 filterChains 에서 등록된 SecurityFilterChain 을 확인할 수 있다.

---

## 인가(Authorization) 구현
### TODO
> TODO

---

## AOP based security
### TODO
> TODO
