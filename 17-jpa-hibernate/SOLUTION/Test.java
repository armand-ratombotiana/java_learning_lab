package com.learning.lab.module17.solution;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.time.LocalDateTime;

public class Test {

    @Test void testUserEntity() { Solution.User u = new Solution.User(); u.setName("John"); u.setEmail("j@test.com"); assertEquals("John", u.getName()); }
    @Test void testUserId() { Solution.User u = new Solution.User(); u.setId(1L); assertEquals(1L, u.getId()); }
    @Test void testUserVersion() { Solution.User u = new Solution.User(); u.setVersion(1L); assertEquals(1L, u.getVersion()); }
    @Test void testUserCreatedAt() { Solution.User u = new Solution.User(); u.setCreatedAt(LocalDateTime.now()); assertNotNull(u.getCreatedAt()); }
    @Test void testProductEntity() { Solution.Product p = new Solution.Product(); p.setName("Widget"); p.setPrice(9.99); assertEquals("Widget", p.getName()); }
    @Test void testProductCategory() { Solution.Product p = new Solution.Product(); Solution.Category c = new Solution.Category(); c.setName("Electronics"); p.setCategory(c); assertEquals("Electronics", p.getCategory().getName()); }
    @Test void testProductReviews() { Solution.Product p = new Solution.Product(); p.getReviews().add(new Solution.Review()); assertEquals(1, p.getReviews().size()); }
    @Test void testCategoryEntity() { Solution.Category c = new Solution.Category(); c.setName("Books"); assertEquals("Books", c.getName()); }
    @Test void testCategoryProducts() { Solution.Category c = new Solution.Category(); c.getProducts().add(new Solution.Product()); assertEquals(1, c.getProducts().size()); }
    @Test void testReviewEntity() { Solution.Review r = new Solution.Review(); r.setRating(5); r.setComment("Great"); assertEquals(5, r.getRating()); }
    @Test void testInMemoryRepoSave() { Solution.UserRepository repo = new Solution.InMemoryUserRepository(); Solution.User u = new Solution.User(); u.setName("Test"); repo.save(u); assertEquals(1, repo.count()); }
    @Test void testInMemoryRepoFindById() { Solution.UserRepository repo = new Solution.InMemoryUserRepository(); Solution.User u = new Solution.User(); u.setName("Test"); repo.save(u); assertTrue(repo.findById(1L).isPresent()); }
    @Test void testInMemoryRepoFindByEmail() { Solution.UserRepository repo = new Solution.InMemoryUserRepository(); Solution.User u = new Solution.User(); u.setEmail("test@test.com"); repo.save(u); assertTrue(repo.findByEmail("test@test.com").isPresent()); }
    @Test void testInMemoryRepoFindByNameContaining() { Solution.UserRepository repo = new Solution.InMemoryUserRepository(); Solution.User u = new Solution.User(); u.setName("John Doe"); repo.save(u); assertEquals(1, repo.findByNameContaining("John").size()); }
    @Test void testInMemoryRepoDelete() { Solution.UserRepository repo = new Solution.InMemoryUserRepository(); Solution.User u = new Solution.User(); repo.save(u); repo.delete(u); assertEquals(0, repo.count()); }
    @Test void testInMemoryRepoFindAll() { Solution.UserRepository repo = new Solution.InMemoryUserRepository(); repo.save(new Solution.User()); repo.save(new Solution.User()); assertEquals(2, repo.findAll().size()); }
    @Test void testJpqlQueryBuilder() { Solution.JpQLQueryBuilder q = new Solution.JpQLQueryBuilder().select("User").where("u.email = :email").orderBy("u.name"); assertTrue(q.build().contains("SELECT u FROM User u")); }
    @Test void testJpqlQueryBuilderParams() { Solution.JpQLQueryBuilder q = new Solution.JpQLQueryBuilder().setParameter("email", "test@test.com"); assertEquals(1, q.getParameters().size()); }
    @Test void testCriteriaBuilderEqual() { Solution.Condition c = Solution.CriteriaBuilder.equal("email", "test@test.com"); assertTrue(c.getExpression().contains("=")); }
    @Test void testCriteriaBuilderGreaterThan() { Solution.Condition c = Solution.CriteriaBuilder.greaterThan("price", "100"); assertTrue(c.getExpression().contains(">")); }
    @Test void testCriteriaBuilderLike() { Solution.Condition c = Solution.CriteriaBuilder.like("name", "%test%"); assertTrue(c.getExpression().contains("LIKE")); }
    @Test void testCriteriaBuilderBetween() { Solution.Condition c = Solution.CriteriaBuilder.between("age", "18", "65"); assertTrue(c.getExpression().contains("BETWEEN")); }
    @Test void testEntityManagerPersist() { Solution.EntityManager em = new Solution.EntityManager(); em.persist(new Solution.User()); }
    @Test void testEntityTransactionBegin() { Solution.EntityTransaction tx = new Solution.EntityTransaction(); tx.begin(); assertTrue(tx.isActive()); }
    @Test void testEntityTransactionCommit() { Solution.EntityTransaction tx = new Solution.EntityTransaction(); tx.begin(); tx.commit(); assertFalse(tx.isActive()); }
    @Test void testEntityTransactionRollback() { Solution.EntityTransaction tx = new Solution.EntityTransaction(); tx.begin(); tx.rollback(); assertFalse(tx.isActive()); }
    @Test void testPersistenceContext() { Solution.PersistenceContext ctx = new Solution.PersistenceContext(); Solution.User u = new Solution.User(); ctx.persist(u); assertNotNull(ctx.find(Solution.User.class, u.hashCode())); }
    @Test void testPersistenceContextClear() { Solution.PersistenceContext ctx = new Solution.PersistenceContext(); ctx.persist(new Solution.User()); ctx.clear(); }
    @Test void testPagedResultContent() { Solution.PagedResult<String> pr = new Solution.PagedResult<>(List.of("a","b"), 2, 1, 0, 10); assertEquals(2, pr.getContent().size()); }
    @Test void testPagedResultTotalElements() { Solution.PagedResult<String> pr = new Solution.PagedResult<>(List.of("a"), 100, 10, 0, 10); assertEquals(100, pr.getTotalElements()); }
    @Test void testPagedResultTotalPages() { Solution.PagedResult<String> pr = new Solution.PagedResult<>(List.of("a"), 100, 10, 0, 10); assertEquals(10, pr.getTotalPages()); }
    @Test void testPagedResultHasNext() { Solution.PagedResult<String> pr = new Solution.PagedResult<>(List.of("a"), 100, 10, 5, 10); assertTrue(pr.hasNext()); }
    @Test void testPagedResultNoNextOnLastPage() { Solution.PagedResult<String> pr = new Solution.PagedResult<>(List.of("a"), 100, 10, 9, 10); assertFalse(pr.hasNext()); }
    @Test void testProductDescription() { Solution.Product p = new Solution.Product(); p.setDescription("A great product"); assertEquals("A great product", p.getDescription()); }
    @Test void testTagEntity() { Solution.Relationships.Tag t = new Solution.Relationships.Tag(); t.setName("Sale"); assertEquals("Sale", t.getName()); }
    @Test void testTagProducts() { Solution.Relationships.Tag t = new Solution.Relationships.Tag(); t.getProducts().add(new Solution.Product()); assertEquals(1, t.getProducts().size()); }
    @Test void testAddressEntity() { Solution.Relationships.Address a = new Solution.Relationships.Address(); a.setStreet("123 Main St"); assertEquals("123 Main St", a.getStreet()); }
    @Test void testNPlusOneOptimization() { String q = Solution.NPlusOneDemo.getOptimizedQuery(); assertTrue(q.contains("JOIN FETCH")); }
    @Test void testQueriesConstants() { assertTrue(Solution.Queries.FIND_ALL_USERS.contains("SELECT")); }
    @Test void testReviewUser() { Solution.Review r = new Solution.Review(); Solution.User u = new Solution.User(); r.setUser(u); assertNotNull(r.getUser()); }
    @Test void testReviewProduct() { Solution.Review r = new Solution.Review(); Solution.Product p = new Solution.Product(); r.setProduct(p); assertNotNull(r.getProduct()); }
    @Test void testPagedResultPageAndSize() { Solution.PagedResult<String> pr = new Solution.PagedResult<>(List.of("a"), 50, 5, 2, 10); assertEquals(2, pr.getPage()); assertEquals(10, pr.getSize()); }
    @Test void testEntityManagerFind() { Solution.EntityManager em = new Solution.EntityManager(); Solution.User u = new Solution.User(); u.setId(1L); em.persist(u); assertNotNull(em.find(Solution.User.class, 1L)); }
    @Test void testReviewComment() { Solution.Review r = new Solution.Review(); r.setComment("Excellent product!"); assertEquals("Excellent product!", r.getComment()); }
}