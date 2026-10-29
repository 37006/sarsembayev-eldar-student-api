package kz.iitu.studentapi;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(StudentController.BASE_PATH)
public class StudentController {
    public static final String BASE_PATH = "/api/sarsembayev-eldar/students";
    private final StudentService service;
    public StudentController(StudentService service) { this.service = service; }
    @GetMapping
    public List<Student> all() { return service.findAll(); }
    @GetMapping("/{id}")
    public Student one(@PathVariable long id) { return service.findById(id); }
    @PostMapping
    public ResponseEntity<Student> create(@Valid @RequestBody StudentRequest request) {
        Student student = service.create(request);
        return ResponseEntity.created(URI.create(BASE_PATH + "/" + student.getId())).body(student);
    }
    @PutMapping("/{id}")
    public Student update(@PathVariable long id, @Valid @RequestBody StudentRequest request) {
        return service.update(id, request);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
