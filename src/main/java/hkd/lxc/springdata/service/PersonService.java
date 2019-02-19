package hkd.lxc.springdata.service;


import hkd.lxc.springdata.entities.Person;
import hkd.lxc.springdata.repository.PersonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonService {
	
	@Autowired
	private PersonRepository personRepository;
	
	@Transactional
	public void updatePersonEmail(String email, Integer id){
		personRepository.updatePersonEmail(id, email);
	}
	
	@Transactional
	public void save(Iterable<Person> entities) {
		personRepository.save(entities);
	}

}
