package hu.bankblaze.bankblaze.controller;

import hu.bankblaze.bankblaze.service.CorporateService;
import hu.bankblaze.bankblaze.service.QueueNumberService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
@RequestMapping("/corporate")
public class CorporateController {

    private QueueNumberService queueNumberService;
    private CorporateService corporateService;

    @GetMapping
    public String getCorporate(Model model) {
        model.addAttribute("corporates", corporateService.getAllCorporates());
        return "showCorporate";
    }

    @PostMapping
    public String getCorporate(Model model, @RequestParam("id") int number) {
        model.addAttribute("header", "Vállalati");
        queueNumberService.modifyNumber(corporateService.generateQueueNumber(number));
        queueNumberService.modifyToCorporate(true);
        return "queueNumber";
    }
}
