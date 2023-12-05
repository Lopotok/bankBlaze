package hu.bankblaze.bankblaze.controller;

import hu.bankblaze.bankblaze.model.Desk;
import hu.bankblaze.bankblaze.model.Employee;
import hu.bankblaze.bankblaze.model.Permission;
import hu.bankblaze.bankblaze.model.QueueNumber;
import hu.bankblaze.bankblaze.service.AdminService;
import hu.bankblaze.bankblaze.service.DeskService;
import hu.bankblaze.bankblaze.service.PermissionService;
import hu.bankblaze.bankblaze.service.QueueNumberService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
@RequestMapping("/employee")
public class EmployeeController {

    private AdminService adminService;
    private DeskService deskService;
    private QueueNumberService queueNumberService;
    private PermissionService permissionService;
    private SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping
    @PreAuthorize("hasAuthority('USER')")
    public String getEmployeePage(Model model) {
        Employee employee = adminService.getEmployeeByName(adminService.getLoggedInUsername());
        model.addAttribute("desk", deskService.getDeskByEmployeeId(employee.getId()));
        model.addAttribute("actualPermission", adminService.setActualPermission(employee));
        model.addAttribute("retailCount", queueNumberService.countRetail()+1);
        model.addAttribute("corporateCount", queueNumberService.countCorporate()+1);
        model.addAttribute("tellerCount", queueNumberService.countTeller()+1);
        model.addAttribute("premiumCount", queueNumberService.countPremium()+1);
        model.addAttribute("actualCount", adminService.setActualCount(employee));
        model.addAttribute("employeeCount", adminService.setEmployeeCount(employee));
        return "employee";
    }

    @PostMapping
    public String nextQueueNumber(Model model) {
        Employee employee = adminService.getEmployeeByName(adminService.getLoggedInUsername());
        Desk desk = deskService.nextQueueNumber(employee);
        if (desk != null) {
            model.addAttribute("actualPermission", adminService.setActualPermission(employee));
            simpMessagingTemplate.convertAndSend("/topic/app", desk);
            return "redirect:/desk/next";
        }
        return "redirect:/employee";
    }

    @GetMapping("/closure")
    public String getClosure(Model model){
        Employee employee = adminService.getEmployeeByName(adminService.getLoggedInUsername());
        String permissions = permissionService.getPermissionByLoggedInUser(employee.getId());
        Permission permission = permissionService.getPermissionByEmployee(employee);
        QueueNumber nextQueueNumber = adminService.determineNextQueueNumber(permissions, permission);
        model.addAttribute("nextQueueNumber", nextQueueNumber);
        adminService.processNextQueueNumber(nextQueueNumber);
        return "redirect:/employee";
    }

    @GetMapping("/redirect")
    public String getRedirect(Model model, @RequestParam("redirectLocation") String redirectLocation) {
        Employee employee = adminService.getEmployeeByName(adminService.getLoggedInUsername());
        String permissions = permissionService.getPermissionByLoggedInUser(employee.getId());
        Permission permission = permissionService.getPermissionByEmployee(employee);
        QueueNumber nextQueueNumber = adminService.determineNextQueueNumber(permissions, permission);
        model.addAttribute("nextQueueNumber", nextQueueNumber);
        adminService.processRedirect(nextQueueNumber, redirectLocation);
        return "redirect:/employee";
    }

    @GetMapping("/deleteNumber")
    public String deleteNumber(Model model){
        Employee employee = adminService.getEmployeeByName(adminService.getLoggedInUsername());
        String permissions = permissionService.getPermissionByLoggedInUser(employee.getId());
        Permission permission = permissionService.getPermissionByEmployee(employee);
        QueueNumber nextQueueNumber = adminService.determineNextQueueNumber(permissions, permission);
        model.addAttribute("nextQueueNumber", nextQueueNumber);
        adminService.deleteNextQueueNumber(nextQueueNumber);
        return "redirect:/employee";
    }

}

