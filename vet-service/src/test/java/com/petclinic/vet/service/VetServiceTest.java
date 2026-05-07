package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.SpecialtyEntity;
import com.petclinic.vet.entity.VetEntity;
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
    private VetService service;

    private VetEntity vetEntity;
    private SpecialtyEntity specialtyEntity;
    private VetResponseDto vetResponseDto;

    @BeforeEach
    void setUp() {
        specialtyEntity = new SpecialtyEntity();
        specialtyEntity.setId(1);
        specialtyEntity.setName("radiology");

        vetEntity = new VetEntity();
        vetEntity.setId(1);
        vetEntity.setFirstName("James");
        vetEntity.setLastName("Carter");
        vetEntity.setSpecialties(new HashSet<>(Set.of(specialtyEntity)));

        vetResponseDto = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    void listAll_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(vetEntity));
        when(vetMapper.toResponseDto(vetEntity)).thenReturn(vetResponseDto);

        List<VetResponseDto> result = service.listAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void listAll_returnsEmptyList() {
        when(vetRepository.findAll()).thenReturn(List.of());

        List<VetResponseDto> result = service.listAll();

        assertThat(result).isEmpty();
    }

    @Test
    void getById_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vetEntity));
        when(vetMapper.toResponseDto(vetEntity)).thenReturn(vetResponseDto);

        VetResponseDto result = service.getById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.firstName()).isEqualTo("James");
        assertThat(result.specialties()).hasSize(1);
    }

    @Test
    void getById_throwsNotFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }

    @Test
    void create_savesVetWithSpecialties() {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of(1));

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialtyEntity));
        when(vetRepository.save(any(VetEntity.class))).thenReturn(vetEntity);
        when(vetMapper.toResponseDto(vetEntity)).thenReturn(
            new VetResponseDto(2, "Helen", "Leary", List.of(new SpecialtyResponseDto(1, "radiology"))));

        VetResponseDto result = service.create(request);

        assertThat(result.firstName()).isEqualTo("Helen");
        verify(vetRepository).save(any(VetEntity.class));
    }

    @Test
    void create_savesVetWithEmptySpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of());
        VetResponseDto emptySpecDto = new VetResponseDto(1, "James", "Carter", List.of());

        when(vetRepository.save(any(VetEntity.class))).thenReturn(vetEntity);
        when(vetMapper.toResponseDto(vetEntity)).thenReturn(emptySpecDto);

        VetResponseDto result = service.create(request);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void create_throwsWhenSpecialtyNotFound() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(99));

        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty");
    }

    @Test
    void update_updatesVet() {
        VetRequestDto request = new VetRequestDto("James", "Updated", List.of(1));
        VetResponseDto updatedDto = new VetResponseDto(1, "James", "Updated",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(vetEntity));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialtyEntity));
        when(vetRepository.save(vetEntity)).thenReturn(vetEntity);
        when(vetMapper.toResponseDto(vetEntity)).thenReturn(updatedDto);

        VetResponseDto result = service.update(1, request);

        assertThat(result.lastName()).isEqualTo("Updated");
    }

    @Test
    void update_throwsNotFound() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of());

        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_throwsWhenSpecialtyNotFound() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(99));

        when(vetRepository.findById(1)).thenReturn(Optional.of(vetEntity));
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(1, request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty");
    }

    @Test
    void delete_removesAndReturns() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vetEntity));
        when(vetMapper.toResponseDto(vetEntity)).thenReturn(vetResponseDto);

        VetResponseDto result = service.delete(1);

        assertThat(result.id()).isEqualTo(1);
        verify(vetRepository).delete(vetEntity);
    }

    @Test
    void delete_throwsNotFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByLastName_returnsMatching() {
        when(vetRepository.searchByLastName("Cart")).thenReturn(List.of(vetEntity));
        when(vetMapper.toResponseDto(vetEntity)).thenReturn(vetResponseDto);

        List<VetResponseDto> result = service.searchByLastName("Cart");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).lastName()).isEqualTo("Carter");
    }

    @Test
    void findBySpecialtyId_returnsMatching() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(vetEntity));
        when(vetMapper.toResponseDto(vetEntity)).thenReturn(vetResponseDto);

        List<VetResponseDto> result = service.findBySpecialtyId(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyName_returnsMatching() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(vetEntity));
        when(vetMapper.toResponseDto(vetEntity)).thenReturn(vetResponseDto);

        List<VetResponseDto> result = service.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void create_savesVetWithNullSpecialtyIds() {
        VetRequestDto request = new VetRequestDto("James", "Carter", null);
        VetResponseDto emptySpecDto = new VetResponseDto(1, "James", "Carter", List.of());

        when(vetRepository.save(any(VetEntity.class))).thenReturn(vetEntity);
        when(vetMapper.toResponseDto(vetEntity)).thenReturn(emptySpecDto);

        VetResponseDto result = service.create(request);

        assertThat(result.specialties()).isEmpty();
    }
}
