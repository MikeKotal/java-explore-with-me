package ru.practicum.explorewithme.ewm.server.services;

import ru.practicum.explorewithme.dto.comment.CommentDto;
import ru.practicum.explorewithme.dto.comment.CommentRequest;
import ru.practicum.explorewithme.dto.comment.CommentShortDto;

import java.util.List;

public interface CommentService {

    CommentDto getCommentByComId(Long userId, Long comId);

    List<CommentShortDto> getCommentsByUserId(Long userId, Integer from, Integer size);

    CommentDto createComment(Long userId, Long eventId, CommentRequest commentRequest);

    CommentDto updateComment(Long userId, Long comId, CommentRequest commentRequest);

    void deleteComment(Long userId, Long comId);
}
