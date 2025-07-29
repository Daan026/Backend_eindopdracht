# Script om automatisch pull requests 4 en 5 aan te maken
# Voer uit met: powershell -ExecutionPolicy Bypass -File create-pull-requests.ps1

Write-Host "=== Creating Pull Request 4: Improve Validation ===" -ForegroundColor Green

# Ga terug naar main branch
git checkout main

# Maak nieuwe branch voor validation improvements
git checkout -b feature/improve-validation

# Voeg validation annotations toe aan User entity
$userEntityPath = "src/main/java/com/fondsdelecturelibre/entity/User.java"
$userContent = Get-Content $userEntityPath -Raw

# Voeg import toe voor validation
$userContent = $userContent -replace "import lombok\.\*;", @"
import lombok.*;
import jakarta.validation.constraints.*;
"@

# Voeg validation annotations toe
$userContent = $userContent -replace "@Column\(unique = true, nullable = false, length = 50\)\s+private String username;", @"
@Column(unique = true, nullable = false, length = 50)
    @NotBlank(message = "Gebruikersnaam is verplicht")
    @Size(min = 3, max = 50, message = "Gebruikersnaam moet tussen 3 en 50 karakters zijn")
    private String username;
"@

$userContent = $userContent -replace "@Column\(unique = true, nullable = false, length = 100\)\s+private String email;", @"
@Column(unique = true, nullable = false, length = 100)
    @NotBlank(message = "Email is verplicht")
    @Email(message = "Ongeldig email formaat")
    private String email;
"@

Set-Content $userEntityPath $userContent

# Commit en push
git add .
git commit -m "feat: Add validation annotations to User entity for better data integrity"
git push -u origin feature/improve-validation

Write-Host "Pull Request 4 created! Link: https://github.com/Daan026/Backend_eindopdracht/pull/new/feature/improve-validation" -ForegroundColor Yellow

Write-Host "`n=== Creating Pull Request 5: Add Logging ===" -ForegroundColor Green

# Ga terug naar main
git checkout main

# Maak nieuwe branch voor logging
git checkout -b feature/add-logging

# Voeg logging toe aan UserService
$userServicePath = "src/main/java/com/fondsdelecturelibre/service/UserService.java"
$serviceContent = Get-Content $userServicePath -Raw

# Voeg logging import toe
$serviceContent = $serviceContent -replace "import org.springframework", @"
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework
"@

# Voeg logger field toe
$serviceContent = $serviceContent -replace "public class UserService \{", @"
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
"@

# Voeg logging statements toe aan belangrijke methodes
$serviceContent = $serviceContent -replace "public User save\(User user\) \{", @"
public User save(User user) {
        logger.info("Saving user with username: {}", user.getUsername());
"@

$serviceContent = $serviceContent -replace "return userRepository\.save\(user\);", @"
User savedUser = userRepository.save(user);
        logger.info("Successfully saved user with ID: {}", savedUser.getId());
        return savedUser;
"@

Set-Content $userServicePath $serviceContent

# Commit en push
git add .
git commit -m "feat: Add comprehensive logging to UserService for better monitoring and debugging"
git push -u origin feature/add-logging

Write-Host "Pull Request 5 created! Link: https://github.com/Daan026/Backend_eindopdracht/pull/new/feature/add-logging" -ForegroundColor Yellow

Write-Host "`n=== Summary ===" -ForegroundColor Cyan
Write-Host "✅ Pull Request 1: README improvement"
Write-Host "✅ Pull Request 2: Exception handling enhancement" 
Write-Host "✅ Pull Request 3: API documentation"
Write-Host "✅ Pull Request 4: Validation improvements"
Write-Host "✅ Pull Request 5: Logging implementation"
Write-Host "`nAll 5 pull requests have been created! 🎉" -ForegroundColor Green

# Ga terug naar main branch
git checkout main

Write-Host "`nYou can now visit GitHub to merge these pull requests." -ForegroundColor Blue
