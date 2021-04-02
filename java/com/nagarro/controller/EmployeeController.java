package com.nagarro.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nagarro.exception.EmployeeNotFoundException;
import com.nagarro.model.Employee;
import com.nagarro.repository.EmployeeRepository;

// tag::hateoas-imports[]
// end::hateoas-imports[]

@RestController
class EmployeeController {
	@Autowired
	private final EmployeeRepository repository;

	EmployeeController(EmployeeRepository repository) {
		this.repository = repository;
	}

	// Aggregate root

	// tag::get-aggregate-root[]
	@GetMapping("/employees")
	CollectionModel<EntityModel<Employee>> all() {

		List<EntityModel<Employee>> employees = repository.findAll().stream()
				.map(employee -> EntityModel.of(employee,
						linkTo(methodOn(EmployeeController.class).one(employee.getEmployee_code())).withSelfRel(),
						linkTo(methodOn(EmployeeController.class).all()).withRel("employees")))
				.collect(Collectors.toList());

		return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
	}
	// end::get-aggregate-root[]

	@PostMapping("/employees")
	Employee newEmployee(@Valid @RequestBody Employee newEmployee) {
		return repository.save(newEmployee);
	}

	// Single item

	// tag::get-single-item[]
	@GetMapping("/employees/{id}")
	EntityModel<Employee> one(@PathVariable Long id) {

		Employee employee = repository.findById(id) //
				.orElseThrow(() -> new EmployeeNotFoundException(id));

		return EntityModel.of(employee, //
				linkTo(methodOn(EmployeeController.class).one(id)).withSelfRel(),
				linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
	}
	// end::get-single-item[]

	@PutMapping("/employees/{id}")
	Employee replaceEmployee(@RequestBody Employee newEmployee,@Valid @PathVariable Long id) {

		return repository.findById(id) //
				.map(employee -> {
					employee.setName(newEmployee.getName());
					employee.setEmail(newEmployee.getEmail());
					employee.setLocation(newEmployee.getLocation());
					employee.setDob(newEmployee.getDob());
					return repository.save(employee);
				}) //
				.orElseGet(() -> {
					newEmployee.setName(newEmployee.getName());
					newEmployee.setEmail(newEmployee.getEmail());
					newEmployee.setLocation(newEmployee.getLocation());
					newEmployee.setDob(newEmployee.getDob());
					return repository.save(newEmployee);
				});
	}

	@DeleteMapping("/employees/{id}")
	void deleteEmployee(@PathVariable Long id) {
		repository.deleteById(id);
	}
}
