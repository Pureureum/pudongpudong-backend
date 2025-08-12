package purureum.pudongpudong.global.apiPayload.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageResponse<T> {
	
	private List<T> content;
	private long totalElements;
	private boolean isLast;
	private int totalPages;
	
	public static <T> PageResponse<T> fromPage (Page<T> page) {
		return new PageResponse<>(
				page.getContent(),
				page.getTotalElements(),
				page.isLast(),
				page.getTotalPages()
		);
	}
}
