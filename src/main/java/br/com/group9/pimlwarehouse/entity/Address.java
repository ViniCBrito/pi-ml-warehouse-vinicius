package br.com.group9.pimlwarehouse.entity;

import lombok.*;

import javax.persistence.Embeddable;

@NoArgsConstructor
@AllArgsConstructor
@Builder @Getter @Setter
@Embeddable
public class Address {
    private String address;
    private Integer addressNumber;
    private String addressComplement;
    private String addressDistrict;
    private String postalCode;
    private String city;
    private String state;
    private String country;
    private String placeId;

    @Override
    public String toString() {
        return address +
                ", " + addressNumber +
                " - " + addressComplement +
                " - " + addressDistrict +
                " - " + postalCode +
                " - " + city +
                " - " + state +
                " - " + country;
    }

    public String searchQuery() {
        return address +
                ", " + addressNumber +
                " - " + addressDistrict +
                " - " + postalCode +
                " - " + city +
                " - " + state +
                " - " + country;
    }
}
