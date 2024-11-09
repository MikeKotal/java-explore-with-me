package ru.practicum.explorewithme.ewm.gateway.adminapi.comments.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explorewithme.ewm.gateway.adminapi.comments.AdminCommentClient;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminCommentGatewayController.class)
public class AdminCommentGatewayControllerTest {

    @MockBean
    AdminCommentClient adminCommentClient;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void whenDeleteCommentByAdminThenReturn204() throws Exception {
        when(adminCommentClient.deleteCommentByAdmin(anyLong())).thenReturn(new ResponseEntity<>(HttpStatusCode.valueOf(204)));

        mockMvc.perform(delete("/admin/comments/{comId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
