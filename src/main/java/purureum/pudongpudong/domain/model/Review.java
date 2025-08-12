package purureum.pudongpudong.domain.model;

import jakarta.persistence.*;
import lombok.*;
import purureum.pudongpudong.global.common.domain.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Review  extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private Double rating;
	
	@Column(nullable = false)
	private String content;
	
	@Column(nullable = false)
	private Integer likeCount;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "park_id", nullable = false)
	private Park park;
	
	// TODO: 유저 매핑
}
