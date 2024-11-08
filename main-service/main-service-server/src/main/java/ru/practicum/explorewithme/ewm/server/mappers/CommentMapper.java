package ru.practicum.explorewithme.ewm.server.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.explorewithme.dto.comment.CommentDto;
import ru.practicum.explorewithme.dto.comment.CommentRequest;
import ru.practicum.explorewithme.dto.comment.CommentShortDto;
import ru.practicum.explorewithme.ewm.server.models.Comment;
import ru.practicum.explorewithme.ewm.server.models.Event;
import ru.practicum.explorewithme.ewm.server.models.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static ru.practicum.explorewithme.dto.validators.DateTimeFormatValidator.FORMATTER;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static CommentShortDto mapToCommentShortDto(Comment comment) {
        log.info("Comment для Short в маппер: {}", comment);
        CommentShortDto commentShortDto = CommentShortDto.builder()
                .commenter(comment.getCommenter().getName())
                .comment(comment.getComment())
                .createdAt(comment.getCreatedAt().format(FORMATTER))
                .build();
        log.info("CommentShortDto из маппера: {}", commentShortDto);
        return commentShortDto;
    }

    public static CommentDto mapToCommentDto(Comment comment) {
        log.info("Comment в маппер: {}", comment);
        CommentDto commentDto = CommentDto.builder()
                .id(comment.getId())
                .commenter(UserMapper.mapToUserShortDto(comment.getCommenter()))
                .comment(comment.getComment())
                .createdAt(comment.getCreatedAt().format(FORMATTER))
                .build();
        log.info("CommentDto из маппера: {}", commentDto);
        return commentDto;
    }

    public static Comment mapToComment(CommentRequest commentRequest, Event event, User commenter) {
        log.info("CommentRequest в маппер: {}", commentRequest);
        Comment comment = Comment.builder()
                .event(event)
                .commenter(commenter)
                .comment(commentRequest.getComment())
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
        log.info("Comment из маппера: {}", comment);
        return comment;
    }

    public static Comment mapToUpdateComment(CommentRequest commentRequest, Comment oldComment) {
        log.info("CommentRequest в маппер {} для обновления {}", commentRequest, oldComment);
        oldComment.setComment(commentRequest.getComment());
        log.info("Обновленный Comment: {}", oldComment);
        return oldComment;
    }

    public static Set<CommentShortDto> mapToCommentSet(Set<Comment> comments) {
        log.info("Список комментариев в маппер: {}", comments);
        Set<CommentShortDto> commentDtos = comments.stream()
                .map(CommentMapper::mapToCommentShortDto)
                .collect(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator.comparing(CommentShortDto::getCreatedAt))));
        log.info("Список комментариев из маппера: {}", commentDtos);
        return commentDtos;
    }
}
