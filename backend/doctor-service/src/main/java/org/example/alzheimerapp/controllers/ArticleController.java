package org.example.alzheimerapp.controllers;

import org.example.alzheimerapp.entities.Article;
import org.example.alzheimerapp.services.interfaces.ArticleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private static final Logger log = LoggerFactory.getLogger(ArticleController.class);

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public ResponseEntity<List<Article>> getAllArticles() {
        log.info("Incoming request: GET /api/articles");
        return ResponseEntity.ok(articleService.getAllArticles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable("id") Integer id) {
        log.info("Incoming request: GET /api/articles/{}", id);
        return ResponseEntity.ok(articleService.getArticleById(id));
    }

    @PostMapping
    public ResponseEntity<?> createArticle(
            @RequestBody Article article,
            @RequestHeader(value = "Role", required = false) String role) {

        log.info("Incoming request: POST /api/articles (Role: {})", role);
        if (!"Doctor".equalsIgnoreCase(role)) {
            log.warn("Access denied: Only Doctors can create articles. Provided role: {}", role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only Doctors can create articles");
        }
        return ResponseEntity.ok(articleService.createArticle(article));
    }

    @PutMapping
    public ResponseEntity<?> updateArticle(
            @RequestBody Article article,
            @RequestHeader(value = "Role", required = false) String role) {

        log.info("Incoming request: PUT /api/articles (Role: {})", role);
        if (!"Doctor".equalsIgnoreCase(role)) {
            log.warn("Access denied: Only Doctors can update articles. Provided role: {}", role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only Doctors can update articles");
        }
        return ResponseEntity.ok(articleService.updateArticle(article));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticle(
            @PathVariable("id") Integer id,
            @RequestHeader(value = "Role", required = false) String role) {

        log.info("Incoming request: DELETE /api/articles/{} (Role: {})", id, role);
        if (!"Doctor".equalsIgnoreCase(role)) {
            log.warn("Access denied: Only Doctors can delete articles. Provided role: {}", role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only Doctors can delete articles");
        }
        articleService.deleteArticle(id);
        return ResponseEntity.ok("Article deleted successfully");
    }
}
