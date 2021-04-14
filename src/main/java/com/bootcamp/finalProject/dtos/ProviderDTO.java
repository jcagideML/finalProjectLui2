package com.bootcamp.finalProject.dtos;

import com.bootcamp.finalProject.model.Part;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProviderDTO {

    private Long idProvider;
    @NotNull @Valid
    private String name;
    @NotNull @Valid
    private String address;
    @NotNull @Valid
    private String phone;
    @NotNull @Valid
    private String country;
}
