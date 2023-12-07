package hu.bankblaze.bankblaze.service;

import hu.bankblaze.bankblaze.model.Desk;
import hu.bankblaze.bankblaze.model.Employee;
import hu.bankblaze.bankblaze.model.Permission;
import hu.bankblaze.bankblaze.model.QueueNumber;
import hu.bankblaze.bankblaze.repo.EmployeeRepository;
import hu.bankblaze.bankblaze.repo.PermissionRepository;
import hu.bankblaze.bankblaze.repo.QueueNumberRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AdminService {

    private EmployeeRepository employeeRepository;
    private DeskService deskService;
    private PermissionService permissionService;
    private QueueNumberRepository queueNumberRepository;
    private QueueNumberService queueNumberService;
    private PermissionRepository permissionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Employee> getAllActiveClerks() {
        return employeeRepository.getAllActiveClerks();
    }

    public List<Employee> getAllClerks() {
        return employeeRepository.getAllClerks();
    }

    public List<Employee> getAllAdmins() {
        return employeeRepository.getAllAdmins();
    }

    public void saveAdmin(Employee employee) {
        String encodedPassword = passwordEncoder.encode(employee.getPassword());
        employee.setPassword(encodedPassword);
        employeeRepository.save(employee);
    }

    public void modifyEmployeeByName(String name, String newRole) {
        Employee employee = employeeRepository.getAdminByName(name);
        employee.setRole(newRole);
        employeeRepository.save(employee);
    }

    public boolean isAdmin(String userName, String password) {
        Employee foundEmployee = employeeRepository.getAdminByName(userName);
        if (foundEmployee != null && foundEmployee.getRole().equals("ADMIN")) {
            return true;
        }

        return false;
    }

    public boolean isUser(String userName, String password) {
        Employee foundEmployee = employeeRepository.getAdminByName(userName);
        if (foundEmployee != null && foundEmployee.getRole().equals("USER")) {
            return true;
        }

        return false;
    }

    public Employee getEmployeeByName(String name) {
        return employeeRepository.findByName(name).orElse(null);
    }

    public String getLoggedInUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public int setActualCount(Employee employee) {
        Desk desk = deskService.nextQueueNumber(employee);
        int actualCount = 0;
        if (desk != null) {
            if (desk.getQueueNumber().getToRetail()) {
                actualCount = queueNumberService.countRetail();
            } else if (desk.getQueueNumber().getToCorporate()) {
                actualCount = queueNumberService.countCorporate();
            } else if (desk.getQueueNumber().getToTeller()) {
                actualCount = queueNumberService.countTeller();
            } else if (desk.getQueueNumber().getToPremium()) {
                actualCount = queueNumberService.countPremium();
            }
            return actualCount;
        }
        return 0;
    }

    public int setEmployeeCount(Employee employee) {
        Permission permission = permissionService.getPermissionByEmployee(employee);
        int employeeCount = 0;
        if (permission.getForRetail()) {
            employeeCount = permissionRepository.countByForRetailTrue();
        } else if (permission.getForCorporate()) {
            employeeCount = permissionRepository.countByForCorporateTrue();
        } else if (permission.getForTeller()) {
            employeeCount = permissionRepository.countByForTellerTrue();
        } else if (permission.getForPremium()) {
            employeeCount = permissionRepository.countByForPremiumTrue();
        }
        return employeeCount;
    }

    public void processNextQueueNumber(QueueNumber nextQueueNumber) {
        if (nextQueueNumber != null) {
            nextQueueNumber.setActive(false);
            queueNumberRepository.save(nextQueueNumber);
        }
    }

    public void processRedirect(QueueNumber nextQueueNumber, String redirectLocation) {
        if (nextQueueNumber != null) {
            nextQueueNumber.setToRetail("retail".equals(redirectLocation));
            nextQueueNumber.setToCorporate("corporate".equals(redirectLocation));
            nextQueueNumber.setToTeller("teller".equals(redirectLocation));
            nextQueueNumber.setToPremium("premium".equals(redirectLocation));
            queueNumberRepository.save(nextQueueNumber);
        }
    }

    public String setActualPermission(Employee employee) {
        Desk desk = deskService.nextQueueNumber(employee);
        String actualPermission = "";
        if (desk != null) {
            if (desk.getQueueNumber().getToRetail()) {
                actualPermission = "Lakossági";
            } else if (desk.getQueueNumber().getToCorporate()) {
                actualPermission = "Vállalati";
            } else if (desk.getQueueNumber().getToTeller()) {
                actualPermission = "Pénztári";
            } else if (desk.getQueueNumber().getToPremium()) {
                actualPermission = "Prémium";
            }
        }
        return actualPermission;
    }

    public void deleteAdminAndRelatedData(String name) {
        Employee employee = employeeRepository.findByName(name).orElse(null);
        if (employee != null) {
            deskService.removeEmployeeFromDesk(employee.getId());
            permissionService.deleteEmployee(employee.getId());
            employeeRepository.deleteById(employee.getId());
        }
    }

}





