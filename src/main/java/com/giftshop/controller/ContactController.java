package com.giftshop.controller;

import com.giftshop.model.Message;
import com.giftshop.service.MessageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContactController {

    private final MessageService messageService;

    public ContactController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @PostMapping("/contact")
    public String submit(@RequestParam String name,
                         @RequestParam(required = false) String email,
                         @RequestParam(required = false) String phone,
                         @RequestParam(required = false) String subject,
                         @RequestParam String content,
                         RedirectAttributes ra) {
        Message m = new Message();
        m.setName(name);
        m.setEmail(email);
        m.setPhone(phone);
        m.setSubject(subject);
        m.setContent(content);
        m.setRead(false);
        messageService.save(m);

        ra.addFlashAttribute("toast", "Your message has been sent. We'll get back to you soon.");
        return "redirect:/contact";
    }
}
