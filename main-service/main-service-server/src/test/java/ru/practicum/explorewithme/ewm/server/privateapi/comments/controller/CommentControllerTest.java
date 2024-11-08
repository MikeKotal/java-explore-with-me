package ru.practicum.explorewithme.ewm.server.privateapi.comments.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.explorewithme.dto.comment.CommentDto;
import ru.practicum.explorewithme.dto.comment.CommentShortDto;
import ru.practicum.explorewithme.dto.user.UserShortDto;
import ru.practicum.explorewithme.ewm.server.services.CommentService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.explorewithme.ewm.server.TestData.createCommentDto;
import static ru.practicum.explorewithme.ewm.server.TestData.createCommentRequest;
import static ru.practicum.explorewithme.ewm.server.TestData.createCommentShortDto;

@WebMvcTest(controllers = CommentController.class)
public class CommentControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    CommentService commentService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void whenGetCommentByComIdThenReturnComment() throws Exception {
        CommentDto commentDto = createCommentDto();
        when(commentService.getCommentByComId(anyLong(), anyLong())).thenReturn(commentDto);

        ResultActions resultActions = mockMvc.perform(get("/users/comments/{comId}", 1)
                        .param("userId", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        checkCommentDtoResponse(resultActions, commentDto);
    }

    @Test
    public void whenGetCommentsByUserIdThenReturnListSize1() throws Exception {
        CommentShortDto commentShortDto = createCommentShortDto();
        when(commentService.getCommentsByUserId(anyLong(), anyInt(), anyInt())).thenReturn(List.of(commentShortDto));

        mockMvc.perform(get("/users/{userId}/comments", 1)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].commenter", is(commentShortDto.getCommenter()), String.class))
                .andExpect(jsonPath("$[0].comment", is(commentShortDto.getComment()), String.class))
                .andExpect(jsonPath("$[0].createdAt", is(commentShortDto.getCreatedAt()), String.class));
    }

    @Test
    public void whenCreateCommentThenReturnNewComment() throws Exception {
        CommentDto commentDto = createCommentDto();
        when(commentService.createComment(anyLong(), anyLong(), any())).thenReturn(commentDto);

        ResultActions resultActions = mockMvc.perform(post("/users/{userId}/comments", 1)
                        .param("eventId", "1")
                        .content(mapper.writeValueAsString(createCommentRequest()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        checkCommentDtoResponse(resultActions, commentDto);
    }

    @Test
    public void whenUpdateCommentThenReturnUpdatedComment() throws Exception {
        CommentDto commentDto = createCommentDto();
        when(commentService.updateComment(anyLong(), anyLong(), any())).thenReturn(commentDto);

        ResultActions resultActions = mockMvc.perform(patch("/users/{userId}/comments/{comId}", 1, 1)
                        .content(mapper.writeValueAsString(createCommentRequest()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        checkCommentDtoResponse(resultActions, commentDto);
    }

    @Test
    public void whenDeleteCommentThenReturn204() throws Exception {
        doNothing().when(commentService).deleteComment(anyLong(), anyLong());

        mockMvc.perform(delete("/users/{userId}/comments/{comId}", 1, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    private void checkCommentDtoResponse(ResultActions resultActions, CommentDto commentDto) throws Exception {
        resultActions
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.commenter", is(commentDto.getCommenter()), UserShortDto.class))
                .andExpect(jsonPath("$.comment", is(commentDto.getComment()), String.class))
                .andExpect(jsonPath("$.createdAt", is(commentDto.getCreatedAt()), String.class));
    }
}
