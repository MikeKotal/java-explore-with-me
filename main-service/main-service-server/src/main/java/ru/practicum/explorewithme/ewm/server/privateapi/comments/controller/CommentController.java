package ru.practicum.explorewithme.ewm.server.privateapi.comments.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.dto.comment.CommentDto;
import ru.practicum.explorewithme.dto.comment.CommentRequest;
import ru.practicum.explorewithme.dto.comment.CommentShortDto;
import ru.practicum.explorewithme.ewm.server.services.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/comments/{comId}")
    public CommentDto getCommentByComId(@RequestParam Long userId,
                                        @PathVariable Long comId) {
        return commentService.getCommentByComId(userId, comId);
    }

    @GetMapping("/{userId}/comments")
    public List<CommentShortDto> getCommentsByUserId(@PathVariable Long userId,
                                                     @RequestParam Integer from,
                                                     @RequestParam Integer size) {
        return commentService.getCommentsByUserId(userId, from, size);
    }

    @PostMapping("/{userId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable Long userId,
                                    @RequestParam Long eventId,
                                    @RequestBody @Valid CommentRequest commentRequest) {
        return commentService.createComment(userId, eventId, commentRequest);
    }

    @PatchMapping("/{userId}/comments/{comId}")
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long comId,
                                    @RequestBody CommentRequest commentRequest) {
        return commentService.updateComment(userId, comId, commentRequest);
    }

    @DeleteMapping("/{userId}/comments/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long comId) {
        commentService.deleteComment(userId, comId);
    }
}
