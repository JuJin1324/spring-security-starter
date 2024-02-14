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

### HttpBasicAuthenticationProvider
> ```java
> @Component
> public class HttpBasicAuthenticationProvider implements AuthenticationProvider {
> 
>     @Override
>     public Authentication authenticate(Authentication authentication) throws AuthenticationException {
>         String username = authentication.getName();
>         String password = authentication.getCredentials().toString();
> 
>         UserDetails userDetails = isValidUser(username, password);
> 
>         if (userDetails != null) {
>             return new UsernamePasswordAuthenticationToken(
>                     username,
>                     password,
>                     userDetails.getAuthorities());
>         } else {
>             throw new BadCredentialsException("Incorrect user credentials !!");
>         }
>     }
> 
>     @Override
>     public boolean supports(Class<?> authenticationType) {
>         return authenticationType
>                 .equals(UsernamePasswordAuthenticationToken.class);
>     }
> 
>     private UserDetails isValidUser(String username, String password) {
>         if (username.equals("admin") && password.equals("1234")) {
>             return User
>                     .withUsername(username)
>                     .password("NOT_DISCLOSED")
>                     .roles("ADMIN")
>                     .build();
>         }
>         return null;
>     }
> }
> ```
> authentication 객체에서 username 과 password 를 추출하여 isValidUser 메서드를 호출한다.    
> isValidUser 메서드에 amdin/1234 를 하드코딩하여 유효한 로그인 정보인지를 판별하였다.  

### 참조사이트
> [HTTP basic auth란? - (인증 방식 4단계 / Hand-Shaking / 보안 / realm)](https://blog.naver.com/PostView.naver?blogId=asd7005201&logNo=222446348947&parentCategoryNo=&categoryNo=23&viewDate=&isShowPopularPosts=true&from=search)

---

## Access token 을 통한 인증(Authentication) 구현
### Security 설정
> ```java
> @Configuration
> @EnableWebSecurity
> @RequiredArgsConstructor
> public class SecurityConfig {
>     ...
>     // access token 을 사용한 요청에서 인증 정보를 추출하기 위해서 사용자가 생성한 커스텀 filter
>     private final AccessTokenAuthenticationFilter accessTokenAuthenticationFilter;
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
>                 // accessTokenAuthenticationFilter 필터를 UsernamePasswordAuthenticationFilter 앞에 둔다.
>                 .addFilterBefore(accessTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
>                 .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)   // 세션을 사용하지 않는다고 설정한다.
>                 .and()
>                 .authenticationProvider(accessTokenAuthenticationProvider)    // 인증 로직을 담은 AccessTokenAuthenticationProvider 를 등록한다. 
>                 .csrf().disable();    // 세션을 통한 인증이 아니기 때문에 csrf 는 disable 한다.
> 
>         return http.build();
>     }
> }
> ```

### AccessTokenAuthenticationFilter
> ```java
> @Component
> public class AccessTokenAuthenticationFilter extends OncePerRequestFilter {
> 
>     @Override
>     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
>         SecurityContextHolder.getContext().setAuthentication(getAuthentication(request));
>         filterChain.doFilter(request, response);
>     }
> 
>     private Authentication getAuthentication(HttpServletRequest request) {
>         var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
>         if (ObjectUtils.isEmpty(authHeader)) {
>             return null;
>         }
>         return AccessAuthenticationToken.of(authHeader);
>     }
> }
> ```
> Header 의 엑세스 토큰 값을 추출하여 Authentication 객체를 생성한 후에 해당 객체를 SecurityContext 에 등록한다.  
> 만약 HttpHeader 에 인증 토큰이 없다면 null 을 반환한다. 혹시라도 null 대신 빈 AuthenticationToken 객체를 반환하도록 하면 
> Spring Security 는 인증을 시도한 것으로 생각해서 permitAll() 설정한 URI 도 인증을 시도하게 된다.  

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
> 	  @Override
> 	  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
> 	  	  var accessAuthenticationToken = (AccessAuthenticationToken)authentication;
> 	  	  var tokenValue = (String)accessAuthenticationToken.getCredentials();
> 	  	  var accessToken = parse(tokenValue);
>     
> 	  	  return AccessAuthenticationToken.authenticated(accessToken);
> 	  }
>   
> 	  @Override
> 	  public boolean supports(Class<?> authentication) {
> 	  	  return AccessAuthenticationToken.class.isAssignableFrom(authentication);
> 	  }
>   
> 	  private AccessToken parse(String bearerToken) {
> 	  	  try {
> 	  	  	  return parseAccessTokenUseCase.parse(bearerToken);
> 	  	  } catch (ExpiredAccessTokenException e) {
> 	  	  	  throw new CredentialsExpiredException(e.getMessage());
> 	  	  } catch (InvalidAccessTokenException e) {
> 	  	  	  throw new BadCredentialsException(e.getMessage());
> 	  	  }
> 	  }
> }
> ```
> AccessTokenAuthenticationFilter 에서 추출한 tokenValue 을 AccessToken 으로 변환하여 
> 변환이 정상적으로 동작하는지를 통해 검증한다. 변환 도중에 예외 발생 시 예외를 던진다.  
> 예외는 AuthenticationException 을 상속받은 예외만 처리가 가능하다.    

### AccessAuthenticationToken
> ```java
> @Getter
> public class AccessAuthenticationToken extends AbstractAuthenticationToken {
>     private static final String TOKEN_PREFIX = "Bearer ";
>     private final String value;
>     private final AccessToken accessToken;
> 
> 	  protected AccessAuthenticationToken(String value, AccessToken accessToken, boolean authenticated) {
> 	  	  super(null);
> 	  	  this.value = value;
> 	  	  this.accessToken = accessToken;
> 	  	  super.setAuthenticated(authenticated);
> 	  }
>   
> 	  public static AccessAuthenticationToken of(String value) {
> 	  	  validate(value);
>     
> 	  	  return new AccessAuthenticationToken(value, null, false);
> 	  }
>   
> 	  public static AccessAuthenticationToken authenticated(AccessToken accessToken) {
> 	  	  return new AccessAuthenticationToken(null, accessToken, true);
> 	  }
>   
> 	  @Override
> 	  public Object getCredentials() {
> 	  	  return getValue();
> 	  }
>   
> 	  @Override
> 	  public Object getPrincipal() {
> 	  	  return getValue();
> 	  }
>   
> 	  private static void validate(String value) {
> 	  	  if (isEmptyString(value) || !value.startsWith(TOKEN_PREFIX)) {
> 	  	  	  throw new IllegalArgumentException("Bearer token needs to include a word. \"Bearer \"");
> 	  	  }
> 	  }
>   
> 	  private static boolean isEmptyString(String value) {
> 	  	  return value == null || value.isBlank();
> 	  }
> }
> ```

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
>     public SecurityFilterChain accessTokenFilterChain(HttpSecurity http) throws Exception {
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
> 여기서는 '/actuator' 로 시작하는 URI 은 Http Basic Authentication 을 이용하고, 나머지는 Access Token Authentication 을 이용하도록 설정하였다.

### 등록된 FilterChain 확인
> FilterChainProxy.class 의 doFilter 메서드의 첫 부분에 break point 를 걸고 아무 API 호출을 한다.   
> debugger 에서 this 아래 filterChains 에서 등록된 SecurityFilterChain 을 확인할 수 있다.

---

## Form Login 을 통한 인증(Authentication) 구현
### Security 설정
> SecurityConfig.java
> ```java
> @Configuration
> @EnableWebSecurity
> public class SecurityConfig {
>     @Bean
>     public PasswordEncoder passwordEncoder() {
>         return new BCryptPasswordEncoder();
>     }
> 
>     @Bean
>     public UserDetailsService userDetailsService() {
>         return new CustomUserDetailService(passwordEncoder());
>     }
> 
>     @Bean
>     public DaoAuthenticationProvider daoAuthenticationProvider() {
>         var provider = new DaoAuthenticationProvider();
>         provider.setPasswordEncoder(passwordEncoder());
>         provider.setUserDetailsService(userDetailsService());
> 
>         return provider;
>     }
> 
>     @Bean
>     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
>         http.authorizeRequests()
>                 // GET /login 은 인증없는 접근을 허가한다. 
>                 .antMatchers(GET, "/login").permitAll()
>                 .anyRequest().authenticated()
>                 .and()
>                 // 로그인으로 Form Login 을 사용한다.
>                 .formLogin()
>                 // 로그인 페이지의 URL 을 입력한다.
>                 .loginPage("/login")
>                 // 로그인 프로세스의 URL 을 입력한다. 이 URL 은 직접 구현하는 것이 아닌 Spring Security 에서 처리하며, 
>                 // 위의 로그인 페이지에서 <form> 태그에 POST 로 submit 할 URL 을 지정하는 것이다. 
>                 .loginProcessingUrl("/login")
>                 // 로그인이 성공하면 리다이렉트할 페이지의 URL 을 입력한다.
>                 .defaultSuccessUrl("/home")
>                 .and()
>                 .logout()
>                 // 로그아웃의 URL 을 입력한다. 이 URL 은 직접 구현하는 것이 아닌 Spring Security 에서 처리한다.
>                 .logoutUrl("/logout")
>                 // 로그아웃 시 세션을 제거한다.
>                 .invalidateHttpSession(true)
>                 // 로그아웃 시 쿠키를 제거한다. 여기서는 스프링 세션인 JSESSIONID 를 제거한다.
>                 .deleteCookies("JSESSIONID");
> 
>             return http.build();
>     }
> }
> ``` 

### Form 로그인을 위한 계정 확인 서비스 사용자화
> CustomUserDetailService.java
> ```java
> @RequiredArgsConstructor
> public class CustomUserDetailService implements UserDetailsService {
>     private final static String ADMIN_USERNAME = "testUsername";
>     private final static String ADMIN_PASSWORD = "Password1234";
> 
>     private final PasswordEncoder passwordEncoder;
> 
>     @Override
>     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
>         // TODO: RDB Repository 와 연계 코드로 수정 가능.
> 
>         if (!username.equals(ADMIN_USERNAME)) {
>             throw new UsernameNotFoundException("Invalid username");
>         }
>         return User.builder()
>                 .username(username)
>                 .roles(ADMIN_USERNAME)
>                 .password(ADMIN_PASSWORD)
>                 .passwordEncoder(passwordEncoder::encode)
>                 .build();
>     }
> }
> ```

---

## 인가(Authorization) 구현
### TODO
> TODO

---

## AOP based security
### TODO
> TODO
