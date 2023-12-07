package hu.bankblaze.bankblaze.service;

import hu.bankblaze.bankblaze.model.Desk;
import hu.bankblaze.bankblaze.model.Employee;
import hu.bankblaze.bankblaze.model.Permission;
import hu.bankblaze.bankblaze.model.QueueNumber;
import hu.bankblaze.bankblaze.repo.DeskRepository;
import hu.bankblaze.bankblaze.repo.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class DeskService {
    private DeskRepository deskRepository;
    private EmployeeRepository employeeRepository;
    private QueueNumberService queueNumberService;
    private PermissionService permissionService;


    public Desk getDeskById(Long id) {
        return deskRepository.findById(id).orElse(null);
    }

    public List<Desk> getAllDesks() {
        return deskRepository.findAll();
    }

    public void modifyEmployee(Long id, Long newEmployee) {
        Desk desk = getDeskById(id);
        Employee employee = employeeRepository.findById(newEmployee).orElse(null);
        if (employee != null) {
            desk.setEmployee(employee);
            deskRepository.save(desk);
        }
    }

    public void assignEmployeeToDesk(Map<String, String> employeesToDesks) {
        Set<Long> selectedDesks = new HashSet<>();

        employeesToDesks.forEach((param, value) -> {
            if (param.startsWith("desk")) {
                Long deskId = Long.parseLong(value);
                selectedDesks.add(deskId);
            }
        });

        for (Long deskId : selectedDesks) {
            String employeeParam = "employee" + deskId;
            if (employeesToDesks.containsKey(employeeParam)) {
                Long employeeId = Long.parseLong(employeesToDesks.get(employeeParam));
                modifyEmployee(deskId, employeeId);
            }
        }
    }

    public Desk getDeskByEmployeeId(Long employeeId) {
        return deskRepository.findByEmployeeId(employeeId);
    }

    public void removeEmployeeFromDesk(Long employeeId){
        Desk deskWithEmployeeId = getDeskByEmployeeId(employeeId);
        if (deskWithEmployeeId != null) {
            deskWithEmployeeId.setEmployee(null);
            deskRepository.save(deskWithEmployeeId);
        }
    }
    public void saveDesk(Desk desk) {
        deskRepository.save(desk);
    }

    public Desk nextQueueNumber(Employee employee) {
        Desk desk = getDeskByEmployeeId(employee.getId());
        Permission permission = permissionService.getPermissionByEmployee(employee);
        List<QueueNumber> queueNumberList = new ArrayList<>();
        try {
            if (permission.getForRetail() && queueNumberService.countRetail() > 0) {
                queueNumberList.add(queueNumberService.getNextRetail());
            }
            if (permission.getForCorporate() && queueNumberService.countCorporate() > 0) {
                queueNumberList.add(queueNumberService.getNextCorporate());
            }
            if (permission.getForTeller() && queueNumberService.countTeller() > 0) {
                queueNumberList.add(queueNumberService.getNextTeller());
            }
            if (permission.getForPremium() && queueNumberService.countPremium() > 0) {
                queueNumberList.add(queueNumberService.getNextPremium());
            }
            QueueNumber queueNumber = queueNumberService.getSmallestNumber(queueNumberList);
            desk.setQueueNumber(queueNumber);
            deskRepository.save(desk);
            return desk;
        } catch (IndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    protected Desk findDeskByQueueNumber(QueueNumber queueNumber) {
        return deskRepository.findByQueueNumber(queueNumber);
    }

    public int countRetailCustomersUnderService() {
        return deskRepository.countByQueueNumberIsNotNullAndQueueNumberToRetailIsTrue();
    }

    public int countCorporateCustomersUnderService() {
        return deskRepository.countByQueueNumberIsNotNullAndQueueNumberToCorporateIsTrue();
    }

    public int countTellerCustomersUnderService() {
        return deskRepository.countByQueueNumberIsNotNullAndQueueNumberToTellerTrue();
    }

    public int countPremiumCustomersUnderService() {
        return deskRepository.countByQueueNumberIsNotNullAndQueueNumberToPremiumIsTrue();
    }

    public void deleteQueueNumbersFromAllTheDesks() {
        List<Desk> desks = getAllDesks();
        for (int i = 0; i < desks.size(); i++) {
            Desk desk = desks.get(i);
            desk.setQueueNumber(null);
            deskRepository.save(desk);
        }
    }

    public void deleteEmployeesFromAllTheDesks() {
        List<Desk> desks = getAllDesks();
        for (int i = 0; i < desks.size(); i++) {
            Desk desk = desks.get(i);
            desk.setEmployee(null);
            deskRepository.save(desk);
        }
    }
}
