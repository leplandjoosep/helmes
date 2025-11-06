package com.helmes.task.service;

import com.helmes.task.entity.Sector;
import com.helmes.task.repository.SectorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectorServiceTest {

    @Mock
    private SectorRepository sectorRepository;

    @InjectMocks
    private SectorService sectorService;

    @Test
    void buildHierarchySetsDepthAndParentChildOrder() {
        Sector root = new Sector();
        root.setId(1L);
        root.setName("Root");
        root.setParentId(null);

        Sector childA = new Sector();
        childA.setId(2L);
        childA.setName("Child A");
        childA.setParentId(1L);

        Sector childB = new Sector();
        childB.setId(3L);
        childB.setName("Child B");
        childB.setParentId(1L);

        Sector grandchild = new Sector();
        grandchild.setId(4L);
        grandchild.setName("Grandchild");
        grandchild.setParentId(3L);

        when(sectorRepository.findAll()).thenReturn(Arrays.asList(root, childA, childB, grandchild));

        List<Sector> ordered = sectorService.buildHierarchy();

        assertThat(ordered).hasSize(4);

        assertThat(ordered.get(0).getId()).isEqualTo(1L);
        assertThat(ordered.get(0).getDepth()).isEqualTo(0);

        assertThat(ordered.get(1).getDepth()).isEqualTo(1);
        assertThat(ordered.get(2).getDepth()).isEqualTo(1);
        assertThat(ordered.get(3).getDepth()).isEqualTo(2);

        assertThat(ordered.get(1).getParentId()).isEqualTo(1L);
        assertThat(ordered.get(2).getParentId()).isEqualTo(1L);
        assertThat(ordered.get(3).getParentId()).isEqualTo(3L);
    }
}
