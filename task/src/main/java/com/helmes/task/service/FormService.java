package com.helmes.task.service;

import com.helmes.task.dto.FormDto;
import com.helmes.task.entity.Form;
import com.helmes.task.repository.FormRepository;
import com.helmes.task.repository.SectorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FormService {

    private final SectorRepository sectorRepository;
    private final FormRepository formRepository;

    public Form saveOrUpdate(Long existingId, FormDto dto) {
        Form form = (existingId != null)
                ? formRepository.findById(existingId).orElse(new Form())
                : new Form();

        form.setName(dto.getName());
        form.setAgree(dto.isAgree());
        form.setSectors(sectorRepository.findAllById(dto.getSectors()));

        return formRepository.save(form);
    }

    public Form findById(Long id) {
        return formRepository.findById(id).orElse(null);
    }
}
