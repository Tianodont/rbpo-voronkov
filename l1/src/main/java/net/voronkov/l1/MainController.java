package net.voronkov.l1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.stream.IntStream;

@RestController
public class MainController {
	@GetMapping("/hello")
	public String hello(@RequestParam String Z) {
		return "Слово" + Z;
	}

	@GetMapping("/numbers")
	public List<Integer> numbers(@RequestParam int n) {
		return IntStream.rangeClosed(1, n).boxed().toList();
	}
}
