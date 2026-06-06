# Auth request/response flow (chi tiet)

## Tong quan security va filter
- SecurityConfig permitAll cho /api/auth/** va mot so endpoint public; cac endpoint khac yeu cau JWT.
- JwtAuthenticationFilter chay truoc UsernamePasswordAuthenticationFilter.
- Filter lay Authorization header, can co "Bearer <token>". Neu thieu/khong dung prefix -> bo qua filter (khong set auth) va tiep tuc.
- Neu token hop le, filter trich userId tu claim "userId", tim User, gan authorities = role, set SecurityContext.

## Cau truc JWT
- Tao boi JwtUtil.generateToken(name, userId, email).
- Claims: userId, email; subject = name.
- Ky HS256 voi secret (>= 32 bytes); het han theo jwt.expiration.

---

## 1) POST /api/auth/register
### Request
- URL: /api/auth/register
- Body (RegisterRequest): name, email, password, phone

### Flow (thanh cong)
1. AuthController nhan request va goi AuthService.register.
2. AuthService kiem tra userRepository.existsByEmail(email).
3. Tao User tu request, sau do save vao DB.
4. Tao JWT tu name, userId, email.
5. Tra AuthResponse: token, type="Bearer", userId, email, role.

### Flow (that bai)
- Neu email da ton tai -> RuntimeException("Email already exists").

### Response
- 200 OK: AuthResponse (token, type, userId, email, role)
- 500: loi RuntimeException (email da ton tai)

### Ghi chu ky thuat
- Password hien tai duoc set 2 lan; gia tri cuoi la password thuan.

---

## 2) POST /api/auth/login
### Request
- URL: /api/auth/login
- Body (LoginRequest): email, password

### Flow (thanh cong)
1. AuthController goi AuthService.login.
2. AuthService tim user theo email.
3. Kiem tra password (so sanh chuoi thuan).
4. Tao JWT tu name, userId, email.
5. Tra AuthResponse: token, type="Bearer", userId, email, role.

### Flow (that bai)
- Email khong ton tai -> RuntimeException("Invalid email or password").
- Password sai -> RuntimeException("Invalid email or password").

### Response
- 200 OK: AuthResponse (token, type, userId, email, role)
- 500: loi RuntimeException (email/password sai)

---

## 3) GET /api/auth/validate
### Request
- URL: /api/auth/validate
- Header: Authorization: Bearer <token>

### Flow (thanh cong)
1. AuthController kiem tra header va prefix "Bearer ".
2. Trich token va goi AuthService.validateToken.
3. JwtUtil validate: parse claims, kiem tra het han.
4. Tra true.

### Flow (that bai)
- Thieu header hoac sai prefix -> 401 + false.
- Token parse fail hoac het han -> 401 + false.

### Response
- 200 OK: true
- 401 UNAUTHORIZED: false

---

## 4) Request den endpoint can auth (khong nam trong permitAll)
### Request
- Header: Authorization: Bearer <token>

### Flow (thanh cong)
1. JwtAuthenticationFilter validate token.
2. Trich userId, tim User, gan authorities = role.
3. Set SecurityContext; request tiep tuc vao controller.

### Flow (that bai)
- Thieu/sai token -> khong set SecurityContext, request se bi chan boi Spring Security (401/403 tuy config).
