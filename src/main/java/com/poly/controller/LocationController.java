package com.poly.controller;

import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.poly.DAO.LocationDAO;
import com.poly.entity.Location;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
//@RequestMapping("/admin")
public class LocationController {
	
	@Autowired
	LocationDAO service;
	
	@GetMapping("admin/location")
	public String getMethodName(Model model,@ModelAttribute("location")Location location
			,Optional<Integer> page,@RequestParam("field") Optional<String> field) {
		
		Location cate = (location!=null) ? location:new Location();
		model.addAttribute("location", cate);
		
		Pageable pageable = paginate(page,field);
		Page<Location> categories = service.findAll(pageable);
		model.addAttribute("locations", categories);
		
		return "/views/items/location";
	}
	
	@PostMapping("admin/location")
	public String postMethodName(Model model,@ModelAttribute("location")Location location) {
		service.save(location);
		List<Location> categories = service.findAll();
		model.addAttribute("locations", categories);
		return "redirect:/admin/location";
	}
	
	@PostMapping("admin/location/edit/{id}")
	public String edit(@PathVariable("id")int id, RedirectAttributes attributes) {
		Location location = service.findById(id).get();
		attributes.addFlashAttribute("location", location);
		return "redirect:/admin/location";
	}
	
	@PostMapping("admin/location/remove/{id}")
	public String remove(@PathVariable("id")String id,@ModelAttribute("location")Location location) {
		service.delete(location);
		return "redirect:/admin/location";
	}
	
	@PostMapping("admin/location/reset")
	public String reset() {
		return "redirect:/admin/location";
	}
	
	@GetMapping("admin/location/list")
	public String getMethodName(@RequestParam("namevalue")String name,Model model) {
		
		Location category = new Location();
		model.addAttribute("location", category);
		
		Pageable pageable = PageRequest.of(0, 3);
		Page<Location> list = service.findByNameLike(name,pageable);
		model.addAttribute("locations", list);
		return "/views/items/location";
	}
	
	public Sort sort(@RequestParam("field") Optional<String> field) {
		String name = field.orElse("name");
		Direction direction = Direction.ASC;
		return  Sort.by(direction,name);
	}
	
	private Pageable paginate(Optional<Integer> page,@RequestParam("field") Optional<String> field) {
		int pageNumber = page.orElse(0);
		if (pageNumber < 0) {
			pageNumber = 0;
		}
		Sort sorting = sort(field); // Lấy thông tin sắp xếp từ tham số field
		return PageRequest.of(pageNumber, 3,sorting); // Điều chỉnh kích thước trang theo nhu cầu
	}
	
}
