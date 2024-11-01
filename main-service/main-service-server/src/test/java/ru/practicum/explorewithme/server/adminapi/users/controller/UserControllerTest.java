package ru.practicum.explorewithme.server.adminapi.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explorewithme.server.adminapi.users.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.explorewithme.server.TestData.createNewUserRequest;
import static ru.practicum.explorewithme.server.TestData.createUserDto;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void whenCreatedUserThenNewUserReturn() throws Exception {
        when(userService.createUser(any())).thenReturn(createUserDto());

        mockMvc.perform(post("/admin/users")
                        .content(mapper.writeValueAsString(createNewUserRequest()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(createUserDto().getId()), Long.class))
                .andExpect(jsonPath("$.name", is(createUserDto().getName()), String.class))
                .andExpect(jsonPath("$.email", is(createUserDto().getEmail()), String.class));
    }

    @Test
    public void whenCallGetUserThenReturnListUsersSize1() throws Exception {
        when(userService.getUsersByIds(anyList(), anyInt(), anyInt())).thenReturn(List.of(createUserDto()));

        mockMvc.perform(get("/admin/users?ids=1&from=0&size=10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(createUserDto().getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(createUserDto().getName()), String.class))
                .andExpect(jsonPath("$[0].email", is(createUserDto().getEmail()), String.class));
    }

    @Test
    public void whenDeleteUserThenReturn204() throws Exception {
        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/admin/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void checkInternalServerError500() throws Exception {
        when(userService.createUser(any())).thenThrow(new RuntimeException("Тест"));

        mockMvc.perform(post("/admin/users")
                        .content(mapper.writeValueAsString(createNewUserRequest()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", is("Тест"), String.class))
                .andExpect(jsonPath("$.reason", is("Произошла непредвиденная ошибка."), String.class))
                .andExpect(jsonPath("$.status", is(HttpStatus.INTERNAL_SERVER_ERROR.name()), String.class))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }
}
