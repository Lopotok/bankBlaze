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

    public Employee getAdminById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    public void saveAdmin(Employee employee) {
        String encodedPassword = passwordEncoder.encode(employee.getPassword());
        employee.setPassword(encodedPassword);
        employeeRepository.save(employee);
    }

    public void deleteAdminByName(String name) {
        try {
            Employee employee = employeeRepository.findByName(name).orElse(null);
            if (employee != null) {
                employeeRepository.delete(employee);
            }
        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException("Hiba történt az admin törlése közben: " + e.getMessage());
        }
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
        Permission permission = permissionService.getPermissionByEmployee(employee);
        int actualCount = 0;
        if (permission.getForRetail()) {
            actualCount = queueNumberRepository.countByActiveIsTrueAndToRetailIsTrue();
        } else if (permission.getForCorporate()) {
            actualCount = queueNumberRepository.countByActiveIsTrueAndToCorporateIsTrue();
        } else if (permission.getForTeller()) {
            actualCount = queueNumberRepository.countByActiveIsTrueAndToTellerIsTrue();
        } else if (permission.getForPremium()) {
            actualCount = queueNumberRepository.countByActiveIsTrueAndToPremiumIsTrue();
        }
        return actualCount;
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

    public QueueNumber determineNextQueueNumber(String permission, Permission permissions) {
        QueueNumber nextQueueNumber = null;
        if (permission != null) {
            if (Boolean.TRUE.equals(permissions.getForRetail())) {
                nextQueueNumber = queueNumberRepository.findFirstByActiveTrueAndToRetailTrue();
            } else if (Boolean.TRUE.equals(permissions.getForCorporate())) {
                nextQueueNumber = queueNumberRepository.findFirstByActiveTrueAndToCorporateTrue();
            } else if (Boolean.TRUE.equals(permissions.getForTeller())) {
                nextQueueNumber = queueNumberRepository.findFirstByActiveTrueAndToTellerTrue();
            } else if (Boolean.TRUE.equals(permissions.getForPremium())) {
                nextQueueNumber = queueNumberRepository.findFirstByActiveTrueAndToPremiumTrue();
            }
        }
        return nextQueueNumber;
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

    public void deleteNextQueueNumber(QueueNumber nextQueueNumber) {
        if (nextQueueNumber != null) {
            Desk desk = deskService.findDeskByQueueNumber(nextQueueNumber);
            if (desk != null) {
                desk.setQueueNumber(null);
                deskService.saveDesk(desk);
            }
            queueNumberRepository.delete(nextQueueNumber);
        } else {
            throw new IllegalArgumentException("A nextQueueNumber értéke null, nem törölhető.");
        }
    }

    public String setActualPermission(Employee employee) {
        Permission permission = permissionService.getPermissionByEmployee(employee);
        String actualPermission = null;
        if (permission.getForRetail()) {
            actualPermission = "Lakosság";
        } else if (permission.getForCorporate()) {
            actualPermission = "Vállalat";
        } else if (permission.getForTeller()) {
            actualPermission = "Pénztár";
        } else if (permission.getForPremium()) {
            actualPermission = "Prémium";
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





