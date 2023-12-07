package hu.bankblaze.bankblaze.service;

import hu.bankblaze.bankblaze.model.Employee;
import hu.bankblaze.bankblaze.model.SecurityUser;
import hu.bankblaze.bankblaze.repo.EmployeeRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Component
@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    public JpaUserDetailsService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Employee> employee = employeeRepository.findByName(username);
        return employee
                .map(SecurityUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username));
    }
}
