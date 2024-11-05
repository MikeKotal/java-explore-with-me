package ru.practicum.explorewithme.server.publicapi.compilations.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.explorewithme.server.adminapi.categories.model.Category;
import ru.practicum.explorewithme.server.adminapi.compilations.dao.CompilationRepository;
import ru.practicum.explorewithme.server.adminapi.compilations.model.Compilation;
import ru.practicum.explorewithme.server.adminapi.users.model.User;
import ru.practicum.explorewithme.server.exceptions.NotFoundException;
import ru.practicum.explorewithme.server.privateapi.events.model.Event;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class PublicCompilationServiceImplTest {

    @Autowired
    PublicCompilationService publicCompilationService;

    @MockBean
    CompilationRepository compilationRepository;

    @Mock
    Compilation compilation;

    @Mock
    Event event;

    @Mock
    Category category;

    @Mock
    User user;

    @Mock
    Page<Compilation> compilations;

    @Test
    public void checkGetCompilationsByPinnedAndPublicUser() {
        when(compilationRepository.findCompilationsByPinned(anyBoolean(), any(Pageable.class)))
                .thenReturn(List.of(compilation));
        when(compilation.getEvents()).thenReturn(Set.of(event));
        when(event.getCategory()).thenReturn(category);
        when(event.getInitiator()).thenReturn(user);
        publicCompilationService.getCompilationsByPublicUser(Boolean.TRUE, 0, 10);

        Mockito.verify(compilationRepository, Mockito.times(1))
                .findCompilationsByPinned(anyBoolean(), any(Pageable.class));
    }

    @Test
    public void checkGetAllCompilationsByPublicUser() {
        when(compilationRepository.findAll(any(Pageable.class))).thenReturn(compilations);
        publicCompilationService.getCompilationsByPublicUser(null, 0, 10);

        Mockito.verify(compilationRepository, Mockito.times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void checkGetCompilationByIdByUserId() {
        when(compilationRepository.findById(anyLong())).thenReturn(Optional.of(compilation));
        when(compilation.getEvents()).thenReturn(Set.of(event));
        when(event.getCategory()).thenReturn(category);
        when(event.getInitiator()).thenReturn(user);
        publicCompilationService.getCompilationByIdByPublicUser(1L);

        Mockito.verify(compilationRepository, Mockito.times(1)).findById(anyLong());
    }

    @Test
    public void checkEmptyCompilationValidation() {
        when(compilationRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> publicCompilationService.getCompilationByIdByPublicUser(1L));

        String expectedMessage = "Подборки с идентификатором = '1' не найдено";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }
}
