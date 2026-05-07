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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
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
    private VetResponseDto vetDto;
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(new HashSet<>(Set.of(radiology)));

        vetDto = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    void findAll_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(anyList())).thenReturn(List.of(vetDto));

        List<VetResponseDto> result = vetService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void findById_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        VetResponseDto result = vetService.findById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getFirstName()).isEqualTo("James");
        assertThat(result.getSpecialties()).hasSize(1);
    }

    @Test
    void findById_throwsWhenNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.findById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 999");
    }

    @Test
    void create_savesVetWithSpecialties() {
        VetRequestDto request = new VetRequestDto("Helen", "Leary",
            List.of(new SpecialtyReferenceDto(1, "radiology")));

        when(specialtyRepository.findByNameIn(anySet())).thenReturn(List.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result).isNotNull();
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void create_savesVetWithoutSpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of());

        Vet noSpecVet = new Vet();
        noSpecVet.setId(2);
        noSpecVet.setFirstName("James");
        noSpecVet.setLastName("Carter");
        VetResponseDto noSpecDto = new VetResponseDto(2, "James", "Carter", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(noSpecVet);
        when(vetMapper.toResponseDto(noSpecVet)).thenReturn(noSpecDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result.getSpecialties()).isEmpty();
    }

    @Test
    void create_savesVetWithNullSpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter", null);

        Vet noSpecVet = new Vet();
        noSpecVet.setId(2);
        noSpecVet.setFirstName("James");
        noSpecVet.setLastName("Carter");
        VetResponseDto noSpecDto = new VetResponseDto(2, "James", "Carter", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(noSpecVet);
        when(vetMapper.toResponseDto(noSpecVet)).thenReturn(noSpecDto);

        VetResponseDto result = vetService.create(request);

        assertThat(result).isNotNull();
    }

    @Test
    void update_updatesVet() {
        VetRequestDto request = new VetRequestDto("Helen", "Leary",
            List.of(new SpecialtyReferenceDto(1, "radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findByNameIn(anySet())).thenReturn(List.of(radiology));
        when(vetRepository.save(vet)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        VetResponseDto result = vetService.update(1, request);

        assertThat(result).isNotNull();
        verify(vetRepository).save(vet);
    }

    @Test
    void update_throwsWhenNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.update(999, new VetRequestDto("A", "B", List.of())))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_removesVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));

        vetService.delete(1);

        verify(vetRepository).delete(vet);
    }

    @Test
    void delete_throwsWhenNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialtyName_returnsFilteredVets() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(anyList())).thenReturn(List.of(vetDto));

        List<VetResponseDto> result = vetService.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByName_returnsByName() {
        when(vetRepository.searchByName("James")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(anyList())).thenReturn(List.of(vetDto));

        List<VetResponseDto> result = vetService.searchByName("James");

        assertThat(result).hasSize(1);
    }
}
