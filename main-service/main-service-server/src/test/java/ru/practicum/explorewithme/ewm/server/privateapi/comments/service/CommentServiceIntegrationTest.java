package ru.practicum.explorewithme.ewm.server.privateapi.comments.service;

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
import ru.practicum.explorewithme.dto.comment.CommentShortDto;
import ru.practicum.explorewithme.ewm.server.models.Comment;
import ru.practicum.explorewithme.ewm.server.services.CommentService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static ru.practicum.explorewithme.ewm.server.TestData.createCommentRequest;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentServiceIntegrationTest {

    private final EntityManager em;
    private final CommentService commentService;

    @Test
    public void checkSuccessGetCommentByComId() {
        CommentDto commentDto = commentService.getCommentByComId(1L, 1L);

        assertThat(commentDto.getId(), equalTo(1L));
        assertThat(commentDto.getCommenter(), notNullValue());
        assertThat(commentDto.getCommenter().getName(), equalTo("Name"));
        assertThat(commentDto.getCommenter().getEmail(), equalTo("test@test.ru"));
        assertThat(commentDto.getComment(), equalTo("Супер комментарий"));
        assertThat(commentDto.getCreatedAt(), equalTo("2024-10-01 23:59:59"));
    }

    @Test
    public void checkSuccessGetCommentsByUserId() {
        List<CommentShortDto> commentShortDtos = commentService.getCommentsByUserId(1L, 0, 10);

        assertThat(commentShortDtos, notNullValue());
        assertThat(commentShortDtos.size(), equalTo(1));
        CommentShortDto commentShortDto = commentShortDtos.getFirst();

        assertThat(commentShortDto.getCommenter(), equalTo("Name"));
        assertThat(commentShortDto.getComment(), equalTo("Супер комментарий"));
        assertThat(commentShortDto.getCreatedAt(), equalTo("2024-10-01 23:59:59"));
    }

    @Test
    public void checkSuccessCreateComment() {
        CommentRequest commentRequest = createCommentRequest();
        CommentDto commentDto = commentService.createComment(1L, 2L, commentRequest);

        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.id = :id", Comment.class);
        Comment comment = query.setParameter("id", commentDto.getId()).getSingleResult();

        assertThat(comment.getId(), equalTo(commentDto.getId()));
        assertThat(comment.getEvent(), notNullValue());
        assertThat(comment.getEvent().getId(), equalTo(2L));
        assertThat(comment.getCommenter(), notNullValue());
        assertThat(comment.getCommenter().getId(), equalTo(1L));
        assertThat(comment.getComment(), equalTo(commentRequest.getComment()));
        assertThat(comment.getCreatedAt(), notNullValue());
    }

    @Test
    public void checkSuccessUpdateComment() {
        CommentRequest commentRequest = createCommentRequest();
        CommentDto commentDto = commentService.updateComment(1L, 1L, commentRequest);

        assertThat(commentDto.getComment(), equalTo(commentRequest.getComment()));
    }

    @Test
    public void checkSuccessDeleteComment() {
        CommentRequest commentRequest = createCommentRequest();
        CommentDto commentDto = commentService.createComment(2L, 2L, commentRequest);

        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.id = :id", Comment.class);
        Comment comment = query.setParameter("id", commentDto.getId()).getSingleResult();

        commentService.deleteComment(2L, comment.getId());

        int count = query.setParameter("id", comment.getId()).getResultList().size();
        assertThat(count, equalTo(0));
    }
}
