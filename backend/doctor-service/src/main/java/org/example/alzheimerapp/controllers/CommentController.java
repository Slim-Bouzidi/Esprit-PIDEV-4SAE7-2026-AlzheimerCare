package org.example.alzheimerapp.controllers;

import org.example.alzheimerapp.entities.Comment;
import org.example.alzheimerapp.services.interfaces.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private static final Logger log = LoggerFactory.getLogger(CommentController.class);

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<Comment>> getCommentsByArticle(@PathVariable("articleId") Integer articleId) {
        log.info("Incoming request: GET /api/comments/article/{}", articleId);
        return ResponseEntity.ok(commentService.getCommentsByArticle(articleId));
    }

    @PostMapping
    public ResponseEntity<?> createComment(
            @RequestBody Comment comment,
            @RequestParam("articleId") Integer articleId,
            @RequestHeader(value = "Role", required = false) String role) {

        log.info("Incoming request: POST /api/comments (Article: {}, Role: {})", articleId, role);
        if (!"Aidant".equalsIgnoreCase(role)) {
            log.warn("Access denied: Only Aidants can post comments. Provided role: {}", role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only Aidants can post comments");
        }
        // Set the role properly depending on logic (it can be also taken from the DTO,
        // but here we enforce it)
        comment.setRole(role);
        return ResponseEntity.ok(commentService.createComment(comment, articleId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(
            @PathVariable("id") Integer id,
            @RequestHeader(value = "Role", required = false) String role) {

        log.info("Incoming request: DELETE /api/comments/{} (Role: {})", id, role);
        if (!"Doctor".equalsIgnoreCase(role) && !"Aidant".equalsIgnoreCase(role)) {
            log.warn("Access denied: Only Doctors or Aidants can delete comments. Provided role: {}", role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only Doctors or commenting Aidants can delete comments");
        }
        commentService.deleteComment(id);
        return ResponseEntity.ok("Comment deleted successfully");
    }
}
