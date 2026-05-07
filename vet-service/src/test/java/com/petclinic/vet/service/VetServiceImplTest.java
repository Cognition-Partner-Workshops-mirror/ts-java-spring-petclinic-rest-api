package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
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
    private VetResponseDto responseDto;
    private VetRequestDto requestDto;

    @BeforeEach
    void setUp() {
        Specialty radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(Set.of(radiology));

        responseDto = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyDto(1, "radiology")));

        requestDto = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyDto(1, "radiology")));
    }

    @Test
    void findAll_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtos(anyList())).thenReturn(List.of(responseDto));

        List<VetResponseDto> result = vetService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void findById_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        VetResponseDto result = vetService.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void findById_throwsWhenNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.findById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found");
    }

    @Test
    void create_savesAndReturnsVet() {
        when(vetMapper.toEntity(requestDto)).thenReturn(vet);
        when(specialtyRepository.findByNameInIgnoreCase(anySet())).thenReturn(List.of());
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        VetResponseDto result = vetService.create(requestDto);

        assertThat(result.firstName()).isEqualTo("James");
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void create_withNullSpecialties() {
        VetRequestDto noSpecReq = new VetRequestDto("James", "Carter", null);
        Vet noSpecVet = new Vet();
        noSpecVet.setId(2);
        noSpecVet.setFirstName("James");
        noSpecVet.setLastName("Carter");

        when(vetMapper.toEntity(noSpecReq)).thenReturn(noSpecVet);
        when(vetRepository.save(any(Vet.class))).thenReturn(noSpecVet);
        when(vetMapper.toResponseDto(noSpecVet)).thenReturn(
            new VetResponseDto(2, "James", "Carter", List.of()));

        VetResponseDto result = vetService.create(noSpecReq);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void create_withEmptySpecialties() {
        VetRequestDto emptySpecReq = new VetRequestDto("James", "Carter", List.of());
        Vet emptySpecVet = new Vet();
        emptySpecVet.setId(3);
        emptySpecVet.setFirstName("James");
        emptySpecVet.setLastName("Carter");

        when(vetMapper.toEntity(emptySpecReq)).thenReturn(emptySpecVet);
        when(vetRepository.save(any(Vet.class))).thenReturn(emptySpecVet);
        when(vetMapper.toResponseDto(emptySpecVet)).thenReturn(
            new VetResponseDto(3, "James", "Carter", List.of()));

        VetResponseDto result = vetService.create(emptySpecReq);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void update_updatesAndReturnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findByNameInIgnoreCase(anySet())).thenReturn(List.of());
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(responseDto);

        VetResponseDto result = vetService.update(1, requestDto);

        assertThat(result.firstName()).isEqualTo("James");
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void update_throwsWhenNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.update(999, requestDto))
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
    void findBySpecialty_returnsMatchingVets() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtos(anyList())).thenReturn(List.of(responseDto));

        List<VetResponseDto> result = vetService.findBySpecialty("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByName_returnsMatchingVets() {
        when(vetRepository.findByNameContainingIgnoreCase("james")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtos(anyList())).thenReturn(List.of(responseDto));

        List<VetResponseDto> result = vetService.searchByName("james");

        assertThat(result).hasSize(1);
    }
}
