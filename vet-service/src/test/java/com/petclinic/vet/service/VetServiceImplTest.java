package com.petclinic.vet.service;

import com.petclinic.vet.dto.request.SpecialtyRequestDto;
import com.petclinic.vet.dto.request.VetRequestDto;
import com.petclinic.vet.dto.response.SpecialtyResponseDto;
import com.petclinic.vet.dto.response.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VetServiceImplTest {

    @Mock
    private VetRepository vetRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private VetMapper vetMapper;

    @InjectMocks
    private VetServiceImpl vetService;

    @Test
    void findAll_returnsAllVets() {
        Vet vet = buildVet(1, "James", "Carter");
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetRepository.findAll()).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtos(anyCollection())).thenReturn(List.of(dto));

        List<VetResponseDto> result = vetService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void findById_existingVet_returnsVet() {
        Vet vet = buildVet(1, "James", "Carter");
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(dto);

        VetResponseDto result = vetService.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void findById_nonExisting_throwsNotFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.findById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet");
    }

    @Test
    void create_withSpecialties_resolvesAndSaves() {
        SpecialtyRequestDto specDto = new SpecialtyRequestDto(null, "radiology");
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of(specDto));

        Vet mappedVet = buildVet(null, "Helen", "Leary");
        Specialty radiology = buildSpecialty(1, "radiology");
        Vet savedVet = buildVet(1, "Helen", "Leary");
        savedVet.setSpecialties(Set.of(radiology));

        SpecialtyResponseDto specResponseDto = new SpecialtyResponseDto(1, "radiology");
        VetResponseDto responseDto = new VetResponseDto(1, "Helen", "Leary", List.of(specResponseDto));

        when(vetMapper.toEntity(request)).thenReturn(mappedVet);
        when(specialtyRepository.findByNameInIgnoreCase(Set.of("radiology"))).thenReturn(List.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(savedVet);
        when(vetMapper.toResponseDto(savedVet)).thenReturn(responseDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.specialties()).hasSize(1);
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void create_withEmptySpecialties_savesWithoutSpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of());
        Vet mappedVet = buildVet(null, "James", "Carter");
        Vet savedVet = buildVet(1, "James", "Carter");
        VetResponseDto responseDto = new VetResponseDto(1, "James", "Carter", List.of());

        when(vetMapper.toEntity(request)).thenReturn(mappedVet);
        when(vetRepository.save(any(Vet.class))).thenReturn(savedVet);
        when(vetMapper.toResponseDto(savedVet)).thenReturn(responseDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void create_withNullSpecialties_savesWithoutSpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter", null);
        Vet mappedVet = buildVet(null, "James", "Carter");
        Vet savedVet = buildVet(1, "James", "Carter");
        VetResponseDto responseDto = new VetResponseDto(1, "James", "Carter", List.of());

        when(vetMapper.toEntity(request)).thenReturn(mappedVet);
        when(vetRepository.save(any(Vet.class))).thenReturn(savedVet);
        when(vetMapper.toResponseDto(savedVet)).thenReturn(responseDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result.specialties()).isEmpty();
    }

    // Verifies that unknown specialty names are rejected with IllegalArgumentException
    @Test
    void create_withUnknownSpecialty_throwsIllegalArgument() {
        SpecialtyRequestDto specDto = new SpecialtyRequestDto(null, "cardiology");
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of(specDto));

        Vet mappedVet = buildVet(null, "Helen", "Leary");
        when(vetMapper.toEntity(request)).thenReturn(mappedVet);
        when(specialtyRepository.findByNameInIgnoreCase(Set.of("cardiology"))).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> vetService.create(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("cardiology");
    }

    @Test
    void update_existingVet_updatesFields() {
        SpecialtyRequestDto specDto = new SpecialtyRequestDto(null, "surgery");
        VetRequestDto request = new VetRequestDto("Updated", "Name", List.of(specDto));
        Vet existingVet = buildVet(1, "James", "Carter");
        Specialty surgery = buildSpecialty(2, "surgery");
        Vet savedVet = buildVet(1, "Updated", "Name");
        savedVet.setSpecialties(Set.of(surgery));
        VetResponseDto responseDto = new VetResponseDto(1, "Updated", "Name",
            List.of(new SpecialtyResponseDto(2, "surgery")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(existingVet));
        when(specialtyRepository.findByNameInIgnoreCase(Set.of("surgery"))).thenReturn(List.of(surgery));
        when(vetRepository.save(any(Vet.class))).thenReturn(savedVet);
        when(vetMapper.toResponseDto(savedVet)).thenReturn(responseDto);

        VetResponseDto result = vetService.update(1, request);

        assertThat(result.firstName()).isEqualTo("Updated");
        assertThat(result.lastName()).isEqualTo("Name");
    }

    @Test
    void update_nonExisting_throwsNotFound() {
        VetRequestDto request = new VetRequestDto("A", "B", List.of());
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.update(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingVet_deletesAndReturns() {
        Vet vet = buildVet(1, "James", "Carter");
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(dto);

        VetResponseDto result = vetService.delete(1);

        assertThat(result.id()).isEqualTo(1);
        verify(vetRepository).delete(vet);
    }

    @Test
    void delete_nonExisting_throwsNotFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_delegatesToRepository() {
        Vet vet = buildVet(1, "James", "Carter");
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetRepository.searchByName("jam")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtos(anyCollection())).thenReturn(List.of(dto));

        List<VetResponseDto> result = vetService.searchByName("jam");

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialty_delegatesToRepository() {
        Vet vet = buildVet(1, "Helen", "Leary");
        VetResponseDto dto = new VetResponseDto(1, "Helen", "Leary", List.of());
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtos(anyCollection())).thenReturn(List.of(dto));

        List<VetResponseDto> result = vetService.findBySpecialty("radiology");

        assertThat(result).hasSize(1);
    }

    private Vet buildVet(Integer id, String firstName, String lastName) {
        Vet vet = new Vet();
        vet.setId(id);
        vet.setFirstName(firstName);
        vet.setLastName(lastName);
        return vet;
    }

    private Specialty buildSpecialty(Integer id, String name) {
        Specialty specialty = new Specialty();
        specialty.setId(id);
        specialty.setName(name);
        return specialty;
    }
}
