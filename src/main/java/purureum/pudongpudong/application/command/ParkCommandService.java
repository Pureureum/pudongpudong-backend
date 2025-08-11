package purureum.pudongpudong.application.command;

import purureum.pudongpudong.domain.model.Park;
import reactor.core.publisher.Flux;

public interface ParkCommandService {
	Flux<Park> saveAllParks();
}
