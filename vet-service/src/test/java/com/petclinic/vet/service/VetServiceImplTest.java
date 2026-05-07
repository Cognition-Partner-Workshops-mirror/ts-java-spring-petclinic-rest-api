package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;
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
    private VetServiceImpl service;

    private Vet james;
    private VetDto jamesDto;
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        james = new Vet();
        james.setId(1);
        james.setFirstName("James");
        james.setLastName("Carter");
        james.setSpecialties(Set.of(radiology));

        jamesDto = new VetDto(1, "James", "Carter",
            List.of(new SpecialtyDto(1, "radiology")));
    }

    @Test
    void findAll_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(james));
        when(vetMapper.toDtoList(any())).thenReturn(List.of(jamesDto));

        List<VetDto> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void findById_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetMapper.toDto(james)).thenReturn(jamesDto);

        VetDto result = service.findById(1);

        assertThat(result.getFirstName()).isEqualTo("James");
    }

    @Test
    void findById_throwsNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_savesVetWithSpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyDto(1, "radiology")));

        Vet newVet = new Vet();
        newVet.setFirstName("James");
        newVet.setLastName("Carter");

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(newVet)).thenReturn(james);
        when(vetMapper.toDto(james)).thenReturn(jamesDto);

        VetDto result = service.create(request);

        assertThat(result.getFirstName()).isEqualTo("James");
        assertThat(result.getSpecialties()).hasSize(1);
    }

    @Test
    void create_withEmptySpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of());
        Vet newVet = new Vet();
        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(vetRepository.save(newVet)).thenReturn(james);
        when(vetMapper.toDto(james)).thenReturn(jamesDto);

        VetDto result = service.create(request);

        assertThat(result).isNotNull();
    }

    @Test
    void create_withNullSpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter", null);
        Vet newVet = new Vet();
        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(vetRepository.save(newVet)).thenReturn(james);
        when(vetMapper.toDto(james)).thenReturn(jamesDto);

        VetDto result = service.create(request);

        assertThat(result).isNotNull();
    }

    @Test
    void create_withNullSpecialtyId() {
        SpecialtyDto specDto = new SpecialtyDto(null, "radiology");
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(specDto));
        Vet newVet = new Vet();
        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(vetRepository.save(newVet)).thenReturn(james);
        when(vetMapper.toDto(james)).thenReturn(jamesDto);

        VetDto result = service.create(request);

        assertThat(result).isNotNull();
    }

    @Test
    void create_throwsWhenSpecialtyNotFound() {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyDto(999, "unknown")));
        Vet newVet = new Vet();

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_updatesVet() {
        VetRequestDto request = new VetRequestDto("Helen", "Leary",
            List.of(new SpecialtyDto(1, "radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(james)).thenReturn(james);
        VetDto updatedDto = new VetDto(1, "Helen", "Leary",
            List.of(new SpecialtyDto(1, "radiology")));
        when(vetMapper.toDto(james)).thenReturn(updatedDto);

        VetDto result = service.update(1, request);

        assertThat(result.getFirstName()).isEqualTo("Helen");
        verify(vetMapper).updateEntity(request, james);
    }

    @Test
    void update_throwsNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999,
            new VetRequestDto("x", "y", List.of())))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_deletesAndReturns() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(james));
        when(vetMapper.toDto(james)).thenReturn(jamesDto);

        VetDto result = service.delete(1);

        assertThat(result.getFirstName()).isEqualTo("James");
        verify(vetRepository).delete(james);
    }

    @Test
    void delete_throwsNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialtyId_returnsVets() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(james));
        when(vetMapper.toDtoList(any())).thenReturn(List.of(jamesDto));

        List<VetDto> result = service.findBySpecialtyId(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void findByLastName_returnsVets() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter")).thenReturn(List.of(james));
        when(vetMapper.toDtoList(any())).thenReturn(List.of(jamesDto));

        List<VetDto> result = service.findByLastName("Carter");

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyName_returnsVets() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(james));
        when(vetMapper.toDtoList(any())).thenReturn(List.of(jamesDto));

        List<VetDto> result = service.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
    }
}
