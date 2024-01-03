package Project.OpenBook.Domain.Customer.Service;

import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Customer.Repository.CustomerRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import Project.OpenBook.Jwt.TokenDto;
import Project.OpenBook.Jwt.TokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static Project.OpenBook.Constants.ErrorCode.LOGIN_FAIL;

@Service
@RequiredArgsConstructor
public class CustomerService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final AuthenticationManagerBuilder authenticationManager;
    private final TokenManager tokenManager;

    /**
     * 관리자
     */
    @Transactional
    public TokenDto loginAdmin(String loginId, String password){
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(loginId, password);
        Authentication authentication = authenticationManager.getObject().authenticate(upToken);

        if(!authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.LOGIN_FAIL);
        }

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        Customer customer = (Customer) authentication.getPrincipal();
        TokenDto tokenDto = tokenManager.generateToken(customer);
        return tokenDto;
    }

    @Override
    public Customer loadUserByUsername(String username) throws UsernameNotFoundException {
        return customerRepository.findByNickName(username).orElseThrow(() -> {
            throw new CustomException(LOGIN_FAIL);
        });
    }
}
