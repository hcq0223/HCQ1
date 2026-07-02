##数据库结构
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID，主键，自增',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名，唯一，用于登录和展示',
    email VARCHAR(100) UNIQUE NOT NULL COMMENT '电子邮箱，唯一，用于账号验证和找回密码',
    password_hash VARCHAR(255) NOT NULL COMMENT '加密后的密码哈希值，推荐使用bcrypt加密算法',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '账号创建时间，自动记录',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '信息最后更新时间，每次修改自动更新'
) COMMENT='用户信息表，存储用户基本账户信息';
##简历基本信息表
CREATE TABLE resumes (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '简历ID，主键，自增',
    user_id INT NOT NULL COMMENT '所属用户ID，关联users表，标识简历创建者',
    title VARCHAR(200) NOT NULL COMMENT '简历标题，便于用户在简历列表中识别和管理',
    full_name VARCHAR(100) NOT NULL COMMENT '求职者全名，简历中显示的真实姓名',
    email VARCHAR(100) COMMENT '联系邮箱，用于招聘方联系求职者',
    phone VARCHAR(20) COMMENT '联系电话号码，支持国际号码格式',
    location VARCHAR(200) COMMENT '所在地区，家庭住址',
    linkedin_url VARCHAR(500) COMMENT 'LinkedIn个人主页链接，增强职业社交展示',
    github_url VARCHAR(500) COMMENT 'GitHub个人主页链接，展示代码和技术项目',
    personal_website VARCHAR(500) COMMENT '个人网站或博客链接，展示更多个人作品和信息',
    avatar_url VARCHAR(500) COMMENT '简历头像URL，个性化简历展示',
    professional_summary TEXT COMMENT '个人简介/求职意向，概括性描述职业背景和求职目标',
    template_id INT COMMENT '使用的简历模板ID，关联简历模板表，控制展示样式',
    status ENUM('draft', 'published') DEFAULT 'draft' COMMENT '简历状态：draft-草稿（仅自己可见），published-已发布（可对外分享）',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '软删除标记，TRUE表示已删除，FALSE表示正常，支持回收站功能',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '简历创建时间，自动记录',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '简历最后修改时间，每次更新自动刷新',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT='简历基本信息表，存储用户创建的每份简历的核心信息，一个用户只能创建一个简历基本信息';

##工作经历表
CREATE TABLE work_experiences (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '工作经历ID，主键，自增',
    resume_id INT NOT NULL COMMENT '所属简历ID，关联resumes表，标识该工作经历属于哪份简历',
    company_name VARCHAR(200) NOT NULL COMMENT '公司名称，显示任职企业的全称',
    position VARCHAR(200) NOT NULL COMMENT '职位名称，在该公司担任的具体职务',
    location VARCHAR(200) COMMENT '工作地点，所在城市或地区',
    start_date DATE NOT NULL COMMENT '入职日期，开始在该公司工作的日期',
    end_date DATE COMMENT '离职日期，为空表示至今仍在职',
    description TEXT COMMENT '工作描述，详细描述工作职责、工作内容和日常工作范围',
    achievements TEXT COMMENT '主要成就，列出工作中取得的关键成果、业绩数据和突出贡献',
    sort_order INT DEFAULT 0 COMMENT '排序序号，数值越小越靠前显示，用于自定义工作经历展示顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间，自动生成',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录最后修改时间，每次更新自动刷新',
    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
) COMMENT='工作经历表，存储用户每份简历中的工作履历信息，支持多段工作经历的排序展示';

##教育经历表
CREATE TABLE education (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '教育经历ID，主键，自增',
    resume_id INT NOT NULL COMMENT '所属简历ID，关联resumes表，标识该教育经历属于哪份简历',
    school_name VARCHAR(200) NOT NULL COMMENT '学校名称，就读院校的全称',
    degree VARCHAR(100) COMMENT '学位名称，如：学士、硕士、博士、MBA等',
    major VARCHAR(200) COMMENT '专业名称，主修专业全称，如：计算机科学与技术',
    gpa DECIMAL(3,2) COMMENT '平均学分绩点，格式为3.50（保留两位小数），如GPA较高可选择性展示',
    start_date DATE NOT NULL COMMENT '入学日期，开始在该学校就读的日期',
    end_date DATE COMMENT '毕业日期，为空表示至今仍在读',
    description TEXT COMMENT '教育描述，可填写主修课程、研究方向、论文题目等详细信息',
    achievements TEXT COMMENT '学术成就，列举在校期间获得的荣誉、奖项、奖学金等突出表现',
    sort_order INT DEFAULT 0 COMMENT '排序序号，数值越小越靠前显示，通常最高学历在前',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间，自动生成',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录最后修改时间，每次更新自动刷新',
    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
) COMMENT='教育经历表，存储用户每份简历中的教育背景信息，支持多段教育经历的排序展示';


##技能表
CREATE TABLE skills (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '技能ID，主键，自增',
    resume_id INT NOT NULL COMMENT '所属简历ID，关联resumes表，标识该技能属于哪份简历',
    skill_name VARCHAR(100) NOT NULL COMMENT '技能名称，如：Python、项目管理、数据分析、Photoshop等',
    proficiency_level ENUM('beginner', 'intermediate', 'advanced', 'expert') DEFAULT 'intermediate' COMMENT '技能熟练程度：beginner-入门（了解基础），intermediate-中级（可独立工作），advanced-高级（熟练掌握），expert-专家（精通并可指导他人）',
    category VARCHAR(50) COMMENT '技能分类，便于分组展示，如：编程语言、前端框架、数据库、设计工具、软技能、语言能力等',
    sort_order INT DEFAULT 0 COMMENT '排序序号，数值越小越靠前显示，用于自定义技能展示顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间，自动生成',
    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
) COMMENT='技能表，存储用户每份简历中的专业技能信息，支持分类展示和熟练度标识';

##项目经历表
CREATE TABLE projects (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '项目经历ID，主键，自增',
    resume_id INT NOT NULL COMMENT '所属简历ID，关联resumes表，标识该项目经历属于哪份简历',
    project_name VARCHAR(200) NOT NULL COMMENT '项目名称，展示项目的完整名称或简称',
    role VARCHAR(100) COMMENT '担任角色/职位，如：项目负责人、前端开发、全栈工程师、UI设计师等',
    project_url VARCHAR(500) COMMENT '项目链接，可填写GitHub仓库地址、线上演示地址或产品官网',
    start_date DATE COMMENT '项目开始日期，标识参与项目的起始时间',
    end_date DATE COMMENT '项目结束日期，为空表示项目仍在进行中',
    description TEXT COMMENT '项目描述，详细介绍项目背景、功能特点、解决的核心问题和技术难点',
    technologies_used TEXT COMMENT '使用的技术栈，列出项目中使用的技术、框架、工具等，如：React, Node.js, MySQL, Docker',
    achievements TEXT COMMENT '项目成果/亮点，描述项目取得的成果、个人贡献、性能提升、用户量等可量化指标',
    sort_order INT DEFAULT 0 COMMENT '排序序号，数值越小越靠前显示，用于自定义项目展示顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间，自动生成',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录最后修改时间，每次更新自动刷新',
    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
) COMMENT='项目经历表，存储用户每份简历中的项目经验信息，支持个人项目、工作项目和开源项目的展示';

##证书与奖项表
CREATE TABLE certifications (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '证书/奖项ID，主键，自增',
    resume_id INT NOT NULL COMMENT '所属简历ID，关联resumes表，标识该证书或奖项属于哪份简历',
    name VARCHAR(200) NOT NULL COMMENT '证书或奖项名称，如：PMP项目管理认证、AWS解决方案架构师、全国数学建模大赛一等奖',
    issuing_organization VARCHAR(200) COMMENT '颁发机构/主办单位，如：PMI（项目管理协会）、亚马逊云科技、教育部',
    issue_date DATE COMMENT '获得日期/颁发日期，证书或奖项的取得时间',
    expiry_date DATE COMMENT '有效期截止日期，为空表示永久有效，适用于有有效期的专业认证',
    credential_id VARCHAR(100) COMMENT '证书编号/认证ID，用于在线验证证书真伪的唯一标识符',
    credential_url VARCHAR(500) COMMENT '证书验证链接，提供在线查验证书真伪的官方网址',
    sort_order INT DEFAULT 0 COMMENT '排序序号，数值越小越靠前显示，通常重要的或最近获得的证书优先展示',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间，自动生成',
    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
) COMMENT='证书与奖项表，存储用户获得的专业认证、资格证书和荣誉奖项信息，支持有效期管理和在线验证';

##语言能力表
CREATE TABLE languages (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '语言能力ID，主键，自增',
    resume_id INT NOT NULL COMMENT '所属简历ID，关联resumes表，标识该语言能力属于哪份简历',
    language_name VARCHAR(50) NOT NULL COMMENT '语言名称，如：中文、英语、日语、法语、德语等',
    proficiency_level ENUM('basic', 'conversational', 'professional', 'native') DEFAULT 'professional' COMMENT '语言掌握程度：basic-基础（简单日常用语），conversational-会话（日常流利交流），professional-专业（商务工作应用），native-母语（母语水平）',
    sort_order INT DEFAULT 0 COMMENT '排序序号，数值越小越靠前显示，母语和熟练度高的语言优先展示',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间，自动生成',
    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
) COMMENT='语言能力表，存储用户掌握的语言及其熟练程度，支持多语言能力展示';

##简历模板表
CREATE TABLE resume_templates (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID，自增',
    name VARCHAR(100) NOT NULL COMMENT '模板名称',
    description TEXT COMMENT '模板描述',
    preview_image_url VARCHAR(500) COMMENT '预览图片URL',
    category ENUM('professional', 'creative', 'modern', 'minimalist', 'academic')
        DEFAULT 'professional' COMMENT '模板分类：professional-专业, creative-创意, modern-现代, minimalist-极简, academic-学术',
    is_premium BOOLEAN DEFAULT FALSE COMMENT '是否付费模板（FALSE-免费, TRUE-付费）',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用（FALSE-禁用, TRUE-启用）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT='简历模板表';

##自定义模块表
CREATE TABLE custom_sections (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '自定义模块ID，主键，自增',
    resume_id INT NOT NULL COMMENT '所属简历ID，关联resumes表，标识该自定义模块属于哪份简历',
    section_title VARCHAR(200) NOT NULL COMMENT '自定义模块标题，如：志愿服务、兴趣爱好、发表论文、培训经历等',
    content TEXT COMMENT '自定义模块内容，支持富文本或纯文本，灵活填写任意补充信息',
    sort_order INT DEFAULT 0 COMMENT '排序序号，数值越小越靠前显示，用于控制自定义模块在简历中的展示顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间，自动生成',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录最后修改时间，每次更新自动刷新',
    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
) COMMENT='自定义模块表，允许用户根据个人需求灵活添加简历中标准模块之外的补充信息模块';

##简历分享记录表
CREATE TABLE resume_shares (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '分享记录ID，主键，自增',
    resume_id INT NOT NULL COMMENT '被分享的简历ID，关联resumes表',
    share_token VARCHAR(64) UNIQUE NOT NULL COMMENT '分享令牌，随机生成的唯一字符串，用于构建分享链接，防止被猜测',
    expires_at TIMESTAMP COMMENT '分享过期时间，为空表示永久有效，设置后超时自动失效',
    view_count INT DEFAULT 0 COMMENT '累计浏览次数，记录该分享链接被访问查看的次数',
    is_active BOOLEAN DEFAULT TRUE COMMENT '分享状态标记，TRUE为有效，FALSE为已手动关闭或过期失效',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '分享创建时间，自动生成',
    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
) COMMENT='简历分享记录表，用于生成和管理简历的对外分享链接，支持访问统计和时效控制';
