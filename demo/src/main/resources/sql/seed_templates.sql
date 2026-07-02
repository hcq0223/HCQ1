-- 插入 5 种风格模板，每个分类一个
INSERT INTO resume_templates (name, description, category, is_premium, is_active) VALUES
('专业经典', '经典蓝色风格，稳重专业，适合传统行业求职', 'professional', FALSE, TRUE),
('创意活力', '紫色渐变主题，个性鲜明，展现创造力', 'creative', FALSE, TRUE),
('现代简约', '卡片式布局，简洁清晰，适合互联网行业', 'modern', FALSE, TRUE),
('极简纯净', '大量留白，极简线条，突出内容本身', 'minimalist', FALSE, TRUE),
('学术严谨', '正式学术风格，适合教育、科研岗位', 'academic', FALSE, TRUE);