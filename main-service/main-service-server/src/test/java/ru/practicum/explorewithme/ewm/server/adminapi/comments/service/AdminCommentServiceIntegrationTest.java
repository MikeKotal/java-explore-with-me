package ru.practicum.explorewithme.ewm.server.adminapi.comments.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.comment.CommentDto;
import ru.practicum.explorewithme.dto.comment.CommentRequest;
import ru.practicum.explorewithme.ewm.server.models.Comment;
import ru.practicum.explorewithme.ewm.server.services.AdminCommentService;
import ru.practicum.explorewithme.ewm.server.services.CommentService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.explorewithme.ewm.server.TestData.createCommentRequest;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AdminCommentServiceIntegrationTest {

    private final EntityManager em;
    private final AdminCommentService adminCommentService;
    private final CommentService commentService;

    @Test
    public void checkSuccessDeleteCommentByAdmin() {
        CommentRequest commentRequest = createCommentRequest();
        CommentDto commentDto = commentService.createComment(2L, 2L, commentRequest);

        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.comment = :comment", Comment.class);
        Comment comment = query.setParameter("comment", commentDto.getComment()).getSingleResult();

        adminCommentService.deleteCommentByAdmin(comment.getId());

        int count = query.setParameter("comment", comment.getComment()).getResultList().size();
        assertThat(count, equalTo(0));
    }
}
