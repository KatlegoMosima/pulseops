
package za.co.pulseops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PulseOpsApplication {

    public static void main(String[] args) {

        SpringApplication.run(
            PulseOpsApplication.class,
            args
        );

        System.out.println(
            "PulseOps Backend is running!"
        );
    }
}
