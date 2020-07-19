import org.springframework.web.bind.annotation.*;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;

@RestController
public class Application {

    private EntityManager em;

    public Application(EntityManager em) {
        this.em = em;
    }

    @GetMapping("/update")
    public void updateOrder(@RequestBody Order order) {
        if (order.getId() == null) {
            em.persist(order);
        } else {
            em.merge(order);
        }
    }

    @GetMapping("/loadOrderById")
    @ResponseBody
    public Order getOrder(@RequestParam("id") Long id) {
        if (id == null) {
            return null;
        }
        return em.createQuery("select o from Order o where o.id = :id", Order.class).setParameter("id", id).getResultList().get(0);
    }

    @GetMapping("/getPrice")
    public Double getPrice(@RequestParam("category") String categoryName, @RequestParam("unit") String unit, @RequestParam("count") Integer count) {
        if (count == null || categoryName == null || unit == null) {
            return -1d;
        }

        if (categoryName.equals("Apples")) {
            Category category = em.createQuery("select c from Category c where c.name = :name", Category.class)
                    .setParameter("name", categoryName)
                    .getSingleResult();
            if (unit.equals("kg")) {
                return category.getPrice() * count;
            } else if (unit.equals("g")) {
                return category.getPrice() * count * 1000;
            }
        }

        if (categoryName.equals("TV")) {
            Category category = em.createQuery("select c from Category c where c.name = :name", Category.class)
                    .setParameter("name", categoryName)
                    .getSingleResult();
            return category.getPrice() * count + (category.getPrice() * count * 0.18);
        }

        return null;
    }

    @GetMapping("/deleteOrder")
    public void removeOrder(@RequestParam("id") Long id) {
        final Order order = em.find(Order.class, id);
        em.remove(order);
    }

    @GetMapping("/getCategories")
    public List<Category> getCategories() {
        return em.createQuery("select c from Category c", Category.class).getResultList();
    }

    @GetMapping("/newCategory")
    public void createCategory(@RequestParam("name") String name, @RequestParam("price") Double price) {
        Category category = new Category();
        category.setName(name);
        category.setPrice(price);
        em.persist(category);
    }

    @GetMapping("/updateCategoryPrice")
    public void updateCategoryPrice(@RequestParam("name") String name, @RequestParam("price") Double price) {
        Category category = em.createQuery("select c from Category c where c.name = :name", Category.class)
                .setParameter("name", name)
                .getSingleResult();
        category.setPrice(price);
        em.merge(category);
    }

    @GetMapping("/deleteCategory")
    public void deleteCategory(@RequestParam("id") Long id) {
        final Category category = em.find(Category.class, id);
        em.remove(category);
    }

    @Entity
    public static class Order {
        @Id
        private Long id;

        private String name;

        private Double price;

        private Integer count;

        private String unit;

        @ManyToOne
        private Category category;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public Category getCategory() {
            return category;
        }

        public void setCategory(Category category) {
            this.category = category;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }
    }

    @Entity
    public static class Category {
        @Id
        private Long id;

        private String name;

        private Double price;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }
    }

}
