package com.autowash.autowash_pro.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.autowash.autowash_pro.service.CustomerService;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    // Test Case 1: GET All Customers
    @Test
    public void testGetAllCustomers_Success() throws Exception {
        when(customerService.getAllCustomers()).thenReturn(new ArrayList<>());
         
        mockMvc.perform(get("/api/admin/customers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}