package com.petclinic.vet.service;

import com.petclinic.vet.dto.request.SpecialtyRequest;
import com.petclinic.vet.dto.response.SpecialtyResponse;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.DuplicateResourceException;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpecialtyServiceTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private SpecialtyMapper specialtyMapper;

    @InjectMocks
    private SpecialtyService specialtyService;

    private Specialty radiology;
    private SpecialtyResponse radiologyResponse;

    @BeforeEach
    void setUp() {
        radiology = new Specialty(1, "radiology");
        radiologyResponse = new SpecialtyResponse(1, "radiology");
    }

    @Test
    void listSpecialties_returnsAll() {
        Specialty surgery = new Specialty(2, "surgery");
        SpecialtyResponse surgeryResponse = new SpecialtyResponse(2, "surgery");
        when(specialtyRepository.findAll()).thenReturn(List.of(radiology, surgery));
        when(specialtyMapper.toResponse(radiology)).thenReturn(radiologyResponse);
        when(specialtyMapper.toResponse(surgery)).thenReturn(surgeryResponse);

        List<SpecialtyResponse> result = specialtyService.listSpecialties();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
        assertThat(result.get(1).getName()).isEqualTo("surgery");
    }

    @Test
    void listSpecialties_emptyList() {
        when(specialtyRepository.findAll()).thenReturn(List.of());

        List<SpecialtyResponse> result = specialtyService.listSpecialties();

        assertThat(result).isEmpty();
    }

    @Test
    void getSpecialty_found() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toResponse(radiology)).thenReturn(radiologyResponse);

        SpecialtyResponse result = specialtyService.getSpecialty(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("radiology");
    }

    @Test
    void getSpecialty_notFound() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.getSpecialty(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty")
            .hasMessageContaining("99");
    }

    @Test
    void createSpecialty_success() {
        SpecialtyRequest request = new SpecialtyRequest("cardiology");
        Specialty entity = new Specialty(null, "cardiology");
        Specialty saved = new Specialty(4, "cardiology");
        SpecialtyResponse response = new SpecialtyResponse(4, "cardiology");

        when(specialtyRepository.existsByNameIgnoreCase("cardiology")).thenReturn(false);
        when(specialtyMapper.toEntity(request)).thenReturn(entity);
        when(specialtyRepository.save(entity)).thenReturn(saved);
        when(specialtyMapper.toResponse(saved)).thenReturn(response);

        SpecialtyResponse result = specialtyService.createSpecialty(request);

        assertThat(result.getId()).isEqualTo(4);
        assertThat(result.getName()).isEqualTo("cardiology");
    }

    @Test
    void createSpecialty_duplicateName() {
        SpecialtyRequest request = new SpecialtyRequest("radiology");
        when(specialtyRepository.existsByNameIgnoreCase("radiology")).thenReturn(true);

        assertThatThrownBy(() -> specialtyService.createSpecialty(request))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessageContaining("radiology");
    }

    @Test
    void updateSpecialty_success() {
        SpecialtyRequest request = new SpecialtyRequest("updated-radiology");
        Specialty saved = new Specialty(1, "updated-radiology");
        SpecialtyResponse response = new SpecialtyResponse(1, "updated-radiology");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.findByNameIgnoreCase("updated-radiology")).thenReturn(Optional.empty());
        when(specialtyRepository.save(radiology)).thenReturn(saved);
        when(specialtyMapper.toResponse(saved)).thenReturn(response);

        SpecialtyResponse result = specialtyService.updateSpecialty(1, request);

        assertThat(result.getName()).isEqualTo("updated-radiology");
        verify(specialtyMapper).updateEntity(request, radiology);
    }

    @Test
    void updateSpecialty_notFound() {
        SpecialtyRequest request = new SpecialtyRequest("updated");
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.updateSpecialty(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateSpecialty_duplicateNameDifferentId() {
        SpecialtyRequest request = new SpecialtyRequest("surgery");
        Specialty surgery = new Specialty(2, "surgery");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.findByNameIgnoreCase("surgery")).thenReturn(Optional.of(surgery));

        assertThatThrownBy(() -> specialtyService.updateSpecialty(1, request))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessageContaining("surgery");
    }

    @Test
    void updateSpecialty_sameNameSameId() {
        SpecialtyRequest request = new SpecialtyRequest("radiology");
        Specialty saved = new Specialty(1, "radiology");
        SpecialtyResponse response = new SpecialtyResponse(1, "radiology");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.findByNameIgnoreCase("radiology")).thenReturn(Optional.of(radiology));
        when(specialtyRepository.save(radiology)).thenReturn(saved);
        when(specialtyMapper.toResponse(saved)).thenReturn(response);

        SpecialtyResponse result = specialtyService.updateSpecialty(1, request);

        assertThat(result.getName()).isEqualTo("radiology");
    }

    @Test
    void deleteSpecialty_success() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toResponse(radiology)).thenReturn(radiologyResponse);

        SpecialtyResponse result = specialtyService.deleteSpecialty(1);

        assertThat(result.getId()).isEqualTo(1);
        verify(specialtyRepository).delete(radiology);
    }

    @Test
    void deleteSpecialty_notFound() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.deleteSpecialty(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_returnsMatches() {
        when(specialtyRepository.searchByName("rad")).thenReturn(List.of(radiology));
        when(specialtyMapper.toResponse(radiology)).thenReturn(radiologyResponse);

        List<SpecialtyResponse> result = specialtyService.searchByName("rad");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void searchByName_noMatches() {
        when(specialtyRepository.searchByName("xyz")).thenReturn(List.of());

        List<SpecialtyResponse> result = specialtyService.searchByName("xyz");

        assertThat(result).isEmpty();
    }
}
