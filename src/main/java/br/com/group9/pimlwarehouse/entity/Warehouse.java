package br.com.group9.pimlwarehouse.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@Entity
@Table(name = "warehouse")
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
    private List<Section> sections;

    public void setPlaceId(String placeId) {
        this.address.setPlaceId(placeId);
    }
}
