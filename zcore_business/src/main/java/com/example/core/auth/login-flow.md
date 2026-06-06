# Login flow

## Endpoint
- POST /api/auth/login

## Request body (LoginRequest)
- email
- password

## Flow tom tat
1. AuthController.nhan request va goi AuthService.login(request).
2. AuthService.tim user theo email; neu khong co, tra loi RuntimeException("Invalid email or password").
3. So sanh password: neu request.getPassword() != user.getPassword() thi nem RuntimeException("Invalid email or password").
4. Tao JWT bang JwtUtil.generateToken(name, userId, email).
5. Tra ve AuthResponse gom token, type="Bearer", userId, email, role.

## Response
- 200 OK + AuthResponse

## Loi co the gap
- 500 (RuntimeException): email khong ton tai hoac password sai.

## Ghi chu
- Hien tai check password la so sanh chuoi thuan, khong dung PasswordEncoder.
