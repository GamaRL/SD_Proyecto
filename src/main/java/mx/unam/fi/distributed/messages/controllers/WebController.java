package mx.unam.fi.distributed.messages.controllers;

import lombok.RequiredArgsConstructor;
import mx.unam.fi.distributed.messages.services.AppUserService;
import mx.unam.fi.distributed.messages.services.DeviceService;
import mx.unam.fi.distributed.messages.services.EngineerService;
import mx.unam.fi.distributed.messages.services.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class WebController {

    private static final Logger log = LoggerFactory.getLogger(WebController.class);
    private final EngineerService engineerService;
    private final AppUserService appUserService;
    private final DeviceService deviceService;
    private final TicketService ticketService;

    @Value("${app.server.node_n}")
    private int node_n;

    @GetMapping(value = "/")
    public String index(Model model) {

        var engineers = engineerService.getAll();
        var appUsers = appUserService.getAll();

        model.addAttribute("engineers", engineers);
        model.addAttribute("users", appUsers);
        model.addAttribute("node_n", node_n);

        return "index";
    }

    @GetMapping(value = "/engineer/{engineer_id}")
    public String engineer(
            @PathVariable(name = "engineer_id") Long engineerId,
            Model model) {

        var engineer = engineerService.findById(engineerId);
        var openTickets = engineer.getTickets().stream().filter(t -> t.getCloseDate() == null).collect(Collectors.toList());

        model.addAttribute("engineer", engineer);
        model.addAttribute("node_n", node_n);
        model.addAttribute("open_tickets", openTickets);

        return "engineer";
    }

    @GetMapping(value = "/user/{user_id}")
    public String user(
            @PathVariable(name = "user_id") Long userId,
            Model model) {

        var user = appUserService.findById(userId);
        var availableDevices = deviceService.findAllAvailable();

        model.addAttribute("user", user);
        model.addAttribute("available_devices", availableDevices);
        model.addAttribute("node_n", node_n);

        return "user";
    }

    @PostMapping(value = "/device/create")
    public String createDevice(
            @RequestParam("deviceName") String name,
            @RequestParam("deviceType") String type,
            @RequestParam("deviceSerial") String serialNumber,
            @RequestParam("engineerId") Long engineerId) {

        deviceService.create(name, type, serialNumber);

        return String.format("redirect:/engineer/%s", engineerId);
    }

    @PostMapping(value = "/ticket/create")
    public String createTicket(
            @RequestParam("ticketDescription") String description,
            @RequestParam("ticketDeviceId") Long deviceId,
            @RequestParam("userId") Long userId) {

        ticketService.create(description, userId, deviceId);

        return String.format("redirect:/user/%s", userId);
    }

    @GetMapping(value = "/ticket/close/{ticket_id}")
    public String createTicket(
            @PathVariable("ticket_id") Long ticketId) {

        ticketService.close(ticketId);
        var ticket = ticketService.findById(ticketId);

        return String.format("redirect:/engineer/%s", ticket.getEngineer().getId());
    }
}
