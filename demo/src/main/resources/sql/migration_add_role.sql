-- 为 users 表添加 role 字段，默认值为 'user'
ALTER TABLE users
    ADD COLUMN role VARCHAR(20) DEFAULT 'user' COMMENT '用户角色: user-普通用户, admin-管理员';

-- 将已存在的用户 role 设置为 'user'
UPDATE users SET role = 'user' WHERE role IS NULL;

-- 示例：如果需要创建管理员账号，可执行以下 SQL（密码需要手动替换为 bcrypt 加密后的值）
-- INSERT INTO users (username, email, password_hash, role) VALUES ('admin', 'admin@example.com', '$...', 'admin');