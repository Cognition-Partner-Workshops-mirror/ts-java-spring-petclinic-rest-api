package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    private Vet vet;
    private Specialty specialty;
    private VetResponseDto vetResponseDto;
    private VetRequestDto vetRequestDto;

    @BeforeEach
    void setUp() {
        specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");

        vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(List.of(specialty));

        SpecialtyResponseDto specialtyDto = new SpecialtyResponseDto(1, "radiology");
        vetResponseDto = new VetResponseDto(1, "James", "Carter", List.of(specialtyDto));
        vetRequestDto = new VetRequestDto("James", "Carter", List.of(specialtyDto));
    }

    @Test
    void listVets_returnsAll() {
        when(vetRepository.findAll()).thenReturn(List.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        List<VetResponseDto> result = vetService.listVets();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void getVet_found() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.getVet(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getFirstName()).isEqualTo("James");
    }

    @Test
    void getVet_notFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.getVet(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vet");
    }

    @Test
    void addVet_success() {
        when(vetMapper.toEntity(vetRequestDto)).thenReturn(vet);
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(vetRepository.save(vet)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.addVet(vetRequestDto);

        assertThat(result.getFirstName()).isEqualTo("James");
        verify(vetRepository).save(vet);
    }

    @Test
    void addVet_withEmptySpecialties() {
        VetRequestDto emptySpecRequest = new VetRequestDto("James", "Carter", new ArrayList<>());
        Vet newVet = new Vet();
        newVet.setId(2);
        newVet.setFirstName("James");
        newVet.setLastName("Carter");
        VetResponseDto emptyResponse = new VetResponseDto(2, "James", "Carter", new ArrayList<>());

        when(vetMapper.toEntity(emptySpecRequest)).thenReturn(newVet);
        when(vetRepository.save(newVet)).thenReturn(newVet);
        when(vetMapper.toResponseDto(newVet)).thenReturn(emptyResponse);

        VetResponseDto result = vetService.addVet(emptySpecRequest);

        assertThat(result.getSpecialties()).isEmpty();
    }

    @Test
    void addVet_withNullSpecialties() {
        VetRequestDto nullSpecRequest = new VetRequestDto("James", "Carter", null);
        Vet newVet = new Vet();
        newVet.setId(3);
        newVet.setFirstName("James");
        newVet.setLastName("Carter");
        VetResponseDto nullResponse = new VetResponseDto(3, "James", "Carter", new ArrayList<>());

        when(vetMapper.toEntity(nullSpecRequest)).thenReturn(newVet);
        when(vetRepository.save(newVet)).thenReturn(newVet);
        when(vetMapper.toResponseDto(newVet)).thenReturn(nullResponse);

        VetResponseDto result = vetService.addVet(nullSpecRequest);

        assertThat(result.getFirstName()).isEqualTo("James");
    }

    @Test
    void addVet_specialtyNotFound() {
        SpecialtyResponseDto badSpecDto = new SpecialtyResponseDto(999, "nonexistent");
        VetRequestDto badRequest = new VetRequestDto("James", "Carter", List.of(badSpecDto));

        when(vetMapper.toEntity(badRequest)).thenReturn(new Vet());
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.addVet(badRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Specialty");
    }

    @Test
    void updateVet_success() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(vetRepository.save(vet)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.updateVet(1, vetRequestDto);

        assertThat(result.getFirstName()).isEqualTo("James");
        verify(vetMapper).updateEntity(eq(vetRequestDto), eq(vet));
    }

    @Test
    void updateVet_notFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.updateVet(99, vetRequestDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteVet_success() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.deleteVet(1);

        assertThat(result.getId()).isEqualTo(1);
        verify(vetRepository).delete(vet);
    }

    @Test
    void deleteVet_notFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.deleteVet(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialtyName_returnsMatches() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        List<VetResponseDto> result = vetService.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByName_returnsMatches() {
        when(vetRepository.findByNameContaining("Carter")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        List<VetResponseDto> result = vetService.searchByName("Carter");

        assertThat(result).hasSize(1);
    }

    @Test
    void addVet_withNullIdInSpecialty() {
        SpecialtyResponseDto nullIdSpec = new SpecialtyResponseDto(null, "surgery");
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(nullIdSpec));
        Vet newVet = new Vet();
        newVet.setFirstName("James");
        newVet.setLastName("Carter");
        VetResponseDto response = new VetResponseDto(4, "James", "Carter", new ArrayList<>());

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(vetRepository.save(newVet)).thenReturn(newVet);
        when(vetMapper.toResponseDto(newVet)).thenReturn(response);

        VetResponseDto result = vetService.addVet(request);

        assertThat(result.getFirstName()).isEqualTo("James");
    }
}
