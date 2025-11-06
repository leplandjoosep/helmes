package com.helmes.task.service;

import com.helmes.task.entity.Sector;
import com.helmes.task.repository.SectorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SectorService {

    private SectorRepository sectorRepository;

    public List<Sector> buildHierarchy() {
        List<Sector> all = sectorRepository.findAll();
        all.sort(Comparator.comparing(Sector::getName, String.CASE_INSENSITIVE_ORDER));
        Map<Long, Sector> map = all.stream().collect(Collectors.toMap(Sector::getId, s -> s));

        List<Sector> roots = new ArrayList<>();

        for (Sector s : all) {
            if (s.getParentId() == null) {
                roots.add(s);
            } else {
                Sector parent = map.get(s.getParentId());
                if (parent != null) {
                    parent.getChildren().add(s);
                } else {
                    roots.add(s);
                }
            }
        }

        List<Sector> ordered = new ArrayList<>();
        addRecursively(ordered, roots, 0);
        return ordered;
    }

    private void addRecursively(List<Sector> ordered, List<Sector> items, int depth) {
        for (Sector s : items) {
            s.setDepth(depth);
            ordered.add(s);
            if (!s.getChildren().isEmpty()) {
                addRecursively(ordered, s.getChildren(), depth + 1);
            }
        }
    }
}
