package com.helmes.task.controller;

import com.helmes.task.dto.FormDto;
import com.helmes.task.entity.Form;
import com.helmes.task.entity.Sector;
import com.helmes.task.service.FormService;
import com.helmes.task.service.SectorService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@AllArgsConstructor
@SessionAttributes("form")
public class FormController {

    private FormService formService;
    private SectorService sectorService;

    @ModelAttribute("form")
    public FormDto form() {
        return new FormDto();
    }
    @GetMapping("/")
    public String showForm(@ModelAttribute("form") FormDto formDto,
                           HttpSession session,
                           Model model) {

        Long id = getSessionFormId(session);

        if (id != null && isEmpty(formDto)) {
            Form entity = formService.findById(id);
            if (entity != null) {
                formDto.setName(entity.getName());
                formDto.setAgree(entity.isAgree());
                formDto.setSectors(entity.getSectors().stream().map(Sector::getId).toList());
            }
        }

        model.addAttribute("sectors", sectorService.buildHierarchy());
        return "newIndex";
    }

    @PostMapping("/")
    public String saveForm(@Valid @ModelAttribute("form") FormDto formDto,
                           BindingResult errors,
                           Model model,
                           HttpSession session,
                           RedirectAttributes ra) {

        if (errors.hasErrors()) {
            model.addAttribute("sectors", sectorService.buildHierarchy());
            return "newIndex";
        }

        Form saved = formService.saveOrUpdate(getSessionFormId(session), formDto);
        session.setAttribute("FORM_ID", saved.getId());

        ra.addFlashAttribute("success", "Saved successfully!");
        return "redirect:/";
    }

    private static Long getSessionFormId(HttpSession session) {
        Object o = session.getAttribute("FORM_ID");
        return (o instanceof Long l) ? l : null;
    }

    private static boolean isEmpty(FormDto f) {
        return (f.getName() == null || f.getName().isBlank())
                && (f.getSectors() == null || f.getSectors().isEmpty())
                && !f.isAgree();
    }
}
