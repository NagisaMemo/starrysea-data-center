package top.starrysea.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import top.starrysea.vo.RootResource;

@RestController
public class RootController {

	@GetMapping("/")
	public RootResource index() {
		return new RootResource();
	}
}
