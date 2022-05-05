package br.com.group9.pimlwarehouse.dto;

import br.com.group9.pimlwarehouse.entity.Address;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@NoArgsConstructor
@AllArgsConstructor
@Builder @Getter @Setter
public class AddressDTO {
    @NotBlank(message = "Informar a rua do endereço do Armazém.")
    private String address;

    @Positive(message = "Informar um número válido de endereço do Armazém.")
    private Integer addressNumber;

    @NotNull(message = "Informar o complemento do endereço do Armazém.")
    private String addressComplement;

    @NotBlank(message = "Informar o bairro do endereço do Armazém.")
    private String addressDistrict;

    @NotBlank(message = "Informar o CEP do endereço do Armazém.")
    private String postalCode;

    @NotBlank(message = "Informar a cidade do endereço do Armazém.")
    private String city;

    @NotBlank(message = "Informar o estado do endereço do Armazém.")
    private String state;

    @NotBlank(message = "Informar o país do endereço do Armazém.")
    private String country;

    public Address map() {
        return Address.builder()
                .address(this.address)
                .addressNumber(this.addressNumber)
                .addressComplement(this.addressComplement)
                .addressDistrict(this.addressDistrict)
                .postalCode(this.postalCode)
                .city(this.city)
                .state(this.state)
                .country(this.country)
                .build();
    }

    public static AddressDTO map(Address address) {
        return AddressDTO.builder()
                .address(address.getAddress())
                .addressNumber(address.getAddressNumber())
                .addressComplement(address.getAddressComplement())
                .addressDistrict(address.getAddressDistrict())
                .postalCode(address.getPostalCode())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .build();
    }
}
