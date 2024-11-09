package ru.practicum.explorewithme.ewm.gateway.privateapi.comments.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import ru.practicum.explorewithme.dto.comment.CommentRequest;
import ru.practicum.explorewithme.ewm.gateway.privateapi.comments.CommentClient;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class CommentGatewayController {

    private final CommentClient commentClient;

    @GetMapping("/comments/{comId}")
    public ResponseEntity<Object> getCommentByComId(@RequestParam Long userId,
                                                    @PathVariable Long comId) {
        return commentClient.getCommentByComId(userId, comId);
    }

    @GetMapping("/{userId}/comments")
    public ResponseEntity<Object> getCommentsByUserId(@PathVariable Long userId,
                                                      @RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "10") Integer size) {
        return commentClient.getCommentsById(userId, from, size);
    }

    @PostMapping("/{userId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createComment(@PathVariable Long userId,
                                                @RequestParam Long eventId,
                                                @RequestBody @Valid CommentRequest commentRequest) {
        return commentClient.createComment(userId, eventId, commentRequest);
    }

    @PatchMapping("/{userId}/comments/{comId}")
    public ResponseEntity<Object> updateComment(@PathVariable Long userId,
                                                @PathVariable Long comId,
                                                @RequestBody CommentRequest commentRequest) {
        return commentClient.updateComment(userId, comId, commentRequest);
    }

    @DeleteMapping("/{userId}/comments/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> deleteComment(@PathVariable Long userId,
                                                @PathVariable Long comId) {
        return commentClient.deleteComment(userId, comId);
    }
}
