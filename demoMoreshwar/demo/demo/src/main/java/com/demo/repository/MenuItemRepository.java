package com.demo.repository;

import com.demo.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    // FIX 1: Added 'ORDER BY m.category ASC' to stop the jumbling
    @Query("SELECT DISTINCT m.category FROM MenuItem m ORDER BY m.category ASC")
    List<String> findAllCategories();

    /**
     * FIX 2: Added 'OrderByNameAsc'
     * Now, when you click a category, the food items inside it will 
     * also be sorted alphabetically (e.g., Bacon Burger before Cheese Burger).
     */
    List<MenuItem> findByCategoryOrderByNameAsc(String category);

    // Keep this one as is, or add sorting if you want search results sorted too
    List<MenuItem> findByNameContainingIgnoreCase(String name);
}
