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

    // Test Case 2: GET Customer Vehicles
    @Test
    public void testGetCustomerVehicles_Success() throws Exception {
        UUID customerId = UUID.randomUUID();
        
        when(customerService.getVehiclesByCustomerId(customerId)).thenReturn(new ArrayList<>());
        
        mockMvc.perform(get("/api/admin/customers/" + customerId + "/vehicles")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // Test Case 3: GET Customer Wash History
    @Test
    public void testGetCustomerHistory_Success() throws Exception {
        UUID customerId = UUID.randomUUID();
        
        when(customerService.getBookingHistoryByCustomerId(customerId)).thenReturn(new ArrayList<>());
        
        mockMvc.perform(get("/api/admin/customers/" + customerId + "/history")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // Test Case 4: POST Create New Customer
    @Test
    public void testCreateCustomer_Success() throws Exception {
        String customerJson = "{\"name\":\"Nguyen Van A\",\"phone\":\"0987654321\",\"email\":\"nva@gmail.com\"}";

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/admin/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJson))
                .andExpect(status().isOk()); 
    }

    // Test Case 5: PUT Update Existing Customer
    @Test
    public void testUpdateCustomer_Success() throws Exception {
        UUID customerId = UUID.randomUUID();
    
        String updatedCustomerJson = "{\"name\":\"Nguyen Van A Updated\",\"phone\":\"0987654321\",\"email\":\"nva_updated@gmail.com\"}";

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/admin/customers/" + customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedCustomerJson))
                .andExpect(status().isOk()); 
    }
}