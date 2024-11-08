package ru.practicum.explorewithme.ewm.gateway.adminapi.comments.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.ewm.gateway.adminapi.comments.AdminCommentClient;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class AdminCommentGatewayController {

    private final AdminCommentClient adminCommentClient;

    @DeleteMapping("/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> deleteCommentByAdmin(@PathVariable Long comId) {
        return adminCommentClient.deleteCommentByAdmin(comId);
    }
}
