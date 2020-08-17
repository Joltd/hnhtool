package com.evgenltd.hnhtool.harvester;

//@EnableWebSecurity
public class SecurityConfig {//extends WebSecurityConfigurerAdapter {
//
//    private UserDetailsService userDetailsService;
//
//    @Autowired
//    public void setSecurityConfig(final UserDetailsService userDetailsService) {
//        this.userDetailsService = userDetailsService;
//    }
//
//    @Override
//    protected void configure(final HttpSecurity http) throws Exception {
//        http.csrf().disable()
//                .authorizeRequests()
//                .antMatchers("/auth/login").permitAll()
//                .anyRequest().authenticated()
//        .and()
//                .exceptionHandling()
//                .authenticationEntryPoint(authenticationEntryPoint())
//        .and()
//                .logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK));
//    }
//
//    @NotNull
//    private AuthenticationEntryPoint authenticationEntryPoint() {
//        // for csrf purposes, but security anyway does not works
//        return (request, response, e) -> {
//            response.setStatus(HttpStatus.FORBIDDEN.value());
//            final CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
//            response.setHeader(token.getHeaderName(), token.getToken());
//        };
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Override
//    protected void configure(final AuthenticationManagerBuilder auth) {
//        final DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setPasswordEncoder(passwordEncoder());
//        provider.setUserDetailsService(userDetailsService);
//        auth.authenticationProvider(provider);
//    }
//
//    @Bean
//    @Override
//    protected AuthenticationManager authenticationManager() throws Exception {
//        return super.authenticationManager();
//    }

}
