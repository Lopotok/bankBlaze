package hu.bankblaze.bankblaze.controller;

import hu.bankblaze.bankblaze.service.QueueNumberService;
import hu.bankblaze.bankblaze.service.RetailService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("/retail")
public class RetailController {

    private QueueNumberService queueNumberService;
    private RetailService retailService;

    @GetMapping
    public String getAllRetail(Model model) {
        model.addAttribute("retails", retailService.getAllRetail());
        return "showRetail";
    }

    @PostMapping
    public String getAllRetail(Model model, @RequestParam("id") int number) {
        model.addAttribute("header", "Lakossági");
        queueNumberService.modifyNumber(retailService.generateQueueNumber(number));
        queueNumberService.modifyToRetail(true);
        return "queueNumber";
    }
}
