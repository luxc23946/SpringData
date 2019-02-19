package hkd.lxc.springdata.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import hkd.lxc.springdata.entities.Person;

/**
 * 1. Repository 是一个空接口. 即是一个标记接口
 * 2. 若我们定义的接口继承了 Repository, 则该接口会被 IOC 容器识别为一个 Repository Bean.
 * 纳入到 IOC 容器中. 进而可以在该接口中定义满足一定规范的方法. 
 * 
 * 3. 实际上, 也可以通过 @RepositoryDefinition 注解来替代继承 Repository 接口
 * 
 * Repository 子接口中声明方法
 *   1. 不是随便声明的. 而需要符合一定的规范
 *   2. 查询方法以 find | read | get 开头
 *   3. 涉及条件查询时，条件的属性用条件关键字连接
 *   4. 要注意的是：条件属性以首字母大写。
 *   5. 支持属性的级联查询. 若当前类有符合条件的属性, 则优先使用, 而不使用级联属性. 
 *    若需要使用级联属性, 则属性之间使用 _ 进行连接. 
 * CrudRepository 是 Repository的子接口,里面有一些基本的增删改查API
 * PagingAndSortingRepositor 是 CrudRepository 的子接口,里面扩充了分页和排序  但不能添加参数
 * JpaRepository 是  PagingAndSortingRepositor 的子接口,里面扩充了一些API
 * JpaSpecificationExecutor 是一个独立的接口 ，实现带查询条件的分页
 */
//@RepositoryDefinition(domainClass=Person.class,idClass=Integer.class)
public interface PersonRepository extends JpaRepository<Person, Integer>,JpaSpecificationExecutor<Person> {
	
	/**
	 * 根据LastName获取对应的Person
	 * @return
	 */
	Person getByLastName(String str);
	
	/**
	 * WHERE lastName LIKE ?% AND id < ?
	 * @param lastName
	 * @param id
	 * @return
	 */
	List<Person> getByLastNameStartingWithAndIdLessThan(String lastName, Integer id);
	
	/**
	 * WHERE lastName LIKE %? AND id < ?
	 * @param lastName
	 * @param id
	 * @return
	 */
	List<Person> getByLastNameEndingWithAndIdLessThan(String lastName, Integer id);

	
	/**
	 * where lastName Contains ? and id <?
	 * @return
	 */
	List<Person>getByLastNameContainsAndIdLessThan(String lastName, Integer id);
	
	/**
	 * HERE email IN (?, ?, ?) OR birth < ?
	 */
	List<Person>getByEmailInOrBirthLessThan(List<String> emails,Date date);
	
	
	/**
	 * WHERE a.address.id > ?
	 */
	List<Person> getByAddressIdGreaterThan(Integer id);
	
	
	/**
	 * WHERE a.id > ?
	 */
	List<Person> getByAddress_IdGreaterThan(Integer id);

	/**
	 * 查询 id 值最大的那个 Person
	 * 使用 @Query 注解可以自定义 JPQL 语句以实现更灵活的查询
	 * @return
	 */
	@Query("SELECT p FROM Person p where p.id=(SELECT Max(p2.id) from Person p2)")
	Person getMaxIdPerson();
	
	
	/**
	 * 为 @Query 注解传递参数的方式1: 使用占位符. 参数按下标顺序
	 * @param lastName
	 * @param email
	 * @return
	 */
	@Query("SELECT p FROM Person p WHERE p.lastName like ?1 AND p.email like ?2")
	List<Person> testQueryAnnotationParams1(String lastName, String email);
	
	/**
	 * 为 @Query 注解传递参数的方式2: 命名参数的方式. 
	 * @param lastName
	 * @param email
	 * @return
	 */
	@Query("SELECT p FROM Person p WHERE p.lastName = :lastName AND p.email = :email")
	List<Person> testQueryAnnotationParams2(@Param("email") String email,@Param("lastName") String lastName);
	
	/**
	 * SpringData 允许在占位符上添加 %%. 
	 * @param lastName
	 * @param email
	 * @return
	 */
	@Query("SELECT p FROM Person p WHERE p.lastName LIKE %?1% OR p.email LIKE %?2%")
	List<Person> testQueryAnnotationLikeParam1(String lastName, String email);
	
	/**
	 * SpringData 允许在占位符上添加 %%. 
	 * @param email
	 * @param lastName
	 * @return
	 */
	@Query("SELECT p FROM Person p WHERE p.lastName LIKE %:lastName% OR p.email LIKE %:email%")
	List<Person> testQueryAnnotationLikeParam2(@Param("email") String email, @Param("lastName") String lastName);
		
	/**
	 * 设置 nativeQuery=true 即可以使用原生的 SQL 查询
	 * @return
	 */
	@Query(value="SELECT count(id) FROM jpa_persons", nativeQuery=true)
	long getTotalCount();
	
	/**
	 * 
	 * @param id
	 * @param email
	 */
	@Modifying
	@Query("UPDATE Person p SET p.email = :email WHERE p.id = :id")
	void updatePersonEmail(@Param("id") Integer id, @Param("email") String email);
	
}
