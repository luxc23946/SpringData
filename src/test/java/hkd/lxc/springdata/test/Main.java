package hkd.lxc.springdata.test;

import hkd.lxc.springdata.entities.Person;
import hkd.lxc.springdata.repository.PersonRepository;
import hkd.lxc.springdata.service.PersonService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;

public class Main {

	private ApplicationContext  ctx=null;
	private PersonRepository personRepository=null;
	private PersonService personService=null;
	
	
	@Before
	public void init() {
		ctx=new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		personRepository=ctx.getBean(PersonRepository.class);
		personService=ctx.getBean(PersonService.class);
	}
	
	@After
	public void destory() {
		
	}
	
	@Test
	public void testDataSource() throws SQLException {
		DataSource dataSource=ctx.getBean(DataSource.class);
		System.out.println(dataSource.getConnection());
	}
	
	@Test
	public void testJpa() {
	}
	
	@Test
	public void testQuery1() {
		Person person=personRepository.getByLastName("AA");
		System.out.println(person);
	}

	@Test
	public void testQuery2() {
		List<Person> persons=personRepository.getByLastNameStartingWithAndIdLessThan("a", 10);
		System.out.println(persons);
	}
	@Test
	public void testQuery3() {
		List<Person> persons=personRepository.getByLastNameEndingWithAndIdLessThan("m", 10);
		System.out.println(persons);
	}
	
	@Test
	public void testQuery4() {
		List<Person> persons=personRepository.getByLastNameContainsAndIdLessThan("m",10);
		System.out.println(persons);
	}
	
	@Test
	public void testQuery5() {
		List<String>list=Arrays.asList("aa@aa.com","bb@bb.com","cc@cc.com");
		List<Person> persons=personRepository.
				getByEmailInOrBirthLessThan(list, new Date());
		System.out.println(persons);
	}
	
	@Test
	public void testQuery6() {
		Person person=personRepository.getMaxIdPerson();
		System.out.println(person);
	}
	
	@Test
	public void testQuery7() {
		List<Person> persons=personRepository.testQueryAnnotationParams1("%AA%", "%.com");
		System.out.println(persons);
	}
	
	@Test
	public void testQuery8() {
		List<Person> persons=personRepository.testQueryAnnotationParams2("123@123.com", "AA");
		System.out.println(persons);
	}
	
	@Test
	public void testQuery9() {
		List<Person> persons=personRepository.testQueryAnnotationLikeParam1("AA",".com");
		System.out.println(persons);
	}
	
	@Test
	public void testQuery10() {
		List<Person> persons=personRepository.testQueryAnnotationLikeParam2(".com","AA");
		System.out.println(persons);
	}
	
	
	@Test
	public void testQuery11() {
		long num=personRepository.getTotalCount();
		System.out.println(num);
	}
	
	
	@Test
	public void testQuery12() {
		personService.updatePersonEmail("abc@abc.com", 1);
	}
	
	@Test
	public void testQuery13() {
		List<Person>persons=new ArrayList<Person>();
		Person person=null;
		for(int i= 'a';i<='z';i++) {
			person=new Person();
			person.setBirth(new Date());
			person.setEmail((char)i+""+(char)i+"@"+(char)i+""+(char)i+".com");
			person.setId(i);
			person.setLastName((char)i+""+(char)i);
			persons.add(person);
		}
		System.out.println(persons.size());
		personService.save(persons);
		
	}
	
	@Test
	public void testQuery14() {
		//pageNo 从 0 开始. 
		int pageNo=0;
		int pageSize=5;
		Order order=new Order(Direction.DESC, "id");
		Sort sort=new Sort(order);
		Pageable pageable=new PageRequest(pageNo, pageSize, sort);
		Page<Person> page=personRepository.findAll(pageable);
		
		System.out.println("总记录数: " + page.getTotalElements());
		System.out.println("当前第几页: " + (page.getNumber() + 1));
		System.out.println("总页数: " + page.getTotalPages());
		System.out.println("当前页面的 List: " + page.getContent());
		System.out.println("当前页面的记录数: " + page.getNumberOfElements());		
	}
	
	
	/**
	 * saveAndFlush 相当于 jpa的 merge
	 */
	@Test
	public void testJpaRepository(){
		Person person = new Person();
		person.setBirth(new Date());
		person.setEmail("xy@atguigu.com");
		person.setLastName("xyz");
		person.setId(23);
		
		Person person2 = personRepository.saveAndFlush(person);
		
		System.out.println(person == person2);
	}
	
	
	@Test
	public void testJpaSpecificationExecutor(){
		int pageNo =  0;
		int pageSize = 5;
		Pageable pageable = new PageRequest(pageNo, pageSize);
		
		/**
		 * 通常使用 Specification 的匿名内部类
		 */
		Specification<Person> spec=new Specification<Person>() {
			/**
			 * @param *root: 代表查询的实体类. 
			 * @param query: 可以从中可到 Root 对象, 即告知 JPA Criteria 查询要查询哪一个实体类. 还可以
			 * 来添加查询条件, 还可以结合 EntityManager 对象得到最终查询的 TypedQuery 对象. 
			 * @param *cb: CriteriaBuilder 对象. 用于创建 Criteria 相关对象的工厂. 当然可以从中获取到 Predicate 对象
			 * @return: *Predicate 类型, 代表一个查询条件. 
			 */
			@Override
			public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query,
					CriteriaBuilder cb) {
				Predicate predicate=null;
				Path<Integer> path=root.get("id");
				predicate=cb.gt(path, 5);
				return predicate;
			}
		};
		
		
		Page<Person>page=personRepository.findAll(spec, pageable);
		System.out.println("总记录数: " + page.getTotalElements());
		System.out.println("当前第几页: " + (page.getNumber() + 1));
		System.out.println("总页数: " + page.getTotalPages());
		System.out.println("当前页面的 List: " + page.getContent());
		System.out.println("当前页面的记录数: " + page.getNumberOfElements());
	}
	
}
