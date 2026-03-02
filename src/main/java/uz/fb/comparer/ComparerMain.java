package uz.fb.comparer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uz.fb.comparer.service.ComparingService;

@SpringBootApplication
public class ComparerMain {
    public static void main(String[] args) {
        SpringApplication.run(ComparerMain.class, args);
    }
}
