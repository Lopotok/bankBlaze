package hu.bankblaze.bankblaze.controller;

import hu.bankblaze.bankblaze.service.QueueNumberService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("/queue")
public class QueueNumberController {

    private QueueNumberService queueNumberService;

    @GetMapping("/showNumber")
    public String showQueueNumberPage(Model model) throws Exception {
        model.addAttribute("queueNumber", queueNumberService.getQueueNumber());
        model.addAttribute("count", queueNumberService.getCount());
        return "showNumber";
    }

    @PostMapping("/showNumber")
    public String confirmQueueNumber(@RequestParam("action") String action) {
        try {
            if (action.equals("delete")) {
                queueNumberService.deleteQueueNumber();
            }
            return "redirect:/home";
        } catch (Exception e) {

            e.printStackTrace();
            return "redirect:/errorPage";
        }
    }

    @GetMapping("/queueNumber")
    public String queueQueueNumberPage(Model model) {
        model.addAttribute("queueNumber", queueNumberService.getQueueNumber());
        return "queueNumber";
    }

    @PostMapping("/queueNumber")
    public String generateQueueNumber(@RequestParam String name) {
        queueNumberService.modifyName(name);
        return "redirect:/queue/showNumber";
    }

    @GetMapping("/delete/{id}")
    public String deleteQueueNumber(@PathVariable Long id) {
        queueNumberService.deleteQueueNumberById(id);
        return "redirect:/queue/showNumber";
    }

}