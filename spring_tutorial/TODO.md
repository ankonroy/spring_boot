# Implementation TODO - Authentication & Profile Management

## Phase 1: Backend - Authentication System
- [x] Add Spring Security and Thymeleaf dependencies to pom.xml
- [x] Create User entity (id, email, password, firstName, lastName, role)
- [x] Create UserRepository interface
- [x] Create AuthController (login/register GET/POST, logout)
- [x] Create AuthService (register, authenticate, getCurrentUser)
- [x] Create SecurityConfig (WebSecurityConfig)
- [x] Create UserRegistrationDTO
- [x] Create LoginDTO

## Phase 2: Backend - Update Existing Controllers
- [x] Create ProfileController for profile management
- [x] Create StudentApiController for students management
- [x] Create PageController for serving pages

## Phase 3: Frontend - Authentication Pages
- [x] Create login.html Thymeleaf template
- [x] Create register.html Thymeleaf template
- [x] Add CSS for login/register forms

## Phase 4: Frontend - Protected Pages (convert static to dynamic)
- [x] Update index.html with user info bar and logout
- [x] Update teachers.html with dynamic data
- [x] Update students.html with dynamic data
- [x] Update departments.html with dynamic data
- [x] Update courses.html with dynamic data
- [x] Add navigation with logout button

## Phase 5: Profile Management
- [x] Create profile.html for user profile
- [x] Create student-form.html for teachers to add students
- [x] Create edit-profile.html
- [x] Create change-password.html

## Phase 6: Testing & Verification
- [ ] Build the project to verify compilation
- [ ] Test registration flow
- [ ] Test login/logout
- [ ] Test access control (redirect to login)
- [ ] Test teacher can add students
- [ ] Test student can update own profile
- [ ] Verify all pages work correctly

