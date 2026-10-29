package kz.iitu.studentapi;

import java.util.Map;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.*;

@RestController
public class InfoController {
    private final BuildProperties build;
    public InfoController(BuildProperties build) { this.build = build; }
    @GetMapping("/")
    public Map<String, String> info() {
        return Map.of("application", "Sarsembayev Eldar Student API", "student", "Eldar Sarsembayev",
            "group", "IT2-2301", "studentId", "37006", "version", build.getVersion(),
            "students", StudentController.BASE_PATH);
    }
}
