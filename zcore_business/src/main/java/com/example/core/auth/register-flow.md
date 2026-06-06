# Register flow

## Endpoint
- POST /api/auth/register

## Request body (RegisterRequest)
- name
- email
- password
- phone

## Flow tom tat
1. AuthController.nhan request va goi AuthService.register(request).
2. AuthService.kiem tra userRepository.existsByEmail(email).
   - Neu ton tai: nem RuntimeException("Email already exists").
3. Tao User moi tu request.
   - password duoc set 2 lan; gia tri cuoi cung la password thuan tu request.
4. Luu user qua userRepository.save(user).
5. Tao JWT bang JwtUtil.generateToken(name, userId, email).
6. Tra ve AuthResponse gom token, type="Bearer", userId, email, role.

## Response
- 200 OK + AuthResponse

## Loi co the gap
- 500 (RuntimeException): email da ton tai.

## Ghi chu
- Hien tai user.password luu password thuan (do set password 2 lan).
