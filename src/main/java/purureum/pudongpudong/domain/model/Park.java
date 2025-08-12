package purureum.pudongpudong.domain.model;

import jakarta.persistence.*;
import lombok.*;
import purureum.pudongpudong.domain.model.enums.ParkDifficulty;
import purureum.pudongpudong.global.common.domain.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Park extends BaseEntity {
	
	@Id
	private String id;
	
	@Column(nullable = false, length = 50)
	private String placeName;
	
	private String addressName;
	
	private String roadAddressName;
	
	@Column(nullable = false, length = 100)
	private String description;
	
	@Enumerated(EnumType.STRING)
	private ParkDifficulty difficulty;
	
	@Column(columnDefinition = "DECIMAL(10, 7)", nullable = false)
	private Double longitude;
	
	@Column(columnDefinition = "DECIMAL(10, 7)", nullable = false)
	private Double latitude;
	
	@OneToMany(mappedBy = "fairy", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Fairy> fairies = new ArrayList<>();
}
