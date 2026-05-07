package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyReferenceDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VetServiceTest {

    @Mock
    private VetRepository vetRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private VetMapper vetMapper;

    @InjectMocks
    private VetServiceImpl vetService;

    private Vet james;
    private Specialty radiology;
    private VetResponseDto jamesDto;

    @BeforeEach
    void setUp() {
        radiology = new Specialty("radiology");
        radiology.setId(1);

        james = new Vet("James", "Carter");
        james.setId(1);
        james.setSpecialties(new HashSet<>(Set.of(radiology)));

        jamesDto = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    void getAllVets_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(james));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        List<VetResponseDto> result = vetService.getAllVets();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void getAllVets_returnsEmptyList() {
        when(vetRepository.findAll()).thenReturn(List.of());

        List<VetResponseDto> result = vetService.getAllVets();

        assertThat(result).isEmpty();
    }

    @Test
    void getVetById_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        VetResponseDto result = vetService.getVetById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.firstName()).isEqualTo("James");
        assertThat(result.specialties()).hasSize(1);
    }

    @Test
    void getVetById_throwsWhenNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.getVetById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet");
    }

    @Test
    void createVet_savesVetWithSpecialties() {
        VetRequestDto request = new VetRequestDto("New", "Vet",
            List.of(new SpecialtyReferenceDto(1, "radiology")));

        Vet newVet = new Vet("New", "Vet");
        newVet.setId(7);
        newVet.setSpecialties(Set.of(radiology));
        VetResponseDto newVetDto = new VetResponseDto(7, "New", "Vet",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(newVet);
        when(vetMapper.toResponseDto(newVet)).thenReturn(newVetDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.id()).isEqualTo(7);
        assertThat(result.firstName()).isEqualTo("New");
        assertThat(result.specialties()).hasSize(1);
    }

    @Test
    void createVet_throwsWhenSpecialtyNotFound() {
        VetRequestDto request = new VetRequestDto("New", "Vet",
            List.of(new SpecialtyReferenceDto(999, "unknown")));

        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.createVet(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty");
    }

    @Test
    void createVet_withEmptySpecialties() {
        VetRequestDto request = new VetRequestDto("New", "Vet", List.of());

        Vet newVet = new Vet("New", "Vet");
        newVet.setId(7);
        VetResponseDto newVetDto = new VetResponseDto(7, "New", "Vet", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(newVet);
        when(vetMapper.toResponseDto(newVet)).thenReturn(newVetDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void createVet_withNullSpecialties() {
        VetRequestDto request = new VetRequestDto("New", "Vet", null);

        Vet newVet = new Vet("New", "Vet");
        newVet.setId(7);
        VetResponseDto newVetDto = new VetResponseDto(7, "New", "Vet", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(newVet);
        when(vetMapper.toResponseDto(newVet)).thenReturn(newVetDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void updateVet_updatesAndReturns() {
        VetRequestDto request = new VetRequestDto("Updated", "Name",
            List.of(new SpecialtyReferenceDto(1, "radiology")));

        Vet updatedVet = new Vet("Updated", "Name");
        updatedVet.setId(1);
        updatedVet.setSpecialties(Set.of(radiology));
        VetResponseDto updatedDto = new VetResponseDto(1, "Updated", "Name",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(james)).thenReturn(updatedVet);
        when(vetMapper.toResponseDto(updatedVet)).thenReturn(updatedDto);

        VetResponseDto result = vetService.updateVet(1, request);

        assertThat(result.firstName()).isEqualTo("Updated");
        assertThat(result.lastName()).isEqualTo("Name");
    }

    @Test
    void updateVet_throwsWhenNotFound() {
        VetRequestDto request = new VetRequestDto("X", "Y", List.of());
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.updateVet(999, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteVet_deletesAndReturns() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        VetResponseDto result = vetService.deleteVet(1);

        assertThat(result.id()).isEqualTo(1);
        verify(vetRepository).delete(james);
    }

    @Test
    void deleteVet_throwsWhenNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.deleteVet(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_returnsMatchingVets() {
        when(vetRepository.findByNameContaining("james")).thenReturn(List.of(james));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        List<VetResponseDto> result = vetService.searchByName("james");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void findBySpecialty_returnsMatchingVets() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(james));
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        List<VetResponseDto> result = vetService.findBySpecialty("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByName_returnsEmptyWhenNoMatch() {
        when(vetRepository.findByNameContaining("xyz")).thenReturn(List.of());

        List<VetResponseDto> result = vetService.searchByName("xyz");

        assertThat(result).isEmpty();
    }
}
