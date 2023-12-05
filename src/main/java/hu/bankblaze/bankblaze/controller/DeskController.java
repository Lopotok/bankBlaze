package hu.bankblaze.bankblaze.controller;

import hu.bankblaze.bankblaze.model.Employee;
import hu.bankblaze.bankblaze.service.AdminService;
import hu.bankblaze.bankblaze.service.DeskService;
import hu.bankblaze.bankblaze.service.PermissionService;
import hu.bankblaze.bankblaze.service.QueueNumberService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/desk")
public class DeskController {
    public QueueNumberService queueNumberService;
    public DeskService deskService;
    public AdminService adminService;
    public PermissionService permissionService;


    @GetMapping("/next")
    public String getNextClient(Model model) {
        Employee employee = adminService.getEmployeeByName(adminService.getLoggedInUsername());
        model.addAttribute("desk", deskService.getDeskByEmployeeId(employee.getId()));
        model.addAttribute("retailCount", queueNumberService.countRetail()+1);
        model.addAttribute("corporateCount", queueNumberService.countCorporate()+1);
        model.addAttribute("tellerCount", queueNumberService.countTeller()+1);
        model.addAttribute("premiumCount", queueNumberService.countPremium()+1);
        model.addAttribute("actualPermission", adminService.setActualPermission(employee));
        model.addAttribute("actualCount", adminService.setActualCount(employee));
        model.addAttribute("employeeCount", adminService.setEmployeeCount(employee));

        return "next";
    }

    @PostMapping("next")
    public String getNextClient(){
        return "redirect:/next";
    }

}

