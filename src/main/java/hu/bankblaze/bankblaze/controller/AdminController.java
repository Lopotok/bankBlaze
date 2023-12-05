package hu.bankblaze.bankblaze.controller;

import hu.bankblaze.bankblaze.model.Employee;
import hu.bankblaze.bankblaze.model.Permission;
import hu.bankblaze.bankblaze.service.AdminService;
import hu.bankblaze.bankblaze.service.DeskService;
import hu.bankblaze.bankblaze.service.PermissionService;
import hu.bankblaze.bankblaze.service.QueueNumberService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Controller
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private AdminService adminService;
    private QueueNumberService queueNumberService;
    private PermissionService permissionService;
    private DeskService deskService;


    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getAllClerks(Model model) {
        model.addAttribute("admins", adminService.getAllAdmins());
        model.addAttribute("clerks", adminService.getAllActiveClerks());
        model.addAttribute("permissions", permissionService.getAllPermissions());
        return "admin";
    }

    @PostMapping("/update")
    public String updatePermissions(@RequestParam Map<String, String> permissions) {
        permissionService.updatePermissions(permissions);
        return "redirect:/admin";
    }


    @GetMapping("/statistics")
    public String getStatistics(Model model) {
        model.addAttribute("admins", adminService.getAllAdmins());
        model.addAttribute("retailCount", queueNumberService.countRetail());
        model.addAttribute("corporateCount", queueNumberService.countCorporate());
        model.addAttribute("tellerCount", queueNumberService.countTeller());
        model.addAttribute("premiumCount", queueNumberService.countPremium());
        return "statistics";
    }

    @GetMapping("/desk")
    public String setDesks(Model model) {
        model.addAttribute("admins", adminService.getAllAdmins());
        model.addAttribute("employees", adminService.getAllActiveClerks());
        model.addAttribute("desks", deskService.getAllDesks());
        return "desk";
    }

    @PostMapping("/desk")
    public String setDesks(@RequestParam Map<String, String> employeesToDesks) {
        deskService.assignEmployeeToDesk(employeesToDesks);
        return "redirect:/admin/desk";
    }

    @GetMapping("/registration")
    public String createEmployee(Model model) {
        model.addAttribute("admins", adminService.getAllAdmins());
        model.addAttribute("newEmployee", new Employee());
        return "registration";
    }

    @PostMapping("/registration")
    public String createEmployee(@ModelAttribute("newEmployee") Employee employee,
                                 @RequestParam("defaultRole") String defaultRole,
                                 @RequestParam("defaultPermissionRetail") String defaultPermissionRetail,
                                 @RequestParam("defaultPermissionCorporate") String defaultPermissionCorporate,
                                 @RequestParam("defaultPermissionTeller") String defaultPermissionTeller,
                                 @RequestParam("defaultPermissionPremium") String defaultPermissionPremium) {

        employee.setRole(String.valueOf(defaultRole));
        adminService.saveAdmin(employee);

        permissionService.createPermissionForEmployee(employee, defaultPermissionRetail,
                defaultPermissionCorporate, defaultPermissionTeller, defaultPermissionPremium);
        return "redirect:/admin";
    }

    @GetMapping("/management")
    public String getManagement(Model model) {
        model.addAttribute("admins", adminService.getAllAdmins());
        model.addAttribute("employees", adminService.getAllClerks());
        return "management";
    }

    @PostMapping("/management")
    public String setManagement(@RequestParam("action") String action, String name) {
        switch (action) {
            case "USER", "inactive" -> adminService.modifyEmployeeByName(name, action);
            case "delete" -> adminService.deleteAdminByName(name);
        }

        return "redirect:/admin";
    }

    @PostMapping("update/{id}")
    public String updatePermission(@PathVariable("id") Integer id, @ModelAttribute("permission") Permission update) {
        permissionService.savePermisson(update);
        return "redirect:/admin/" + id;
    }

    @PostMapping("/eod")
    public String endOfDay() {
        queueNumberService.deleteAllQueueNumbers();
        return "redirect:/admin";
    }
    @GetMapping("/delete")
    public String showDeleteForm(Model model) {
        model.addAttribute("admins", adminService.getAllAdmins());
        model.addAttribute("admins", adminService.getAllClerks());
        return "delete";
    }
    @PostMapping("/delete")
    public String deleteAdmin(@RequestParam("action") String action, String name) {
        if (action.equals("delete")) {
            adminService.deleteAdminByName(name);
        }
        return "redirect:/admin";
    }
}