package ru.practicum.explorewithme.ewm.server.adminapi.compilations.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import ru.practicum.explorewithme.dto.compilation.NewCompilationRequest;
import ru.practicum.explorewithme.dto.compilation.UpdateCompilationRequest;
import ru.practicum.explorewithme.ewm.server.models.Category;
import ru.practicum.explorewithme.ewm.server.dao.CompilationRepository;
import ru.practicum.explorewithme.ewm.server.models.Compilation;
import ru.practicum.explorewithme.ewm.server.models.User;
import ru.practicum.explorewithme.ewm.server.dao.EventRepository;
import ru.practicum.explorewithme.ewm.server.models.Event;
import ru.practicum.explorewithme.ewm.server.services.CompilationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class CompilationServiceImplTest {

    @Autowired
    CompilationService compilationService;

    @MockBean
    CompilationRepository compilationRepository;

    @MockBean
    EventRepository eventRepository;

    @Mock
    NewCompilationRequest newCompilationRequest;

    @Mock
    UpdateCompilationRequest updateCompilationRequest;

    @Mock
    Compilation compilation;

    @Mock
    Event event;

    @Mock
    Category category;

    @Mock
    User user;

    @Test
    public void checkCreateCompilation() {
        when(newCompilationRequest.getEvents()).thenReturn(List.of(1L));
        when(eventRepository.findEventsByIdIn(anyList(), any(Sort.class))).thenReturn(Set.of(event));
        when(compilationRepository.save(any())).thenReturn(compilation);
        when(compilation.getEvents()).thenReturn(Set.of(event));
        when(event.getCategory()).thenReturn(category);
        when(event.getInitiator()).thenReturn(user);
        compilationService.createCompilation(newCompilationRequest);

        Mockito.verify(eventRepository, Mockito.times(1)).findEventsByIdIn(anyList(), any(Sort.class));
        Mockito.verify(compilationRepository, Mockito.times(1)).save(any());
    }

    @Test
    public void checkUpdateCompilation() {
        when(compilationRepository.findById(anyLong())).thenReturn(Optional.of(compilation));
        when(event.getEventDate()).thenReturn(LocalDateTime.now());
        when(compilation.getEvents()).thenReturn(Set.of(event));
        when(updateCompilationRequest.getEvents()).thenReturn(List.of(1L));
        when(eventRepository.findEventsByIdIn(anyList(), any(Sort.class))).thenReturn(Set.of(event));
        when(compilationRepository.save(any())).thenReturn(compilation);
        when(event.getCategory()).thenReturn(category);
        when(event.getInitiator()).thenReturn(user);
        compilationService.updateCompilation(1L, updateCompilationRequest);

        Mockito.verify(compilationRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(eventRepository, Mockito.times(1)).findEventsByIdIn(anyList(), any(Sort.class));
        Mockito.verify(compilationRepository, Mockito.times(1)).save(any());
    }

    @Test
    public void checkDeleteCompilation() {
        when(compilationRepository.findById(anyLong())).thenReturn(Optional.of(compilation));
        Mockito.doNothing().when(compilationRepository).delete(any());
        compilationService.deleteCompilation(1L);

        Mockito.verify(compilationRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(compilationRepository, Mockito.times(1)).delete(any());
    }
}
