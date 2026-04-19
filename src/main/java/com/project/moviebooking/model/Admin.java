package com.project.moviebooking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Admin model for OOAD architecture completeness.
 * This class extends BaseUser to demonstrate inheritance and LSP explicitly.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Admin extends BaseUser {

    private String adminCode;

    @Override
    public String getRole() {
        return "ADMIN";
    }
}
