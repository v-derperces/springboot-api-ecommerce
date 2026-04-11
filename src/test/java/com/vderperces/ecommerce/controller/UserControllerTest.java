package com.vderperces.ecommerce.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vderperces.ecommerce.dto.user.ChangePasswordRequest;
import com.vderperces.ecommerce.exceptions.AuthException;
import com.vderperces.ecommerce.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void changePasswordUnauthenticatedShouldReturn401() throws Exception {
        ChangePasswordRequest dto = new ChangePasswordRequest();
        dto.setCurrentPassword("oldpass");
        dto.setNewPassword("newpass");

        mockMvc.perform(put("/api/v1/users/me/password")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void changePasswordWrongCurrentPasswordShouldReturn400() throws Exception {
        ChangePasswordRequest dto = new ChangePasswordRequest();
        dto.setCurrentPassword("wrongpassword");
        dto.setNewPassword("newpassword");

        doThrow(new AuthException("Current password incorrect", HttpStatus.BAD_REQUEST))
                .when(userService).changePassword(any(String.class), any(ChangePasswordRequest.class));

        mockMvc.perform(put("/api/v1/users/me/password")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Current password incorrect"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void changePasswordValidShouldReturnNoContent() throws Exception {
        ChangePasswordRequest dto = new ChangePasswordRequest();
        dto.setCurrentPassword("correctpassword");
        dto.setNewPassword("newpassword");

        // Simulate successful change
        doNothing().when(userService).changePassword(any(String.class), any(ChangePasswordRequest.class));

        mockMvc.perform(put("/api/v1/users/me/password")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }
}
