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
    private VetMapper mapper;

    @InjectMocks
    private VetService service;

    private Vet vetEntity;
    private Specialty specialtyEntity;
    private VetResponseDto vetResponse;
    private VetRequestDto vetRequest;

    @BeforeEach
    void setUp() {
        specialtyEntity = new Specialty(1, "radiology");

        vetEntity = new Vet();
        vetEntity.setId(1);
        vetEntity.setFirstName("James");
        vetEntity.setLastName("Carter");
        vetEntity.setSpecialties(new HashSet<>(Set.of(specialtyEntity)));

        SpecialtyResponseDto specDto = new SpecialtyResponseDto(1, "radiology");
        vetResponse = new VetResponseDto(1, "James", "Carter", List.of(specDto));
        vetRequest = new VetRequestDto("James", "Carter", List.of(specDto));
    }

    @Test
    void listAll_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(vetEntity));
        when(mapper.toResponseDto(vetEntity)).thenReturn(vetResponse);

        List<VetResponseDto> result = service.listAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void getById_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vetEntity));
        when(mapper.toResponseDto(vetEntity)).thenReturn(vetResponse);

        VetResponseDto result = service.getById(1);

        assertThat(result.id()).isEqualTo(1);
    }

    @Test
    void getById_throwsWhenNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_savesVetWithSpecialties() {
        when(mapper.toEntity(vetRequest)).thenReturn(vetEntity);
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialtyEntity));
        when(vetRepository.save(vetEntity)).thenReturn(vetEntity);
        when(mapper.toResponseDto(vetEntity)).thenReturn(vetResponse);

        VetResponseDto result = service.create(vetRequest);

        assertThat(result.firstName()).isEqualTo("James");
        verify(vetRepository).save(vetEntity);
    }

    @Test
    void create_withEmptySpecialties() {
        VetRequestDto noSpecReq = new VetRequestDto("James", "Carter", List.of());
        when(mapper.toEntity(noSpecReq)).thenReturn(vetEntity);
        when(vetRepository.save(vetEntity)).thenReturn(vetEntity);
        when(mapper.toResponseDto(vetEntity)).thenReturn(vetResponse);

        VetResponseDto result = service.create(noSpecReq);

        assertThat(result).isNotNull();
    }

    @Test
    void create_withNullSpecialtyId() {
        SpecialtyResponseDto nullIdSpec = new SpecialtyResponseDto(null, "unknown");
        VetRequestDto req = new VetRequestDto("James", "Carter", List.of(nullIdSpec));
        when(mapper.toEntity(req)).thenReturn(vetEntity);
        when(vetRepository.save(vetEntity)).thenReturn(vetEntity);
        when(mapper.toResponseDto(vetEntity)).thenReturn(vetResponse);

        VetResponseDto result = service.create(req);

        assertThat(result).isNotNull();
    }

    @Test
    void create_throwsWhenSpecialtyNotFound() {
        SpecialtyResponseDto badSpec = new SpecialtyResponseDto(999, "nonexistent");
        VetRequestDto req = new VetRequestDto("James", "Carter", List.of(badSpec));
        when(mapper.toEntity(req)).thenReturn(vetEntity);
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(req))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty");
    }

    @Test
    void update_modifiesVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vetEntity));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialtyEntity));
        when(vetRepository.save(vetEntity)).thenReturn(vetEntity);
        when(mapper.toResponseDto(vetEntity)).thenReturn(vetResponse);

        VetResponseDto result = service.update(1, vetRequest);

        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void update_throwsWhenNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999, vetRequest))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_removesAndReturns() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vetEntity));
        when(mapper.toResponseDto(vetEntity)).thenReturn(vetResponse);

        VetResponseDto result = service.delete(1);

        assertThat(result.id()).isEqualTo(1);
        verify(vetRepository).delete(vetEntity);
    }

    @Test
    void delete_throwsWhenNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_delegatesToRepository() {
        when(vetRepository.findByNameContaining("james")).thenReturn(List.of(vetEntity));
        when(mapper.toResponseDto(vetEntity)).thenReturn(vetResponse);

        List<VetResponseDto> result = service.searchByName("james");

        assertThat(result).hasSize(1);
    }

    @Test
    void filterBySpecialty_delegatesToRepository() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(vetEntity));
        when(mapper.toResponseDto(vetEntity)).thenReturn(vetResponse);

        List<VetResponseDto> result = service.filterBySpecialty("radiology");

        assertThat(result).hasSize(1);
    }
}
