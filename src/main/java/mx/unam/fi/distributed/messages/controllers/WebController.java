package mx.unam.fi.distributed.messages.controllers;

import lombok.RequiredArgsConstructor;
import mx.unam.fi.distributed.messages.services.AppUserService;
import mx.unam.fi.distributed.messages.services.EngineerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final EngineerService engineerService;
    private final AppUserService appUserService;

    @Value("${app.server.node_n}")
    private int node_n;

    @RequestMapping(value = "/index")
    public String index(Model model) {

        var engineers = engineerService.getAll();
        var appUsers = appUserService.getAll();

        model.addAttribute("engineers", engineers);
        model.addAttribute("users", appUsers);
        model.addAttribute("node_n", node_n);

        return "index";
    }
}
