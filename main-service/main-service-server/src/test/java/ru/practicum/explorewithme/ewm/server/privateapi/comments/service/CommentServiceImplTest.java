package ru.practicum.explorewithme.ewm.server.privateapi.comments.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import ru.practicum.explorewithme.dto.comment.CommentRequest;
import ru.practicum.explorewithme.dto.event.State;
import ru.practicum.explorewithme.ewm.server.dao.CommentRepository;
import ru.practicum.explorewithme.ewm.server.dao.EventRepository;
import ru.practicum.explorewithme.ewm.server.dao.UserRepository;
import ru.practicum.explorewithme.ewm.server.exceptions.ConditionException;
import ru.practicum.explorewithme.ewm.server.exceptions.NotFoundException;
import ru.practicum.explorewithme.ewm.server.models.Comment;
import ru.practicum.explorewithme.ewm.server.models.Event;
import ru.practicum.explorewithme.ewm.server.models.User;
import ru.practicum.explorewithme.ewm.server.services.CommentService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

    @Autowired
    CommentService commentService;

    @MockBean
    CommentRepository commentRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    EventRepository eventRepository;

    @Mock
    CommentRequest commentRequest;

    @Mock
    Comment comment;

    @Mock
    Event event;

    @Mock
    User user;

    @Test
    public void checkGetCommentByComId() {
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(comment.getCommenter()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(comment.getCreatedAt()).thenReturn(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        commentService.getCommentByComId(1L, 1L);

        Mockito.verify(userRepository, Mockito.times(1)).existsById(anyLong());
        Mockito.verify(commentRepository, Mockito.times(1)).findById(anyLong());
    }

    @Test
    public void checkGetCommentByUserId() {
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(commentRepository.findCommentsByCommenterId(anyLong(), any(Pageable.class))).thenReturn(List.of(comment));
        when(comment.getCommenter()).thenReturn(user);
        when(comment.getCreatedAt()).thenReturn(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        commentService.getCommentsByUserId(1L, 0, 10);

        Mockito.verify(userRepository, Mockito.times(1)).existsById(anyLong());
        Mockito.verify(commentRepository, Mockito.times(1))
                .findCommentsByCommenterId(anyLong(), any(Pageable.class));
    }

    @Test
    public void checkCreateComment() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(event.getState()).thenReturn(State.PUBLISHED);
        when(commentRepository.save(any())).thenReturn(comment);
        when(comment.getCommenter()).thenReturn(user);
        when(comment.getCreatedAt()).thenReturn(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        commentService.createComment(1L, 1L, commentRequest);

        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(eventRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(commentRepository, Mockito.times(1)).save(any());
    }

    @Test
    public void checkCommentInvalidEventStatus() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(event.getState()).thenReturn(State.CANCELED);

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> commentService.createComment(1L, 1L, commentRequest));

        String expectedMessage = "Комментарии можно оставлять только опубликованным событиям";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }

    @Test
    public void checkUpdateComment() {
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(comment.getCommenter()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(commentRepository.save(any())).thenReturn(comment);
        when(comment.getCommenter()).thenReturn(user);
        when(comment.getCreatedAt()).thenReturn(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        commentService.updateComment(1L, 1L, commentRequest);

        Mockito.verify(userRepository, Mockito.times(1)).existsById(anyLong());
        Mockito.verify(commentRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(commentRepository, Mockito.times(1)).save(any());
    }

    @Test
    public void checkExceptionUpdateCommentFromOtherUser() {
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(comment.getCommenter()).thenReturn(user);
        when(user.getId()).thenReturn(2L);

        ConditionException exception = Assertions.assertThrows(ConditionException.class,
                () -> commentService.updateComment(1L, 1L, commentRequest));

        String expectedMessage = "Допускается обновлять комментарий только автору";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }

    @Test
    public void checkDeleteUser() {
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(comment.getCommenter()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        doNothing().when(commentRepository).delete(any());
        commentService.deleteComment(1L, 1L);

        Mockito.verify(userRepository, Mockito.times(1)).existsById(anyLong());
        Mockito.verify(commentRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(commentRepository, Mockito.times(1)).delete(any());
    }

    @Test
    public void checkExceptionDeleteCommentFromOtherUser() {
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(comment.getCommenter()).thenReturn(user);
        when(user.getId()).thenReturn(2L);

        ConditionException exception = Assertions.assertThrows(ConditionException.class,
                () -> commentService.deleteComment(1L, 1L));

        String expectedMessage = "Удалить комментарий может только автор";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }
}
