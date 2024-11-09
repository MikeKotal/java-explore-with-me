package ru.practicum.explorewithme.ewm.server.privateapi.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.comment.CommentDto;
import ru.practicum.explorewithme.dto.comment.CommentRequest;
import ru.practicum.explorewithme.dto.comment.CommentShortDto;
import ru.practicum.explorewithme.dto.event.State;
import ru.practicum.explorewithme.ewm.server.dao.CommentRepository;
import ru.practicum.explorewithme.ewm.server.dao.EventRepository;
import ru.practicum.explorewithme.ewm.server.dao.UserRepository;
import ru.practicum.explorewithme.ewm.server.exceptions.ConditionException;
import ru.practicum.explorewithme.ewm.server.exceptions.NotFoundException;
import ru.practicum.explorewithme.ewm.server.mappers.CommentMapper;
import ru.practicum.explorewithme.ewm.server.models.Comment;
import ru.practicum.explorewithme.ewm.server.models.Event;
import ru.practicum.explorewithme.ewm.server.models.User;
import ru.practicum.explorewithme.ewm.server.services.CommentService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public CommentDto getCommentByComId(Long userId, Long comId) {
        log.info("Запрос от пользователя {} на получение комментария к событию {}", userId, comId);
        checkUserExists(userId);
        Comment comment = getCommentById(comId);
        checkCommentByOwner(userId, comment, "Детальную информацию по комментарию может просматривать только автор");
        log.info("Комментарий получен: {}", comment);
        return CommentMapper.mapToCommentDto(comment);
    }

    @Override
    public List<CommentShortDto> getCommentsByUserId(Long userId, Integer from, Integer size) {
        log.info("Запрос от пользователя {} на получение списка своих коментариев", userId);
        checkUserExists(userId);
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("createdAt").ascending());
        List<Comment> comments = commentRepository.findCommentsByCommenterId(userId, pageable);
        log.info("Список коментариев пользователя: {}", comments);
        return comments.stream()
                .map(CommentMapper::mapToCommentShortDto)
                .toList();
    }

    @Override
    public CommentDto createComment(Long userId, Long eventId, CommentRequest commentRequest) {
        log.info("Запрос от пользователя {} на создание комментария к событию {}", userId, eventId);
        User commenter = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с id {} отсутствует", userId);
                    return new NotFoundException(String.format("Пользователя с идентификатором = '%s' не найдено", userId));
                });
        Event event = getEventById(eventId);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Комментарии можно оставлять только опубликованным событиям");
        }
        Comment comment = CommentMapper.mapToComment(commentRequest, event, commenter);
        comment = commentRepository.save(comment);
        log.info("Комментарий успешно сохранен {}", comment);
        return CommentMapper.mapToCommentDto(comment);
    }

    @Override
    public CommentDto updateComment(Long userId, Long comId, CommentRequest commentRequest) {
        log.info("Запрос от пользователя {} на обновление комментария {}", userId, comId);
        checkUserExists(userId);
        Comment comment = getCommentById(comId);
        checkCommentByOwner(userId, comment, "Допускается обновлять комментарий только автору");
        comment = commentRepository.save(CommentMapper.mapToUpdateComment(commentRequest, comment));
        log.info("Комментарий успешно обновлен {}", comment);
        return CommentMapper.mapToCommentDto(comment);
    }

    @Override
    public void deleteComment(Long userId, Long comId) {
        log.info("Запрос от пользователя {} на удаление комментария {}", userId, comId);
        checkUserExists(userId);
        Comment comment = getCommentById(comId);
        checkCommentByOwner(userId, comment, "Удалить комментарий может только автор");
        commentRepository.delete(comment);
        log.info("Комментарий успешно удален");
    }

    private void checkUserExists(Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException(String.format("Пользователя с идентификатором = '%s' не найдено", ownerId));
        }
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Событие с id {} отсутствует", eventId);
                    return new NotFoundException(String.format("События с идентификатором = '%s' не найдено", eventId));
                });
    }

    private Comment getCommentById(Long comId) {
        return commentRepository.findById(comId)
                .orElseThrow(() -> {
                    log.error("Комментарий с id {} отсутствует", comId);
                    return new NotFoundException(String.format("Комментария с идентификатором = '%s' не найдено", comId));
                });
    }

    private void checkCommentByOwner(Long userId, Comment comment, String errorMsg) {
        if (!userId.equals(comment.getCommenter().getId())) {
            throw new ConditionException(errorMsg);
        }
    }
}
