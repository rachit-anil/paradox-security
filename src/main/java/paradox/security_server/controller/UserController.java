package paradox.security_server.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import paradox.security_server.models.Customer;
import paradox.security_server.repository.CustomerRepository;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController()
@CrossOrigin(origins = "https://projectparadox.in")
@RequestMapping("/auth")
public class UserController {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody Customer user) {
        Map<String, String> response = new HashMap<>();

        try {
            // Check if the user already exists
            Optional<Customer> existingUser = customerRepository.findByEmail(user.getEmail());
            if (existingUser.isPresent()) {
                response.put("message", "User already exists with this email");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Hash the password and save the user
            String hashPwd = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashPwd);
            Customer savedCustomer = customerRepository.save(user);

            if (savedCustomer.getId() > 0) {
                response.put("message", "User registered successfully");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("message", "User registration failed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception ex) {
            response.put("message", "An exception occurred: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();

        try {
            // Extract the Authorization header
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
                response.put("message", "Authorization header is missing or invalid");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Decode the Base64 encoded credentials
            String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(decodedBytes, StandardCharsets.UTF_8);

            // Split the credentials into username (email) and password
            String[] emailAndPassword = credentials.split(":", 2);
            if (emailAndPassword.length != 2) {
                response.put("message", "Invalid credentials format");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String email = emailAndPassword[0];
            String password = emailAndPassword[1];

            // Check if the user exists
            Optional<Customer> existingUser = customerRepository.findByEmail(email);
            if (existingUser.isEmpty()) {
                response.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Verify the password
            Customer storedUser = existingUser.get();
            if (passwordEncoder.matches(password, storedUser.getPassword())) {
                response.put("message", "Login successful");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception ex) {
            response.put("message", "An exception occurred: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/public")
    public ResponseEntity<String> loginUser() {return ResponseEntity.status(HttpStatus.OK).body("Hello");}
}
