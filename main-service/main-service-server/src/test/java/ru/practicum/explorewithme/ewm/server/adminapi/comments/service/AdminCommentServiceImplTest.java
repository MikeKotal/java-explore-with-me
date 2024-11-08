package ru.practicum.explorewithme.ewm.server.adminapi.comments.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.explorewithme.ewm.server.dao.CommentRepository;
import ru.practicum.explorewithme.ewm.server.exceptions.NotFoundException;
import ru.practicum.explorewithme.ewm.server.models.Comment;
import ru.practicum.explorewithme.ewm.server.services.AdminCommentService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class AdminCommentServiceImplTest {

    @Autowired
    AdminCommentService adminCommentService;

    @MockBean
    CommentRepository commentRepository;

    @Mock
    Comment comment;

    @Test
    public void checkDeleteCommentByAdmin() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).delete(any());
        adminCommentService.deleteCommentByAdmin(1L);

        Mockito.verify(commentRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(commentRepository, Mockito.times(1)).delete(any());
    }

    @Test
    public void checkDeleteCommentNotFoundException() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> adminCommentService.deleteCommentByAdmin(1L));

        String expectedMessage = "Комментария с идентификатором = '1' не найдено";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }
}
