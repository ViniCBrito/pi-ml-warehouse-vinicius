package br.com.group9.pimlwarehouse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class AgentDTO {
    private Long id;
    private String name;
    private String cpf;
    private String username;
    private String password;
    private String email;
    private String role;
    private Long warehouseId;

    public NewAgentDTO convert() {
        return NewAgentDTO.builder()
                .id(this.id)
                .name(this.name)
                .cpf(this.cpf)
                .username(this.username)
                .email(this.email)
                .role(this.role)
                .warehouseId(this.warehouseId)
                .build();
    }
}
