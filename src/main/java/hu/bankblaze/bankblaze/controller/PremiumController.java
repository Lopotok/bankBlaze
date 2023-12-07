package hu.bankblaze.bankblaze.controller;

import hu.bankblaze.bankblaze.service.PremiumService;
import hu.bankblaze.bankblaze.service.QueueNumberService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("/premium")
public class PremiumController {

    private PremiumService premiumService;
    private QueueNumberService queueNumberService;

    @GetMapping
    public String getPremium(Model model) {
        model.addAttribute("header", "Prémium");
        queueNumberService.modifyToPremium(true);
        queueNumberService.modifyNumber(premiumService.generateQueueNumber());
        return "queueNumber";
    }

}
