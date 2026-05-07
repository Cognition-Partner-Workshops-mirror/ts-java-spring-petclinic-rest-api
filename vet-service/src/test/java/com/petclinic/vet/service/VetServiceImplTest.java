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
import java.time.Instant;
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
class VetServiceImplTest {

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
    private VetResponseDto responseDto;
    private VetRequestDto requestDto;

    @BeforeEach
    void setUp() {
        specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");
        specialty.setCreatedAt(Instant.now());
        specialty.setUpdatedAt(Instant.now());

        vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(new HashSet<>(Set.of(specialty)));
        vet.setCreatedAt(Instant.now());
        vet.setUpdatedAt(Instant.now());

        SpecialtyResponseDto specialtyResponseDto = new SpecialtyResponseDto(1, "radiology");
        responseDto = new VetResponseDto(1, "James", "Carter", List.of(specialtyResponseDto));
        requestDto = new VetRequestDto("James", "Carter", List.of(specialtyResponseDto));
    }

    @Test
    void listVets_returnsAll() {
        when(vetRepository.findAll()).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtos(any())).thenReturn(List.of(responseDto));

        List<VetResponseDto> result = vetService.listVets();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void getVet_found() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        VetResponseDto result = vetService.getVet(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void getVet_notFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.getVet(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 999");
    }

    @Test
    void addVet_success() {
        when(vetMapper.toEntity(requestDto)).thenReturn(vet);
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(vetRepository.save(vet)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        VetResponseDto result = vetService.addVet(requestDto);

        assertThat(result.firstName()).isEqualTo("James");
        verify(vetRepository).save(vet);
    }

    @Test
    void addVet_withEmptySpecialties() {
        VetRequestDto reqNoSpecs = new VetRequestDto("James", "Carter", List.of());
        Vet vetNoSpecs = new Vet();
        vetNoSpecs.setId(2);
        vetNoSpecs.setFirstName("James");
        vetNoSpecs.setLastName("Carter");

        VetResponseDto respNoSpecs = new VetResponseDto(2, "James", "Carter", List.of());
        when(vetMapper.toEntity(reqNoSpecs)).thenReturn(vetNoSpecs);
        when(vetRepository.save(vetNoSpecs)).thenReturn(vetNoSpecs);
        when(vetMapper.toResponseDto(vetNoSpecs)).thenReturn(respNoSpecs);

        VetResponseDto result = vetService.addVet(reqNoSpecs);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void addVet_withNullSpecialtyId() {
        SpecialtyResponseDto nullIdSpec = new SpecialtyResponseDto(null, "unknown");
        VetRequestDto reqNullSpec = new VetRequestDto("James", "Carter", List.of(nullIdSpec));
        Vet vetEntity = new Vet();
        vetEntity.setId(3);
        vetEntity.setFirstName("James");
        vetEntity.setLastName("Carter");

        VetResponseDto respDto = new VetResponseDto(3, "James", "Carter", List.of());
        when(vetMapper.toEntity(reqNullSpec)).thenReturn(vetEntity);
        when(vetRepository.save(vetEntity)).thenReturn(vetEntity);
        when(vetMapper.toResponseDto(vetEntity)).thenReturn(respDto);

        VetResponseDto result = vetService.addVet(reqNullSpec);

        assertThat(result.id()).isEqualTo(3);
    }

    @Test
    void addVet_specialtyNotFound() {
        SpecialtyResponseDto missingSpec = new SpecialtyResponseDto(999, "missing");
        VetRequestDto reqMissing = new VetRequestDto("James", "Carter", List.of(missingSpec));
        Vet vetEntity = new Vet();
        when(vetMapper.toEntity(reqMissing)).thenReturn(vetEntity);
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.addVet(reqMissing))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 999");
    }

    @Test
    void updateVet_success() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(vetRepository.save(vet)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        VetResponseDto result = vetService.updateVet(1, requestDto);

        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void updateVet_notFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.updateVet(999, requestDto))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteVet_success() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        VetResponseDto result = vetService.deleteVet(1);

        assertThat(result.id()).isEqualTo(1);
        verify(vetRepository).delete(vet);
    }

    @Test
    void deleteVet_notFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.deleteVet(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialtyId_returnsResults() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtos(any())).thenReturn(List.of(responseDto));

        List<VetResponseDto> result = vetService.findBySpecialtyId(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void findByLastName_returnsResults() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtos(any())).thenReturn(List.of(responseDto));

        List<VetResponseDto> result = vetService.findByLastName("Carter");

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyName_returnsResults() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtos(any())).thenReturn(List.of(responseDto));

        List<VetResponseDto> result = vetService.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void addVet_withNullSpecialtiesList() {
        VetRequestDto reqNullSpecs = new VetRequestDto("James", "Carter", null);
        Vet vetEntity = new Vet();
        vetEntity.setId(4);
        vetEntity.setFirstName("James");
        vetEntity.setLastName("Carter");

        VetResponseDto respDto = new VetResponseDto(4, "James", "Carter", List.of());
        when(vetMapper.toEntity(reqNullSpecs)).thenReturn(vetEntity);
        when(vetRepository.save(vetEntity)).thenReturn(vetEntity);
        when(vetMapper.toResponseDto(vetEntity)).thenReturn(respDto);

        VetResponseDto result = vetService.addVet(reqNullSpecs);

        assertThat(result.id()).isEqualTo(4);
    }
}
